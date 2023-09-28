package dev.partin.james.jellyfinlibrarymanager.FFmpegAbstractions;

import dev.partin.james.jellyfinlibrarymanager.api.model.resources.VideoResource;



public class ResourceAnalyzers {
    public enum VideoCodec {
        H264,
        H264_10BIT,
        H264_NVENC,
        H264_NVENC10,
        H264_QSV,
        H264_QSV10,
        H265,
        H265_10BIT,
        H265_NVENC,
        H265_NVENC10,
        H265_QSV,
        H265_QSV10,
        MPEG2,
        MPEG4,
        VC1,
        VP8,
        VP9,
        VP9_10BIT,
        AV1,
        AV1_10BIT,
        AV1_QSV,
        AV1_QSV10,
//        AAC,
//        AC3,
//        EAC3,
//        DTS,
//        TRUEHD,
//        MP3,
//        VORBIS,
//        FLAC,
//        OPUS,
//        PCM,
//        WMA
    }
    public static class VideoResourceAnalyzer {
        //I should call this class something to reflect the fact that each method analyzes a video resource and sets a property on it.
        //Just write a bunch of dummy functions for now that return 0s or something to handle each property in VideoResource.

        public static void setAll(VideoResource videoResource) {
            //Call all the methods in this class to set all the properties of the video resource.
            setVideoLength(videoResource);
            setVideoResolution(videoResource);
            setVideoBitrate(videoResource);
            setVideoCodec(videoResource);
            setVideoFramerate(videoResource);
            setInterlaced(videoResource);
        }
        public static void setVideoLength(VideoResource videoResource) {
            //TODO: Implement me!
            videoResource.setLengthSeconds(0);
        }

        public static void setVideoResolution(VideoResource videoResource) {
            //TODO: Implement me!
            videoResource.setWidth(0);
            videoResource.setHeight(0);
        }

        public static void setVideoBitrate(VideoResource videoResource) {
            //TODO: Implement me!
            videoResource.setBitrate(0);
        }

        public static void setVideoCodec(VideoResource videoResource) {
            //TODO: Implement me!
            videoResource.setCodec(VideoCodec.H264);
        }

        public static void setVideoFramerate(VideoResource videoResource) {
            //TODO: Implement me!
            videoResource.setFramerate(0);
        }

        public static void setInterlaced(VideoResource videoResource) {
            //TODO: Implement me!
            videoResource.setInterlaced(false);
        }

    }
}