package dev.partin.james.jellyfinlibrarymanager.helpers;

import lombok.Getter;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.google.common.math.IntMath.gcd;
import static org.apache.commons.lang3.ObjectUtils.mode;

@Getter
public class VideoTranscodeJobBuilder {
    private TranscodeConfiguration transcodeConfiguration;
    private File inputFile;
    private long jobID;
    private File outputFile;
    private FFmpeg ffmpeg;
    private FFprobe ffprobe;
    private boolean teceline = false;
    private boolean interlace = false;
    private int[] trueResolution;
    private int[] trueAspectRatio;
    private int[] inputResolution;
    private long inputBitrate;
    private float scale;
    private boolean upscale;
    private FFmpegOutputBuilder ffmpegOutputBuilder;
    private FFmpegBuilder ffmpegBuilder;

    public VideoTranscodeJobBuilder(TranscodeConfiguration transcodeConfiguration, File inputFile, long jobID) throws IOException {
        this.transcodeConfiguration = transcodeConfiguration;
        this.ffmpeg = new FFmpeg();
        this.ffprobe = new FFprobe();
        this.inputFile = inputFile;
        this.jobID = jobID;
        this.outputFile = new File(inputFile.getParent() + "/VideoElement/" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + "[JobID=" + jobID + "].mp4");
        FFmpegProbeResult mediaInformation = ffprobe.probe(inputFile.getAbsolutePath());
        this.inputBitrate = mediaInformation.getFormat().bit_rate;
        this.inputResolution = new int[]{mediaInformation.getStreams().get(0).width, mediaInformation.getStreams().get(0).height};
        this.scale = (float) transcodeConfiguration.getResolution()[0] / (float) inputResolution[0];
        this.upscale = scale > 1;
    }


    private void setTrueResolution() throws IOException {

        /*
        * This code uses the FFmpeg cropdetect filter to detect whether the video has "black bars" on the top or the bottom.
        * If it does, it will crop the video to remove them. This is done to improve efficiency as no extra pixels need to be
        * encoded. The cropdetect filter outputs the width and height of the cropped video to the console, so this code parses
        * the output to get the true resolution of the video.
        */
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg.getPath(), "-i", inputFile.getAbsolutePath(), "-vf", "cropdetect,metadata=mode=print", "-f", "null", "-");
        Process process = processBuilder.start();
        BufferedReader stdInput
                = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        String s;
        int index = 0;
        int[] widthArray = new int[500];
        int[] heightArray = new int[500];
        while ((s = stdInput.readLine()) != null && index <= 500) {
            if (s.contains("lavfi.cropdetect.w=")) {
                int width = Integer.parseInt(s.substring(s.indexOf("=") + 1));
                widthArray[index] = width;
                index++;
            } else if (s.contains("lavfi.cropdetect.h=")) {
                int height = Integer.parseInt(s.substring(s.indexOf("=") + 1));
                heightArray[index] = height;
            }

        }
        process.destroy();
        //Calculate the mode of the width and height arrays to get the true resolution
        trueResolution[0] = mode(widthArray)[0];
        trueResolution[1] = mode(heightArray)[0];
        trueAspectRatio = new int[]{trueResolution[0] / gcd(trueResolution[0], trueResolution[1]), trueResolution[1] / gcd(trueResolution[0], trueResolution[1])};
    }


    public FFmpegJob build() throws IOException {
        if (getTranscodeConfiguration().isAutoCrop()) {
            setTrueResolution();
        } else {
            trueResolution = transcodeConfiguration.getResolution();
        }
        FFmpegProbeResult probeResult = ffprobe.probe("input.mp4");
        FFmpegStream stream = probeResult.getStreams().get(0);
        boolean rf = transcodeConfiguration.getRf() != -1;
        this.ffmpegOutputBuilder = new FFmpegOutputBuilder()
                .setFilename(outputFile.getAbsolutePath())
                .disableAudio()
                .disableSubtitle()
                .setVideoCodec(transcodeConfiguration.getCodec())
                .addExtraArgs("-f", "segment")
                .setVideoMovFlags("-filter:v crop=iw-0:ih-" + (inputResolution[1] - trueResolution[1]) + ",scale=" + (int) (trueResolution[0] * scale) + ":" + (int) (trueResolution[1] * scale) +
                        "-");

        switch (transcodeConfiguration.getCodec()) {
            case "libx264", "libx265", "libvpx-vp9", "libaom-av1", "av1-svt":
                if (transcodeConfiguration.getCrf() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getCrf());
                } else if (transcodeConfiguration.getBitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getBitrate());
                }
                break;

            case "h264_nvenc", "hevc_nvenc":
                if (transcodeConfiguration.getRf() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getRf());
                } else if (transcodeConfiguration.getBitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getBitrate());
                }
                break;

            case "h264_qsv", "hevc_qsv", "vp9_qsv", "av1_qsv":
                if (transcodeConfiguration.getQv() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getQv());
                } else if (transcodeConfiguration.getBitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getBitrate());
                }

            case "copy":
                ffmpegOutputBuilder.setVideoCodec("copy");


        }
        ffmpegBuilder = new FFmpegBuilder().addOutput(ffmpegOutputBuilder);
        ffmpegBuilder.setInput(inputFile.getAbsolutePath())
                .overrideOutputFiles(true);
        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        if (transcodeConfiguration.isTwoPass()) {
            return executor.createJob(ffmpegBuilder, null);
        }

        return executor.createJob(ffmpegBuilder, null);
    }
}
