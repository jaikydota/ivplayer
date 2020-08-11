package com.ctrlvideo.ivplayerdemo;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ctrlvideo.ivplayer.IVEvent;
import com.ctrlvideo.ivplayer.IVView;
import com.ctrlvideo.ivplayer.IVViewListener;
import com.ctrlvideo.ivplayer.ViewState;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;



public class MainActivity extends FragmentActivity implements View.OnClickListener {
    protected String TAG = "CTRLVIDEO";

    //exoplayer
    private PlayerView playerView;
    private SimpleExoPlayer player;

    //互动视频视图组件
    private IVView ivView;

    //控制条
    private LinearLayout ll_Control;
    private FrameLayout fl_chapterOne;
    private FrameLayout fl_chapterTwo;
    private FrameLayout fl_chapterThree;
    private FrameLayout fl_chapterFour;
    Button btn_pure, btn_interrupt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.video_view);
        ivView = findViewById(R.id.ivViewContainer);
        ll_Control = findViewById(R.id.ll_Control);
        fl_chapterOne = findViewById(R.id.fl_chapterOne);
        fl_chapterTwo = findViewById(R.id.fl_chapterTwo);
        fl_chapterThree = findViewById(R.id.fl_chapterThree);
        fl_chapterFour = findViewById(R.id.fl_chapterFour);
        btn_pure = findViewById(R.id.btn_pure);
        btn_interrupt = findViewById(R.id.btn_interrupt);

        btn_pure.setOnClickListener(this);
        btn_interrupt.setOnClickListener(this);
        fl_chapterOne.setOnClickListener(this);
        fl_chapterTwo.setOnClickListener(this);
        fl_chapterThree.setOnClickListener(this);
        fl_chapterFour.setOnClickListener(this);

        checkPermission();
        initializePlayer();
        loadVideo("5213023290601669", "https://movies.ctrlvideo.com/288e5a6dvodsh1257105375/b6f8286f5285890802633318856/f0.mp4");
