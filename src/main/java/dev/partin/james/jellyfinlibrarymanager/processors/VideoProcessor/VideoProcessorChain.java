package dev.partin.james.jellyfinlibrarymanager.processors.VideoProcessor;

import dev.partin.james.jellyfinlibrarymanager.api.model.resources.VideoResource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class VideoProcessorChain {
    protected Logger logger = LoggerFactory.getLogger(VideoProcessorChain.class);
    private VideoProcessorChain next;
    //TODO: Autowire a universal configuration object and place it in the VideoProcessorRequest
    public VideoProcessorChain linkWith(VideoProcessorChain next) {
        this.next = next;
        return next;
    }

    public abstract void process(VideoProcessorRequest request);

    protected void next(VideoProcessorRequest request) {
        try {
            if (next != null) {
                //TODO: Add thread reporting to the VideoProcessorRequest logging.
                logger.info("The " + this.getClass() + " step has finished. Moving to the " + next.getClass() + " step.");
                next.process(request);
            } else {
                logger.info("VideoProcessorChain has finished.");
            }
        } catch (Exception e) {
            logger.error("Error in VideoProcessorChain at the " + this.getClass() + " step.", e);
        }
    }
}

