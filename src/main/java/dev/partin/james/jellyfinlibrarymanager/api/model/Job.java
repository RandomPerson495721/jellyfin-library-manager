package dev.partin.james.jellyfinlibrarymanager.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
public class Job {

    private String fileName;

    private String filepath;

    @OneToOne
    private jobStatus uploadStatus;

    @OneToOne
    private jobStatus transcodeStatus;

    @OneToOne
    private jobStatus subtitlesStatus;

    @OneToOne
    private jobStatus metadataStatus;

    @OneToOne
    private jobStatus cleanupStatus;


    @Id
    private Long id;

    public Job(){
        this.uploadStatus = new jobStatus("Upload");
        this.transcodeStatus = new jobStatus("Transcode");
        this.subtitlesStatus = new jobStatus("Subtitles");
        this.metadataStatus = new jobStatus("Metadata");
        this.cleanupStatus = new jobStatus("Cleanup");
    }

    public Job(String fileName) {
        this.fileName = fileName;
        this.filepath = System.getProperty("java.io.tmpdir") + fileName;
        this.uploadStatus = new jobStatus("Upload");
        this.transcodeStatus = new jobStatus("Transcode");
        this.subtitlesStatus = new jobStatus("Subtitles");
        this.metadataStatus = new jobStatus("Metadata");
        this.cleanupStatus = new jobStatus("Cleanup");

    }

    public void setfileName(String fileName) {
        this.fileName = fileName;
        this.filepath = System.getProperty("java.io.tmpdir") + fileName;
    }
}

