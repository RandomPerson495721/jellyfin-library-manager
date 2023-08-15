package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import dev.partin.james.jellyfinlibrarymanager.api.model.jobStatus;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Map;

@Component
public interface IJobService {
    void createJob(String fileName) throws SQLException;
    void upload(FileItemInput inputStream) throws SQLException;

    void upload(FileItemInput inputStream, Job job) throws SQLException;

}
