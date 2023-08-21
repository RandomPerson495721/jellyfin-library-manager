package dev.partin.james.jellyfinlibrarymanager.api.controller;

import dev.partin.james.jellyfinlibrarymanager.api.model.Job;
import dev.partin.james.jellyfinlibrarymanager.service.IJobService;
import dev.partin.james.jellyfinlibrarymanager.service.JobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
public class JobController {
    private IJobService jobService;

    @Autowired
    public JobController(IJobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/testtwo")
    public ResponseEntity<String> test() throws SQLException {
        jobService.createJob("test");
        return ResponseEntity.ok("Test");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(HttpServletRequest request) throws IOException, ServletException, InterruptedException {
        boolean isMultiPart = JakartaServletFileUpload.isMultipartContent(request);
        //long fileSize = Long.parseLong(request.getParameter("Content-Length"));
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);
        var a = new StringBuilder();

        int bytesRead = 0;
        long totalBytesRead = 0;
        ResponseEntity<String> response = null;
        while (iterator.hasNext()) {
            var item = iterator.next();
            response = jobService.upload(item, 44884, Optional.empty());
        }
        return response;
    }

    @PostMapping("/upload/restart")
    public ResponseEntity<String> restartUpload() {
        return ResponseEntity.ok("Not implemented");
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
