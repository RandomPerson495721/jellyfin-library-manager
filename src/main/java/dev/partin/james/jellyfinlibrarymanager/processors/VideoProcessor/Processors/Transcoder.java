package dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.Processors;

import dev.partin.james.jellyfinlibrarymanager.api.model.resources.VideoResource;
import dev.partin.james.jellyfinlibrarymanager.helpers.VideoTranscodeJobBuilder;
import dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.VideoProcessorChain;
import dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.VideoProcessorRequest;
import net.bramp.ffmpeg.FFmpeg;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Transcoder extends VideoProcessorChain {
    FFmpeg ffmpeg;
    Executor executor;


    public Transcoder() throws IOException {
        ffmpeg = new FFmpeg();
        //TODO: Make this configurable
        executor = Executors.newFixedThreadPool(4);
    }


    @Override
    public void process(VideoProcessorRequest request) {
        VideoResource input = request.getInput();
        VideoResource output = request.getOutput();
        var segments = input.getSegments();
        for (var segment : segments) {
            //Use VideoTranscodeJobBuilder to build the command

            if (!segment.isLeased()) {
                var videoTranscodeJobBuilder = new VideoTranscodeJobBuilder(input, ffmpeg);
                segment.acquireLease(this);
                var command =
            }
        }
        next(request);
    }
}
