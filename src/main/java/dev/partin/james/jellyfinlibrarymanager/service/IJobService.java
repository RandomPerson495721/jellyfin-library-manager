package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public interface IJobService {
    Job createJob(String fileName) throws SQLException;
    ResponseEntity<String> upload(FileItemInput fileItemInput, long fileSize, Optional<Long> byteOffset);

}
