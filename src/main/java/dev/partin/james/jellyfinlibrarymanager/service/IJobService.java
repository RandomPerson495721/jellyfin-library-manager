package dev.partin.james.jellyfinlibrarymanager.service;

import org.apache.commons.fileupload2.core.FileItemInput;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Optional;

@Component
public interface IJobService {

    void upload(InputStream fileStream, String fileName, long fileSize, Optional<Long> byteOffsetOptional) throws SQLException, IOException;
}
