package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.JobDefinition;
import dev.partin.james.jellyfinlibrarymanager.repositories.JobDefinitionRepository;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.javatuples.Pair;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.SeekableByteChannel;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class JobService implements IJobService {

    private final JobDefinitionRepository jobDefinitionRepository;
    private final Logger logger;

    @Autowired
    public JobService(JobDefinitionRepository jobDefinitionRepository, Logger logger) {
        this.jobDefinitionRepository = jobDefinitionRepository;
        this.logger = logger;
    }

    @Override
    public Pair<Integer, String> upload(FileItemInput fileItemInput, long fileSize, Optional<Long> byteOffsetOptional) throws SQLException {
        //TODO: fix naming scheme
        JobDefinition job = new JobDefinition(fileItemInput.getName());
        jobDefinitionRepository.save(job);
        try {
            InputStream inputStream = fileItemInput.getInputStream();
            long byteOffset = byteOffsetOptional.orElse(0L);
            int chunkSize = 1024 * 1024 * 10;
            byte[] chunk = new byte[chunkSize];
            File file = job.getFile();

            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create file");
            } else if (file.length() != byteOffset && byteOffset != 0) {
                throw new IOException("File already exists and is not the correct size");
            } else if (file.length() == fileSize) {
                throw new IOException("File already exists and is the correct size");
            } else if (file.length() > fileSize) {
                throw new IOException("File already exists and is larger than the correct size");
            } else if (!file.canWrite()) {
                throw new IOException("File is not writable");
            }
            long totalBytesRead = 0;
            long bytesRead = 0;

            try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
                while (true) {
                    bytesRead = inputStream.read(chunk, 0, chunkSize);
                    totalBytesRead += Math.max(bytesRead, 0);

                    if (bytesRead == -1) {
                        break;
                    }
                    fileOutputStream.write(chunk, 0, (int) bytesRead);
                    job.getUploadStatus().setProgress(((float) totalBytesRead / (float) fileSize) * 100);
                }
            }

            job.getUploadStatus().setFinished(true);
            jobDefinitionRepository.save(job);
            return new Pair<>(200, "Upload complete");
        } catch (Exception e) {
            //TODO: Better error handling and logging
            job.getUploadStatus().setFailed(true);
            logger.log(java.util.logging.Level.SEVERE, "Error uploading file, stack trace:" );
            Arrays.stream(e.getStackTrace()).forEach(stackTraceElement -> logger.log(java.util.logging.Level.SEVERE, stackTraceElement.toString()));
            jobDefinitionRepository.save(job);
            //throw new RuntimeException(e);
            return new Pair<>(500, "Upload failed");
        }


    }




}
