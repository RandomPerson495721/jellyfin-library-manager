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

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.partin.james.jellyfinlibrarymanager.helpers.MathHelpers.mode;


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
        this.outputFile = new File(inputFile.getParent() + "/VideoElement/" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + jobID + ".mp4");
        var a = inputFile.getPath();
        FFmpegProbeResult mediaInformation = ffprobe.probe(a);
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
        //Don't show anything but the output of the cropdetect filter, no need to show the rest of the ffmpeg output, I either get deadlocks or nothing, I just want the raw data
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg.getPath(), "-i", inputFile.getAbsolutePath(), "-vf", "cropdetect,metadata=mode=print", "-f", "null", "-").redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String s;
        int index = 0;
        int maxFrames = 1000;
        int[] widthArray = new int[maxFrames];
        int[] heightArray = new int[maxFrames];
        s = stdInput.readLine();
        while (index < maxFrames && (s = stdInput.readLine()) != null) {

            if (s.contains("lavfi.cropdetect.w=")) {
                int width = Integer.parseInt(s.substring(s.indexOf("=") + 1));
                if (width < 0) width *= -1;
                widthArray[index] = width;
            } else if (s.contains("lavfi.cropdetect.h=")) {
                int height = Integer.parseInt(s.substring(s.indexOf("=") + 1));
                if (height < 0) height *= -1;
                heightArray[index] = height;
                index++;
            }


        }
        process.destroy();
        //Calculate the mode of the width and height arrays to get the true resolution
        croppedResolution[0] = mode(widthArray);
        croppedResolution[1] = mode(heightArray);
        return croppedResolution;
    }

    public boolean getInterlaced() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(ffmpeg.getPath(), "-i", inputFile.getAbsolutePath(), "-filter:v", "idet", "-frames:v", "500", "-an", "-f", "null", "-").redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        int tff = 0;
        int progressive = 0;
        /* This is a sample output from ffmpeg
         * [Parsed_idet_0 @ 0x600002684b00] Repeated Fields: Neither:   499 Top:     1 Bottom:     1
         * [Parsed_idet_0 @ 0x600002684b00] Single frame detection: TFF:     4 BFF:     4 Progressive:    97 Undetermined:   396
         * [Parsed_idet_0 @ 0x600002684b00] Multi frame detection: TFF:     5 BFF:     0 Progressive:   376 Undetermined:   120
         */
        Pattern tffpattern = Pattern.compile("TFF: (.*?) BFF");
        Pattern progressivepattern = Pattern.compile("Progressive: (.*?) Undetermined");
        String s;
        while ((s = stdInput.readLine()) != null) {
            Matcher tffmatcher = tffpattern.matcher(s);
            Matcher progressivematcher = progressivepattern.matcher(s);
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
        interlace = transcodeConfiguration.isAuto_deinterlace() && getInterlaced();
        teceline = getTeceline();
        this.ffmpegOutputBuilder = new FFmpegOutputBuilder().setFilename(outputFile.getAbsolutePath()).disableAudio().disableSubtitle().setVideoCodec(transcodeConfiguration.getCodec()).addExtraArgs("-movflags", "frag_keyframe+empty_moov")
                //TODO: Investigate mysterious dash sign
                ;

        switch (transcodeConfiguration.getCodec()) {
            case "libx264", "libx265", "libvpx-vp9", "libaom-av1", "av1-svt":
                if (transcodeConfiguration.getCrf() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getCrf());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                int targetWidth = (int) (trueResolution[0] * scale);
                int targetHeight = (int) (trueResolution[1] * scale);
                ffmpegOutputBuilder.setVideoResolution((targetWidth % 2 == 0) ? targetWidth : targetWidth + 1, (targetHeight % 2 == 0) ? targetHeight : targetHeight + 1);


                break;

            case "h264_nvenc", "hevc_nvenc":
                if (transcodeConfiguration.getRf_nvenc() != -1) {
                    ffmpegOutputBuilder.setConstantRateFactor(transcodeConfiguration.getRf_nvenc());
                } else if (transcodeConfiguration.getVideo_bitrate() != -1) {
                    ffmpegOutputBuilder.setVideoBitRate(transcodeConfiguration.getVideo_bitrate());
                }
                ffmpegOutputBuilder.addExtraArgs("-hwaccel", "cuvid");
                //TODO: make the resolution divisible by 2
                ffmpegOutputBuilder.addExtraArgs("-resize", trueResolution[0] + "x" + trueResolution[1]);
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
        String interlaceString = interlace ? ",yadif=mode=send_frame:parity=tff" : "";
        ffmpegOutputBuilder.addExtraArgs("-vf", "crop=iw-" + (inputResolution[0] - trueResolution[0]) + ":ih-" + (inputResolution[1] - trueResolution[1]) + interlaceString);
        ffmpegBuilder = new FFmpegBuilder().addOutput(ffmpegOutputBuilder);
        ffmpegBuilder.setInput(inputFile.getAbsolutePath()).overrideOutputFiles(true);
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
