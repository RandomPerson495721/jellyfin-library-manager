package dev.partin.james.jellyfinlibrarymanager.helpers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TranscodeConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int crf = -1;
    //Nvidia NVENC
    //TODO: Verify the naming schemes for nvenc rate control
    private int rf_nvenc = -1;
    //Intel QSV via VAAPI
    private int qv_QSV = -1;
    private String codec;
    private int video_bitrate = -1;
    private boolean two_pass;
    private boolean HDR;
    private int[] resolution;
    private int framerate;
    private boolean auto_crop;
    private boolean auto_deinterlace;


    //Audio configuration
    private int audio_bitrate = -1;
    private int audio_channels = -1;
    private int audio_sample_rate = -1;
    private String audio_codec;
    private boolean audio_passthrough;

    //Subtitle configuration
    private boolean generate_subtitles;

}

