package dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.Processors;

import dev.partin.james.jellyfinlibrarymanager.api.model.resources.IResource;
import dev.partin.james.jellyfinlibrarymanager.FFmpegAbstractions.ResourceAnalyzers.*;
import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.VideoProcessorChain;
import dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.VideoProcessorRequest;

public class segmenter extends VideoProcessorChain {

    @Override
    public void process(VideoProcessorRequest request) {
        var input = request.getInput();
        var output = request.getOutput();
        //TODO: The properties of input and output should be set prior to the chain being built.
        double segmentLength = output.getSegmentLengthSeconds();
        int segmentCount = (int) Math.ceil(input.getLengthSeconds() / input.getSegmentLengthSeconds());
        for (int i = 0; i < segmentCount; i++) {

            var segment = new JPAFile();
            input.addSegment(segment);
        }
    }
}
