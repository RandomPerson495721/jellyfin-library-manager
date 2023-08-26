package dev.partin.james.jellyfinlibrarymanager.api.controller;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import dev.partin.james.jellyfinlibrarymanager.service.IJobService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@RestController
public class JobController {
    private IJobService jobService;

    @Autowired
    public JobController(IJobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(HttpServletRequest request) throws IOException, ServletException, InterruptedException {
        long fileSize = Long.parseLong(request.getHeader("content-length"));
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);

        ResponseEntity<String> response = null;
        while (iterator.hasNext()) {
            var item = iterator.next();
            var responseUnformatted = jobService.upload(item, fileSize, Optional.empty());
            response = ResponseEntity.status(responseUnformatted.getValue0()).body(responseUnformatted.getValue1());
        }
        return response;
    }

    @PostMapping("/upload/test2")
    public ResponseEntity<String> uploadTest2(@RequestParam String codecName) throws FFmpegException {
        var name = FFmpeg.getCodecByName(codecName);
        return ResponseEntity.ok(name.toString());
    }

    @PostMapping("/upload/restart")
    public ResponseEntity<String> restartUpload(HttpServletRequest request) throws IOException, ServletException, InterruptedException {
        long fileSize = Long.parseLong(request.getHeader("content-length"));
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);
        long byteOffset = Long.parseLong(request.getHeader("byte-offset"));

        ResponseEntity<String> response = null;
        while (iterator.hasNext()) {
            var item = iterator.next();
            var responseUnformatted = jobService.upload(item, fileSize, Optional.of(byteOffset));
            response = ResponseEntity.status(responseUnformatted.getValue0()).body(responseUnformatted.getValue1());
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
