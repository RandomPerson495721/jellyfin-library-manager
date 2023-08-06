package dev.partin.james.jellyfinlibrarymanager.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/job")
public class JobController {

    @PostMapping("/upload/start")
    public void upload() {

    }

    @PostMapping("/upload/restart")
    public void restartUpload() {

    }

    @GetMapping("/upload/status")
    public void uploadStatus() {

    }
    //This should return the processing status of the job
    @GetMapping("/job/processing/status")
    public void jobProcessingStatus() {

    }

}
