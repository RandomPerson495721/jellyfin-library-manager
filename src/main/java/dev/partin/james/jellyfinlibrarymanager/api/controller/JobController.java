package dev.partin.james.jellyfinlibrarymanager.api.controller;

import dev.partin.james.jellyfinlibrarymanager.service.IJobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.ErrorManager;

@RestController
public class JobController {
    private IJobService jobService;
    private final Logger logger = LoggerFactory.getLogger(JobController.class);

    @Autowired
    public JobController(IJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(HttpServletRequest request) throws IOException, SQLException {
        long fileSize = Long.parseLong(request.getHeader("content-length"));
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);
        try {
            while (iterator.hasNext()) {
                var item = iterator.next();
                jobService.upload(item.getInputStream(), item.getName(), fileSize, Optional.empty());
            }
            logger.info("Upload successful");
            return ResponseEntity.ok("Upload successful");
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    //TODO: Implement security to prevent unauthorized access to this endpoint
    //@PostMapping("/upload/restart")
    public ResponseEntity<String> restartUpload(HttpServletRequest request) throws IOException, ServletException, InterruptedException, SQLException {
        long fileSize = Long.parseLong(request.getHeader("content-length"));
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);

        long byteOffset = Long.parseLong(request.getHeader("byte-offset"));

        ResponseEntity<String> response = null;
        while (iterator.hasNext()) {
            var item = iterator.next();
        }
        return response;
    }

    @GetMapping("/upload/status")
    public ResponseEntity<String> uploadStatus() {
        return ResponseEntity.ok("Not implemented");
    }

    @GetMapping("/processing/status")
    public ResponseEntity<String> jobProcessingStatus() {
        return ResponseEntity.ok("Not implemented");
    }

}
