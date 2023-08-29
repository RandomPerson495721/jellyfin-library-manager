package dev.partin.james.jellyfinlibrarymanager.helpers;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.SeekableByteChannel;

@Getter
@Setter
@Entity
public class TranscodeConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int crf = -1;
    //Nvidia NVENC
    private int rf = -1;
    //Intel QSV via VAAPI
    private int qv = -1;
    private String codec;
    private int bitrate = -1;
    private boolean twoPass;
    private boolean HDR;
    private int[] resolution;
    private int framerate;
    private boolean autoCrop;

}

