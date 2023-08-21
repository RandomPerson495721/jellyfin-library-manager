package dev.partin.james.jellyfinlibrarymanager.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class Job {

    private String fileName;

    private String filepath;

    @OneToOne(cascade = CascadeType.ALL)
    private jobStatus uploadStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobStatus transcodeStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobStatus subtitlesStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobStatus metadataStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobStatus cleanupStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @PostPersist
    private void generateFilePath() {
        this.filepath = System.getProperty("java.io.tmpdir") + "/jellyfin-library-manager/" + fileName.substring(0, fileName.lastIndexOf('.')) + "[JobID=" + this.id + "]/";
    }

    public Job() {
        this.uploadStatus = new jobStatus("Upload");
        this.transcodeStatus = new jobStatus("Transcode");
        this.subtitlesStatus = new jobStatus("Subtitles");
        this.metadataStatus = new jobStatus("Metadata");
        this.cleanupStatus = new jobStatus("Cleanup");
    }

    public Job(String fileName) {
        this.fileName = fileName;
        this.uploadStatus = new jobStatus("Upload");
        this.transcodeStatus = new jobStatus("Transcode");
        this.subtitlesStatus = new jobStatus("Subtitles");
        this.metadataStatus = new jobStatus("Metadata");
        this.cleanupStatus = new jobStatus("Cleanup");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

