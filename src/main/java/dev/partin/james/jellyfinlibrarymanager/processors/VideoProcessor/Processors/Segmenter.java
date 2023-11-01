package dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.Processors;

import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.VideoProcessorChain;
import dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor.VideoProcessorRequest;

public class Segmenter extends VideoProcessorChain {

    @Override
    public void process(VideoProcessorRequest request) {
        var input = request.getInput();
        var output = request.getOutput();
        //TODO: The properties of input and output should be set prior to the chain being built.
        int segmentCount = (int) Math.ceil(input.getLengthSeconds() / input.getSegmentLengthSeconds());
        for (int i = 0; i < segmentCount; i++) {
            var segment = new JPAFile("TEMP FILE PATH" + "WHERE VIDEO RESOURCE FILES ARE STORED" + "Segments", "seg_" + input.getId() + "_" + i + input.getFileExtension());
            //Calculate the start and end times for the segment, ensuring that the end time is not greater than the length of the video
            segment.setTime(i * input.getSegmentLengthSeconds(), (i + 1) * input.getSegmentLengthSeconds() > input.getLengthSeconds() ? input.getLengthSeconds() : (i + 1) * input.getSegmentLengthSeconds());
            input.addSegment(segment);
        }
        next(request);
    }
}
