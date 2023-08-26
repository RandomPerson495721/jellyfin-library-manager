package dev.partin.james.jellyfinlibrarymanager.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class JobDefinition {

    private String fileName;

    private String filepath;

    @OneToOne(cascade = CascadeType.ALL)
    private jobDefinitionStatus uploadStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobDefinitionStatus transcodeStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobDefinitionStatus subtitlesStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobDefinitionStatus metadataStatus;

    @OneToOne(cascade = CascadeType.ALL)
    private jobDefinitionStatus cleanupStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @PostPersist
    private void generateFilePath() {
        this.filepath = System.getProperty("java.io.tmpdir") + "/jellyfin-library-manager/" + fileName.substring(0, fileName.lastIndexOf('.')) + "[JobID=" + this.id + "]/";
    }

    public JobDefinition() {
        this.uploadStatus = new jobDefinitionStatus("Upload");
        this.transcodeStatus = new jobDefinitionStatus("Transcode");
        this.subtitlesStatus = new jobDefinitionStatus("Subtitles");
        this.metadataStatus = new jobDefinitionStatus("Metadata");
        this.cleanupStatus = new jobDefinitionStatus("Cleanup");
    }

    public JobDefinition(String fileName) {
        this.fileName = fileName;
        this.uploadStatus = new jobDefinitionStatus("Upload");
        this.transcodeStatus = new jobDefinitionStatus("Transcode");
        this.subtitlesStatus = new jobDefinitionStatus("Subtitles");
        this.metadataStatus = new jobDefinitionStatus("Metadata");
        this.cleanupStatus = new jobDefinitionStatus("Cleanup");
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