//        loadVideo("5213019544683399", "https://movies.ctrlvideo.com/288e5a6dvodsh1257105375/b6f8286f5285890802633318856/f0.mp4");
    }


    private void initializePlayer() {
        //创建简单exo播放器
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        //隐藏播放器的自带控制条，进度条等
        playerView.setUseController(false);
        //监听播放器状态事件
        player.addListener(new ComponentListener());
    }

    private void checkPermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {

        }
        else {
            //提示用户开启录音、摄像头权限
            String[] perms = {"android.permission.RECORD_AUDIO", "android.permission.CAMERA"};
            ActivityCompat.requestPermissions(MainActivity.this, perms, 99);
        }
    }


    private void loadVideo(String pid, String url) {
        // 创建资源
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,"Ivsdk"),null);
        //播放器使用vid的视频
        Uri mp4VideoUri = Uri.parse(url);
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);

        // 准备
        player.prepare(videoSource);
        // 是否播放
        player.setPlayWhenReady(false);

        //ivView初始化，此处传pid
        ivView.initIVView(pid, null, new IVListener(), this, true);
    }


    @Override
    public void onClick(View v) {
        if (v == fl_chapterOne){
            loadVideo("5213023290601669", "https://movies.ctrlvideo.com/288e5a6dvodsh1257105375/b6f8286f5285890802633318856/f0.mp4");
//            loadVideo("5213019544683399", "https://movies.ctrlvideo.com/288e5a6dvodsh1257105375/b6f8286f5285890802633318856/f0.mp4");
        }
        else if (v == fl_chapterTwo){
            loadVideo("5213085402382706", "https://movies-1300249927.file.myqcloud.com/media/13/213/release/5213085402382706/5213085402382706_20200602173910.mp4");
        }
        else if (v == fl_chapterThree){
            loadVideo("5213995671039400", "https://movies.ctrlvideo.com/288e5a6dvodsh1257105375/b6f8286f5285890802633318856/f0.mp4");
        }
        else if (v == fl_chapterFour){
            loadVideo("5852747177970405", "https://movies-1300249927.file.myqcloud.com/media/85/85/release/5852747177970405/5852747177970405_20200713170442.mp4");
//            loadVideo("5921655052599148", "https://movies.ctrlvideo.com/288e5a6dvodsh1257105375/f5686e525285890797737440710/f0.mp4");
        }
        else if (v == btn_pure){
            if (ivView.isPureMode())
                ivView.setPureMode(false);
            else
                ivView.setPureMode(true);
        }
        else if (v == btn_interrupt){
            if (isEventPrepare) {
                ivView.performAction(IVEvent.EventAction.SKIP_PREPARE_TIME);
                isEventPrepare = false;
            }
        }
    }



    //是否因生命周期导致的暂停
    private boolean isLifeToPause = false;
    //是否进入 “互动事件”
    private boolean isEventPrepare = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isLifeToPause) {
            isLifeToPause = false;
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //暂停视频
        if (player.getPlayWhenReady()) {
            player.setPlayWhenReady(false);
            isLifeToPause = true;
        }

        //如果已经进入事件时间范围内
        //调用 EventAction.SKIP_PREPARE_TIME 将中断事件，如停止录音和手势识别，并将 seek时间到 prepare时间的前1秒。
        if (isEventPrepare) {
            ivView.performAction(IVEvent.EventAction.SKIP_PREPARE_TIME);
            isEventPrepare = false;


        }
    }


    //播放器状态改变listener
    private class ComponentListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playWhenReady && playbackState == Player.STATE_READY) {
                Log.d(TAG, "onPlayerStateChanged: playing media");
            }
            switch (playbackState) {
                case Player.STATE_IDLE:
                    break;
                case Player.STATE_BUFFERING:
                    break;
                //当播放器播放或暂停时
                case Player.STATE_READY:
                    String playStatus = playWhenReady ? "onplay" : "onpause";
                    ivView.onPlayerStateChanged(playStatus);
                    break;
                //当播放器 播放结束[到视频结尾]时
                case Player.STATE_ENDED:
                    break;
                default:
                    break;
            }
        }
    }



    //互动事件listener
    private class IVListener implements IVViewListener {


        /**
         * 当IVView状态改变时调用
         *
         * @param state 状态，ViewState.STATE_READIED 初始化完成
         */
        @Override
        public void onIVViewStateChanged(String state) {
            if (state.equals(ViewState.STATE_READIED)) {
                //开始播放
                player.setPlayWhenReady(true);
            }
        }

        /**
         * 获取当前播放时间
         *
         * @return long类型，毫秒
         */
        @Override
        public long getPlayerCurrentTime() {
            return player.getCurrentPosition();
        }

        /**
         * seek到播放器某个时间
         *
         * @param time long类型，毫秒
         */
        @Override
        public void seekToTime(long time) {
            player.seekTo(time);
        }

        /**
         * 控制播放器，如使播放器 “播放”或“暂停”
         *
         * @param state "play" 播放视频，"pause" 粘贴视频
         */
        @Override
        public void ctrlPlayer(String state) {
            if (state.equals("play")) {
                player.setPlayWhenReady(true);
            }
            else if (state.equals("pause")) {
                player.setPlayWhenReady(false);
            }
        }

        /**
         * 当IvView点击时 [如点击IvView中控件将阻止向上冒泡，不会调用此方法]
         *
         * @param info 点击信息
         */
        @Override
        public void onIVViewClick(String info) {
//            playerView.performClick();
            if (ll_Control.getVisibility() == View.VISIBLE)
                ll_Control.setVisibility(View.GONE);
            else
                ll_Control.setVisibility(View.VISIBLE);
        }

        /**
         * 当事件状态改变时
         * @param eType 事件类型，IVEvent.EVENT_SPEECHRECOGN 语音识别事件，IVEvent.EVENT_GESTURE 手势事件
         * @param state 状态，"prepare" 事件即将开始，"start" 事件开始，"end" 事件结束, "succeed" 触发成功跳帧
         * @param time long类型，毫秒
         */
        @Override
        public void onEventStateChanged(String eType, String state, long time) {
            Log.d(TAG, "onEventStateChanged eventType:" + eType + "  state:" + state + "  time:" + time);
            if (state.equals("prepare")) {
                //判断权限，如果没有权限，暂停视频，申请权限
                isEventPrepare = true;
            }
            else if (state.equals("start")) {
                //如果是语音识别开启
                if (eType.equals(IVEvent.EVENT_SPEECHRECOGN)) {
                    //开启录音和识别过程
                    //TODO 模拟语音识别过程
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            ivView.recognResultSend("我今天出去");
//                        }
//                    }, 500);
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            //包涵太阳关键词，将触发跳帧，并回调 "succeed"状态。
//                            ivView.recognResultSend("我今天出去看太阳");
//                        }
//                    }, 2000);
                }
            }
            else if (state.equals("end") || state.equals("succeed")) {
                isEventPrepare = false;

                if (eType.equals(IVEvent.EVENT_SPEECHRECOGN)) {
                    //结束录音和识别

                }
            }
        }

        /**
         * 当IVView发生错误时
         *
         * @param errorType 错误信息
         */
        @Override
        public void onError(String errorType) {
            Log.d(TAG, "onIvViewError " + errorType);
            if (errorType.equals("get_config_failed")){
                //获取配置文件失败，检查config_url
            }
        }
    }



}
