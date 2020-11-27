package com.ctrlvideo.nativeivview;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 音频播放器
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;


    public AudioPlayer() {

    }

    public void play(String url, boolean loop) {


        try {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setLooping(loop);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        release();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }
}
