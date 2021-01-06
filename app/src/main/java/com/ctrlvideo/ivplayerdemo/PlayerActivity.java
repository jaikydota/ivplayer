package com.ctrlvideo.ivplayerdemo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.ctrlvideo.comment.IVViewListener;
import com.ctrlvideo.comment.ViewState;
import com.ctrlvideo.ivplayer.PlayerState;
import com.ctrlvideo.nativeivview.NativeIVView;

public class PlayerActivity extends FragmentActivity {

    private String TAG = "PlayerActivity";

    private VideoView mVideoView;
    private NativeIVView ivView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mVideoView = findViewById(R.id.video_view);
        mVideoView.setMediaController(null);
        ivView = findViewById(R.id.iv_view);


        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mVideoView.start();
                ivView.onPlayerStateChanged(PlayerState.STATE_READY);

                Log.d(TAG, "onPrepared");


//                mVideoView.start();
//                ivView.onPlayerStateChanged(PlayerState.STATE_ONPLAY);

            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivView.onPlayerStateChanged(PlayerState.STATE_END);
            }
        });

        mVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    ivView.onPlayerStateChanged(PlayerState.STATE_LOADED);
                }

                return true;
            }
        });


        ivView.initIVView("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5925315322305659", null, new IVViewListener() {


            @Override
            public void onIVViewStateChanged(String state, String data) {

                if (ViewState.STATE_READIED.equals(state)) {
                    ivView.onPlayerStateChanged(PlayerState.STATE_LOADED);
                    mVideoView.setVideoPath(data);
                }

            }

            @Override
            public long getPlayerCurrentTime() {
                return mVideoView.getCurrentPosition();
            }

            @Override
            public void seekToTime(long time) {
                mVideoView.seekTo((int) time);
            }

            @Override
            public void ctrlPlayer(String state) {

                if ("play".equals(state)) {
                    mVideoView.start();
                    ivView.onPlayerStateChanged(PlayerState.STATE_ONPLAY);
                } else {
                    mVideoView.pause();
                    ivView.onPlayerStateChanged(PlayerState.STATE_ONPAUSE);
                }

            }

//            @Override
//            public void onIVViewClick(String info) {
//
//                Log.d(TAG, "onIVViewClick--" + info);
//            }

            @Override
            public void onEventCallback(String result) {
                Log.d(TAG, "onEventCallback--" + result);
            }

            @Override
            public void onError(String errorType) {
                Log.d(TAG, "onError--" + errorType);
            }

            @Override
            public void onCustomNotify(String msg) {

            }

            @Override
            public boolean onHrefUrl(String url) {

                Log.d(TAG, "onHrefUrl--" + url);
                return false;
            }

            @Override
            public boolean onCallPhone(String phone) {
                Log.d(TAG, "onCallPhone--" + phone);
                return false;
            }

            @Override
            public void onProgressCallback(String seekList) {
                Log.d(TAG, "seekList--" + seekList);
            }

        }, this);
    }


    private boolean isLifeToPause = false;

    @Override
    protected void onResume() {
        super.onResume();


        if (isLifeToPause) {
            isLifeToPause = false;
            //继续播放
            mVideoView.start();
            ivView.onPlayerStateChanged(PlayerState.STATE_ONPLAY);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mVideoView.isPlaying()) {
            //暂停视频
            mVideoView.pause();
            ivView.onPlayerStateChanged(PlayerState.STATE_ONPAUSE);
            isLifeToPause = true;
        }
    }
}
