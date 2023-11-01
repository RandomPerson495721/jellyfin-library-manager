package dev.partin.james.jellyfinlibrarymanager.helpers;

import dev.partin.james.jellyfinlibrarymanager.api.model.resources.VideoResource;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.job.FFmpegJob;

import java.io.IOException;
import java.util.Date;

public class VideoTranscodeJobBuilder {
    VideoResource input;
    FFmpeg ffmpeg;
    FFmpegExecutor executor;

    public VideoTranscodeJobBuilder(VideoResource resource, FFmpeg ffmpeg) throws IOException {
        input = resource;
        this.ffmpeg = ffmpeg;
        executor = new FFmpegExecutor(ffmpeg);
    }

    private void split() {
        for (var segment : input.getSegments()) {
            Date leaseExpiration;
            if (!segment.isLeased()) {
                leaseExpiration = segment.acquireLease(this);
                JPAFile newFile = new JPAFile(segment.get().getAbsolutePath() +
                        ".tmp_" +
                        segment.getId() +
                        segment.get().getPath().substring(segment.get().getPath().lastIndexOf('.')));
                var command = ffmpeg.builder();
                command.addInput(segment.get().getAbsolutePath());
                command.addOutput(newFile.get().getAbsolutePath());
                command.addExtraArgs("-ss", String.valueOf(segment.getStartTime()));
                command.addExtraArgs("-to", String.valueOf(segment.getEndTime()));
                command.addExtraArgs("-c", "copy");
                try {
                    executor.createJob(command).run();
                    segment.releaseLease();
                } catch (Exception e) {
                    segment.releaseLease();
                    //TODO: replace with something more meaningful
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private FFmpegJob build() {
        return null;
    }


}
