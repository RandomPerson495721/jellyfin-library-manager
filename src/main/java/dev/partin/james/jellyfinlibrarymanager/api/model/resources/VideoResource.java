package dev.partin.james.jellyfinlibrarymanager.api.model.resources;

import dev.partin.james.jellyfinlibrarymanager.FFmpegAbstractions.ResourceAnalyzers.VideoResourceAnalyzer;
import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import dev.partin.james.jellyfinlibrarymanager.FFmpegAbstractions.ResourceAnalyzers.VideoCodec;
import java.util.TreeMap;
@Getter
@Setter
@Entity
public class VideoResource extends IResource {

    private int width = -1;
    private int height = -1;
    private int framerate = -1;
    private int bitrate = -1;
    private VideoCodec codec;
    private int Crf = -1;
    private int nvCq = -1;
    private boolean isInterlaced = false;
    private int qsvCq = -1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    public VideoResource(JPAFile file) {
        super(file);
    }

    public VideoResource() {
        super();
    }

}

