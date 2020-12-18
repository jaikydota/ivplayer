package com.ctrlvideo.ivplayerdemo;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ctrlvideo.ivplayer.IVPlayer;
import com.ctrlvideo.ivplayer.IVPlayerListener;

public class WithPlayerActivity extends FragmentActivity {

    private IVPlayer ivPlayer;
    private String TAG = "WithPlayerActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("WithPlayerActivity","onCreate");

        setContentView(R.layout.activity_with_player);

        ivPlayer = findViewById(R.id.iv_Player);

        ivPlayer.loadIVideo("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866", new IVPlayerListener() {
//        ivPlayer.loadIVideo("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028427201742", new IVPlayerListener() {
            @Override
            public void onStateChanged(String state) {

                Log.d(TAG, "onStateChanged---" + state);
            }

            @Override
            public void onViewClick(String info) {
                Log.d(TAG, "onViewClick---" + info);
            }


            @Override
            public void onError(String errorType) {
                Log.d(TAG, "onError---");
            }

            @Override
            public void onCustomNotify(String msg) {
                Log.d(TAG, "onCustomNotify---");
            }

            @Override
            public void onHrefUrl(String url) {
                Log.d(TAG, "onHrefUrl---");
            }
            @Override
            public void onCallPhone(String phone) {
                Log.d(TAG, "onCallPhone---");
            }

            @Override
            public void onEventCallback(String result) {
                Log.d(TAG, "onEventCallback---");
            }


        });
    }

    private boolean isLifeToPause = false;


    @Override
    protected void onResume() {
        super.onResume();


        if (isLifeToPause) {
            isLifeToPause = false;
            //继续播放
            ivPlayer.play();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ivPlayer.isPlaying()) {
            //暂停视频
            ivPlayer.pause();
            isLifeToPause = true;
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
}
