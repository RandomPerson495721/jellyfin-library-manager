package dev.partin.james.jellyfinlibrarymanager.helpers;

import dev.partin.james.jellyfinlibrarymanager.api.model.JobDefinition;
import lombok.Getter;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.File;
import java.io.IOException;

@Getter
public class AudioTranscodeJobBuilder {
    private TranscodeConfiguration transcodeConfiguration;
    private File inputFile;
    private long jobID;
    private File outputFile;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private FFmpegOutputBuilder ffmpegOutputBuilder;
    private FFmpegBuilder ffmpegBuilder;

    public AudioTranscodeJobBuilder(TranscodeConfiguration transcodeConfiguration, JobDefinition jobDefinition) throws IOException {
        this.transcodeConfiguration = transcodeConfiguration;
        this.ffmpeg = new FFmpeg();
        this.ffprobe = new FFprobe();
        this.inputFile = jobDefinition.getFile();
        this.jobID = jobDefinition.getId();
        this.outputFile = new File(inputFile.getParent() + "/VideoElement/" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + "[JobID=" + jobID + "].mp4");
        FFmpegProbeResult mediaInformation = ffprobe.probe(inputFile.getAbsolutePath());

    }



    public FFmpegJob build() throws IOException {
        FFmpegProbeResult probeResult = ffprobe.probe("input.mp4");
        FFmpegStream stream = probeResult.getStreams().get(0);
        this.ffmpegOutputBuilder = new FFmpegOutputBuilder()
                .setFilename(outputFile.getAbsolutePath())
                .disableAudio()
                .disableSubtitle()
                .setVideoCodec(transcodeConfiguration.getAudio_codec());

        switch (transcodeConfiguration.getCodec()) {
            case "libx264", "libx265", "libvpx-vp9", "libaom-av1", "av1-svt":
                if (transcodeConfiguration.getCrf() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getCrf());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                break;

            case "h264_nvenc", "hevc_nvenc":
                if (transcodeConfiguration.getRf_nvenc() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getRf_nvenc());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                ffmpegOutputBuilder.addExtraArgs("-hwaccel", "cuvid");
                break;

            case "h264_qsv", "hevc_qsv", "vp9_qsv", "av1_qsv":
                if (transcodeConfiguration.getQv_QSV() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getQv_QSV());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                ffmpegOutputBuilder.addExtraArgs("-hwaccel", "qsv");
            case "copy":
                ffmpegOutputBuilder.setVideoCodec("copy");


        }
        ffmpegBuilder = new FFmpegBuilder().addOutput(ffmpegOutputBuilder);
        ffmpegBuilder.setInput(inputFile.getAbsolutePath())
                .overrideOutputFiles(true);
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        if (transcodeConfiguration.isTwo_pass()) {
            return executor.createTwoPassJob(ffmpegBuilder);
        } else {
            return executor.createJob(ffmpegBuilder);
        }
    }

    private boolean getTeceline() {
        //TODO: Implement this
        return false;
    }
}
