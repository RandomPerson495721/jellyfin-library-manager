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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private boolean interlace;
    private int[] trueResolution;
    private int[] inputResolution;
    private long inputBitrate;
    private float scale;
    private boolean upscale;
    private FFmpegOutputBuilder ffmpegOutputBuilder;
    private FFmpegBuilder ffmpegBuilder;

    public VideoTranscodeJobBuilder(TranscodeConfiguration transcodeConfiguration, JobDefinition jobDefinition) throws IOException {
        this.transcodeConfiguration = transcodeConfiguration;
        this.ffmpeg = new FFmpeg();
        this.ffprobe = new FFprobe();
        this.inputFile = jobDefinition.getFile();
        this.jobID = jobDefinition.getId();
        this.outputFile = new File(inputFile.getParent() + "/VideoElement/" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + "[JobID=" + jobID + "].mp4");
        FFmpegProbeResult mediaInformation = ffprobe.probe(inputFile.getAbsolutePath());
        this.inputBitrate = mediaInformation.getFormat().bit_rate;
        this.inputResolution = new int[]{mediaInformation.getStreams().get(0).width, mediaInformation.getStreams().get(0).height};
        this.scale = (float) transcodeConfiguration.getResolution()[0] / (float) inputResolution[0];
        this.upscale = scale > 1;

    }


    private int[] getAutoCroppedResolution() throws IOException {

        /*
        * This code uses the FFmpeg cropdetect filter to detect whether the video has "black bars" on the top or the bottom.
        * If it does, it will crop the video to remove them. This is done to improve efficiency as no extra pixels need to be
        * encoded. The cropdetect filter outputs the width and height of the cropped video to the console, so this code parses
        * the output to get the true resolution of the video.
        */
        int[] croppedResolution = new int[2];
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg.getPath(), "-i", inputFile.getAbsolutePath(), "-vf", "cropdetect,metadata=mode=print", "-f", "null", "-");
        Process process = processBuilder.start();
        BufferedReader stdInput
                = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        String s;
        int index = 0;
        int[] widthArray = new int[500];
        int[] heightArray = new int[500];
        while (index <= 500 && (s = stdInput.readLine()) != null) {
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
        croppedResolution[0] = mode(widthArray)[0];
        croppedResolution[1] = mode(heightArray)[0];
        return croppedResolution;
    }

    public boolean getInterlaced() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg.getPath(), "-i", inputFile.getAbsolutePath(), "-filter:v", "idet", "-frames:v", "500", "-an", "-f", "null", "-");
        Process process = processBuilder.start();
        BufferedReader stdInput
                = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        int tff = 0;
        int progressive = 0;
        /* This is a sample output from ffmpeg
        * [Parsed_idet_0 @ 0x600002684b00] Repeated Fields: Neither:   499 Top:     1 Bottom:     1
        * [Parsed_idet_0 @ 0x600002684b00] Single frame detection: TFF:     4 BFF:     4 Progressive:    97 Undetermined:   396
        * [Parsed_idet_0 @ 0x600002684b00] Multi frame detection: TFF:     5 BFF:     0 Progressive:   376 Undetermined:   120
        */
        Pattern tffpattern = Pattern.compile("TFF: (.*?) BFF");
        Pattern progressivepattern = Pattern.compile("Progressive: (.*?) Undetermined");
        while((stdInput.readLine()) != null) {
            Matcher tffmatcher = tffpattern.matcher(stdInput.readLine());
            Matcher progressivematcher = progressivepattern.matcher(stdInput.readLine());
            if (tffmatcher.find()) {
                tff += Integer.parseInt(tffmatcher.group(1).trim());
            }
            if (progressivematcher.find()) {
                progressive += Integer.parseInt(progressivematcher.group(1).trim());
            }
        }
        process.destroy();
        return tff > progressive;
    }




    public FFmpegJob build() throws IOException {
        if (getTranscodeConfiguration().isAuto_crop()) {
            trueResolution = getAutoCroppedResolution();
        } else {
            trueResolution = transcodeConfiguration.getResolution();
        }
        interlace = getInterlaced();
        teceline = getTeceline();
        FFmpegProbeResult probeResult = ffprobe.probe("input.mp4");
        FFmpegStream stream = probeResult.getStreams().get(0);
        boolean rf = transcodeConfiguration.getRf_nvenc() != -1;
        this.ffmpegOutputBuilder = new FFmpegOutputBuilder()
                .setFilename(outputFile.getAbsolutePath())
                .disableAudio()
                .disableSubtitle()
                .setVideoCodec(transcodeConfiguration.getCodec())
                .addExtraArgs("-f", "segment")
                //TODO: Investigate mysterious dash sign
                .addExtraArgs("-filter:v crop=iw-0:ih-" + (inputResolution[1] - trueResolution[1]) + "-");

        switch (transcodeConfiguration.getCodec()) {
            case "libx264", "libx265", "libvpx-vp9", "libaom-av1", "av1-svt":
                if (transcodeConfiguration.getCrf() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getCrf());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                ffmpegOutputBuilder.setVideoResolution((int) (trueResolution[0] * scale), (int) (trueResolution[1] * scale));
                break;

            case "h264_nvenc", "hevc_nvenc":
                if (transcodeConfiguration.getRf_nvenc() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getRf_nvenc());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                ffmpegOutputBuilder.addExtraArgs("-hwaccel", "cuvid");
                ffmpegOutputBuilder.addExtraArgs("-resize", trueResolution[0] + "x" + trueResolution[1]);
                break;

            case "h264_qsv", "hevc_qsv", "vp9_qsv", "av1_qsv":
                if (transcodeConfiguration.getQv_QSV() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getQv_QSV());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                ffmpegOutputBuilder.addExtraArgs("-hwaccel", "qsv");
                ffmpegOutputBuilder.setVideoResolution((int) (trueResolution[0] * scale), (int) (trueResolution[1] * scale));

            case "copy":
                ffmpegOutputBuilder.setVideoCodec("copy");


        }
        if (transcodeConfiguration.isAuto_deinterlace() && interlace) ffmpegOutputBuilder.setVideoFilter("yadif");
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
