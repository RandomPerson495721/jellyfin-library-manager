package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import dev.partin.james.jellyfinlibrarymanager.repository.MediaConvertJobRepository;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class JobService implements IJobService {

    private final MediaConvertJobRepository jobRepository;
    private final Logger logger;

    @Autowired
    public JobService(MediaConvertJobRepository jobRepository, Logger logger) {
        this.jobRepository = jobRepository;
        this.logger = logger;
    }

    @Override
    public Job createJob(String fileName) {
        var job = new Job(fileName);
        jobRepository.save(job);
        return job;
    }

    @Override
    public ResponseEntity<String> upload(FileItemInput fileItemInput, long fileSize, Optional<Long> byteOffsetOptional){
        Job job = createJob(fileItemInput.getName());
        try {
            InputStream inputStream = fileItemInput.getInputStream();
            long byteOffset = byteOffsetOptional.orElse(0L);
            int chunkSize = 1024 * 1024 * 10;
            byte[] chunk = new byte[chunkSize];
            File fileDirectory = new File(job.getFilepath());
            if (!fileDirectory.exists() && !fileDirectory.mkdirs()) {
                throw new IOException("Failed to create directory");
            }
            File file = new File(fileDirectory, job.getFileName());

            if (!file.exists() && !file.createNewFile()) {
                    throw new IOException("Failed to create file");
            } else if (file.length() != byteOffset && byteOffset != 0) {
                throw new IOException("File already exists and is not the correct size");
            } else if (file.length() == fileSize) {
                throw new IOException("File already exists and is the correct size");
            } else if(file.length() > fileSize) {
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
            jobRepository.save(job);

            return ResponseEntity.ok("Upload complete");
        } catch (Exception e) {
            //TODO: Better error handling and logging
            job.getUploadStatus().setFailed(true);
            logger.log(java.util.logging.Level.SEVERE, "Error uploading file, stack trace:" + e.getStackTrace());
            jobRepository.save(job);
            throw new RuntimeException(e);
            //return ResponseEntity.internalServerError().body("Error uploading file");
        }
    }

}
