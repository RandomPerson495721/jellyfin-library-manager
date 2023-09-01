package dev.partin.james.jellyfinlibrarymanager.service;

import dev.partin.james.jellyfinlibrarymanager.api.model.JobDefinition;
import dev.partin.james.jellyfinlibrarymanager.repositories.JobDefinitionRepository;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public interface IJobService {

    Pair<Integer, String> upload(FileItemInput fileItemInput, long fileSize, Optional<Long> byteOffsetOptional) throws SQLException;
}
