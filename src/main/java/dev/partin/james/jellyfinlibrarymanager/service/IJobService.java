package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public interface IJobService {
    void createJob(String fileName) throws SQLException;

    void upload(FileItemInput inputStream) throws SQLException;

    void upload(FileItemInput inputStream, Job job) throws SQLException;

}
