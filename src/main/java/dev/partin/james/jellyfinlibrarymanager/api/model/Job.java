package dev.partin.james.jellyfinlibrarymanager.api.model;

import java.util.*;

import lombok.Getter;
import org.javatuples.*;

@Getter
public class Job {
    private final String fileName;
    //The tuple represents whether the job is finished and whether it failed
    private Map<String, Pair<Boolean, Boolean>> jobStatus;

    public Job(String fileName) {
        this.fileName = fileName;
        this.jobStatus = Map.of(
            "upload", new Pair<Boolean, Boolean>(false, false),
            "transcode", new Pair<Boolean, Boolean>(false, false),
            "subtitles", new Pair<Boolean, Boolean>(false, false),
            "metadata", new Pair<Boolean, Boolean>(false, false),
            "cleanup", new Pair<Boolean, Boolean>(false, false)
        );
    }

    public void setJobStatus(String stepName, Pair<Boolean, Boolean> successAndFailure) {
        jobStatus.put(stepName, successAndFailure);
    }
}
