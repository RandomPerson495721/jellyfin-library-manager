package dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor;

import dev.partin.james.jellyfinlibrarymanager.api.model.resources.VideoResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class VideoProcessorRequest {

    @OneToOne(cascade = CascadeType.ALL)
    private VideoResource input;

    @OneToOne(cascade = CascadeType.ALL)
    private VideoResource output;

    //Add a universal configuration object here, unfortunately, it likely has to be specific to the instance
    //as it will be used to configure the ffmpeg command line arguments, any changes to the configuration
    //would break any in progress jobs.

    private JobProgress progress = JobProgress.NOT_STARTED;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public VideoProcessorRequest(VideoResource input, VideoResource output) {
        this.input = input;
        this.output = output;
    }

    public VideoProcessorRequest() {
    }

}
