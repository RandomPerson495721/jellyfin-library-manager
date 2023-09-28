package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.JobDefinition;
import dev.partin.james.jellyfinlibrarymanager.repositories.JobDefinitionRepository;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Input;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class JobService implements IJobService {

    private final JobDefinitionRepository jobDefinitionRepository;
    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    public JobService(JobDefinitionRepository jobDefinitionRepository) {
        this.jobDefinitionRepository = jobDefinitionRepository;
    }

    @Override
    public void upload(InputStream fileStream, String fileName, long fileSize, Optional<Long> byteOffsetOptional) throws IOException {
        //TODO: fix naming scheme
        JobDefinition job = new JobDefinition(fileName);
        jobDefinitionRepository.save(job);
        long byteOffset = byteOffsetOptional.orElse(0L);
        int chunkSize = 1024 * 1024 * 10;

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
        long bytesRead;
        byte[] chunk = new byte[chunkSize];
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            while ((bytesRead = fileStream.read(chunk, 0, chunkSize)) > -1) {
                totalBytesRead += bytesRead;
                fileOutputStream.write(chunk, 0, (int) bytesRead);
                //TODO: Implement new progress system
                job.getUploadStatus().setProgress(((float) totalBytesRead / (float) fileSize) * 100);
            }
        }
        job.getUploadStatus().setFinished(true);
        jobDefinitionRepository.save(job);
    }
}
