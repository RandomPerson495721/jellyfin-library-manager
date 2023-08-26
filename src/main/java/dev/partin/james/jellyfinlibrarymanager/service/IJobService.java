package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.JobDefinition;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public interface IJobService {
    JobDefinition createJobDefinition(String fileName) throws SQLException;

    Pair<Integer, String> upload(FileItemInput fileItemInput, long fileSize, Optional<Long> byteOffsetOptional);
}
