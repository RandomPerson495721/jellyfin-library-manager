package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import dev.partin.james.jellyfinlibrarymanager.repository.MediaConvertJobRepository;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class JobService implements IJobService {

    private final MediaConvertJobRepository jobRepository;

    @Autowired
    public JobService(MediaConvertJobRepository jobRepository) {
        this.jobRepository = jobRepository;

    }

    @Override
    public void createJob(String fileName) throws SQLException {
        var job = new Job(fileName);
        jobRepository.save(job);
    }

    @Override
    public void upload(FileItemInput inputStream) throws SQLException {
        createJob(inputStream.getName());
    }

    @Override
    public void upload(FileItemInput inputStream, Job job) throws SQLException {
        createJob(inputStream.getName());
    }


}
