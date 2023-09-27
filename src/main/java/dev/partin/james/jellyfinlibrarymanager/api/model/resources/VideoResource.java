package dev.partin.james.jellyfinlibrarymanager.api.model.resources;

import dev.partin.james.jellyfinlibrarymanager.helpers.JPAFile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.TreeMap;
@Getter
@Setter
@Entity
public class VideoResource {

    private int bitrate = -1;
    private VideoCodec codec;
    private int Crf = -1;
    private int nvCq = -1;
    private int qsvCq = -1;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private enum VideoCodec {
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


}

