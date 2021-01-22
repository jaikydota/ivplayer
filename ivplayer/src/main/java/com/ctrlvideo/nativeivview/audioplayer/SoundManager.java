package com.ctrlvideo.nativeivview.audioplayer;

import java.util.ArrayList;
import java.util.List;

public class SoundManager {


    private static SoundManager soundManager;

    private List<AudioPlayer> playerList;


    public static SoundManager getInstance() {
        if (soundManager == null) {
            soundManager = new SoundManager();
        }

        return soundManager;
    }

    private SoundManager() {
        playerList = new ArrayList<>();
    }


    public void play(String url) {
        play(url, false);
    }

    public void play(String url, boolean loop) {

        AudioPlayer audioPlayer = new AudioPlayer();
        audioPlayer.play(url, loop);
        audioPlayer.setOnAudioPlayCompleteListener(new AudioPlayer.OnAudioPlayCompleteListener() {
            @Override
            public void AudioPlayComplete() {
                playerList.remove(audioPlayer);
            }
        });

        playerList.add(audioPlayer);

    }

    public void pause() {
        if (playerList != null) {

            for (AudioPlayer player : playerList) {
                player.pause();
            }

        }
    }

    public void resume() {
        if (playerList != null) {

            for (AudioPlayer player : playerList) {
                player.resume();
            }

        }
    }

    public void release() {

        if (playerList != null) {

            for (AudioPlayer player : playerList) {
                player.release();
            }
            playerList.clear();
        }


    }


}
