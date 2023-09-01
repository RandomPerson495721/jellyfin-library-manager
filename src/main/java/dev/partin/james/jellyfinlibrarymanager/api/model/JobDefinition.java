package dev.partin.james.jellyfinlibrarymanager.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.File;
import java.io.IOException;

@Entity
@Getter
@Setter
public class JobDefinition {

    private String fileName;

    private String filepath;

    private boolean testMode = false;

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
    private void generateFilePath() throws IOException {
        this.filepath = System.getProperty("java.io.tmpdir") + "jellyfin-library-manager/" + fileName.substring(0, fileName.lastIndexOf('.')) + "[JobID=" + this.id + "]/";
        File fileDirectory = new File(filepath);
        File transcodeDirectory = new File(filepath + "VideoElement/");
        if ((!fileDirectory.exists() && !fileDirectory.mkdirs() && !fileDirectory.createNewFile()) && (!transcodeDirectory.exists() && !transcodeDirectory.mkdirs())) {
            throw new IOException("Failed to create directory");
        }
    }

    public JobDefinition() {
        this((String) null);
    }

    public JobDefinition(String fileName) {
        this.fileName = fileName;
        this.uploadStatus = new jobDefinitionStatus("Upload");
        this.transcodeStatus = new jobDefinitionStatus("Transcode");
        this.subtitlesStatus = new jobDefinitionStatus("Subtitles");
        this.metadataStatus = new jobDefinitionStatus("Metadata");
        this.cleanupStatus = new jobDefinitionStatus("Cleanup");
    }

    public JobDefinition(File file) {
        this(file.getName());
    }

    public File getFile() {
        return new File(filepath + fileName);
    }
}

