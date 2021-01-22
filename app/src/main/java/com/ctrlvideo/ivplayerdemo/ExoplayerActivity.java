package com.ctrlvideo.ivplayerdemo;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ctrlvideo.ivplayer.IVPlayer;

public class ExoplayerActivity extends FragmentActivity {

    private IVPlayer player;
    private String TAG = "ExoplayerActivity";

    private String pid;


    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_with_player);

        pid = getIntent().getStringExtra("pid");
        boolean fullscreen = getIntent().getBooleanExtra("fullscreen", true);
        if (!fullscreen) {
            View view = findViewById(R.id.rootview);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = dip2px(200);
            view.setLayoutParams(layoutParams);
        }

        player = findViewById(R.id.iv_Player);


        player.loadIVideo("https://apiive.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=" + pid, null);


    }


    private boolean isLifeToPause = false;


    @Override
    protected void onResume() {
        super.onResume();

        player.onResume();

        if (isLifeToPause) {
            isLifeToPause = false;
            //继续播放
            player.play();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        player.onPause();

        if (player.isPlaying()) {
            //暂停视频
            player.pause();
            isLifeToPause = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        player.onDestroy();

        if (player!=null) {
            player.release();
        }
    }

    //    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }
}
