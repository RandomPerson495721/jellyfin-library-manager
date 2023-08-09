package dev.partin.james.jellyfinlibrarymanager.api.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;

@RestController("/api/job")
public class JobController {
    @PostMapping("/upload")
    //this should take an input stream from apache commons file upload
    public ResponseEntity<String> upload(HttpServletRequest request) throws IOException, ServletException {
        boolean isMultiPart = JakartaServletFileUpload.isMultipartContent(request);
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);
        byte[] bytes = new byte[0];
        while (iterator.hasNext()) {
            bytes = iterator.next().getInputStream().readNBytes(100);
            System.out.println(Arrays.toString(bytes));
        }
        return ResponseEntity.ok(Arrays.toString(bytes));
    }

    @PostMapping("/upload/restart")
    public ResponseEntity<String> restartUpload() {
        return ResponseEntity.ok("Not implemented");
    }

    @GetMapping("/upload/status")
    public ResponseEntity<String> uploadStatus() {
        return ResponseEntity.ok("Not implemented");
    }
    //This should return the processing status of the job
    @GetMapping("/processing/status")
    public ResponseEntity<String> jobProcessingStatus() {
        return ResponseEntity.ok("Not implemented");
    }

}
