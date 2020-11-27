package com.ctrlvideo.nativeivview;

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

        playerList.add(audioPlayer);

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
