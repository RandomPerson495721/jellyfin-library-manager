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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
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
        var upload = new JakartaServletFileUpload();
        var iterator = upload.getItemIterator(request);
        var a = new StringBuilder();

        byte[] chunk = new byte[0];
        int chunkSize = 1024 * 1024 * 10;
        long index = 0;
        int bytesRead = 0;
        long totalBytesRead = 0;
        while (iterator.hasNext()) {
            var item = iterator.next();
            var name = item.getFieldName();
            InputStream inputStream = item.getInputStream();

            index = 0;
            chunk = new byte[chunkSize];

            while (true) {
                bytesRead = inputStream.read(chunk, 0, chunkSize);
                totalBytesRead += Math.max(bytesRead, 0);
                if (bytesRead == -1) {
                    chunk = new byte[0];
                    break;
                }

                //   hashes[(int) index] = Integer.toString(Arrays.hashCode(chunk));


//                int i = 0;
//                for (byte b : chunk) {
//                    if ((int) b == 0) {
//                        if (i == 0) {
//                            i = 1;
//                        }
//                        var tempchunk = new byte[i];
//                          System.arraycopy(chunk, 0, tempchunk, 0, i);
//                        chunk = tempchunk;
//                        break;
//                    }
//                    i++;
//                }
                index++;


            }
        }
        return ResponseEntity.ok(" Last Chunk Size: " + chunk.length + " Chunk: " + Arrays.toString(chunk) + "last input stream buffer size: " + bytesRead + " total bytes read: " + totalBytesRead);
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
