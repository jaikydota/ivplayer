package com.ctrlvideo.ivplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;

import com.ctrlvideo.comment.IVViewListener;
import com.ctrlvideo.comment.IView;
import com.ctrlvideo.comment.ViewState;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * Author by Jaiky, Date on 2020/4/8.
 */
@SuppressLint("NewApi")
public class IVPlayer extends RelativeLayout implements LifecycleObserver {

    protected String TAG = "IVSDKView";

    //exoplayer
    private PlayerView playerView;
    private SimpleExoPlayer player;

    //互动视频视图组件
    private IView ivView;

    private IVPlayerListener pListener = null;


    private Context mContext;

    public IVPlayer(Context context) {
        super(context);
        initView(context);
    }

    public IVPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public IVPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        if (isInEditMode()){
            return;
        }
        RelativeLayout inflate = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_player, this, true);
        playerView = findViewById(R.id.video_view);
        ivView = findViewById(R.id.ivViewContainer);

        initializePlayer();
    };


    private void initializePlayer() {
        //创建简单exo播放器
        player = new SimpleExoPlayer.Builder(mContext).build();
        playerView.setPlayer(player);

        //隐藏播放器的自带控制条，进度条等
        playerView.setUseController(false);
        //监听播放器状态事件
        player.addListener(new ComponentListener());
    }

    private void loadVideo(String pid) {
        //ivView初始化，此处传pid
        ivView.initIVView(pid, null, new IVListener(), (Activity) mContext);
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

                    pListener.onStateChanged(playStatus);
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
        public void onIVViewStateChanged(String state, String data) {
            if (state.equals(ViewState.STATE_READIED)) {

                // 创建资源
                DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext,"Ivsdk"),null);
                //播放器使用vid的视频
                Uri mp4VideoUri = Uri.parse(data);
                MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
                // 准备
                player.prepare(videoSource);
                //开始播放
                player.setPlayWhenReady(true);

                pListener.onStateChanged(PlayerState.STATE_LOADED);
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
            pListener.onViewClick(info);
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
        }

        /**
         * 当IVView发生错误时
         *
         * @param errorType 错误信息
         */
        @Override
        public void onError(String errorType) {
            Log.d(TAG, "onIvViewError " + errorType);
            pListener.onError(errorType);
        }

        /**
         * 当IVView收到自定义通知
         * @param msg 通知内容
         */
        @Override
        public void onCustomNotify(String msg) {
            pListener.onCustomNotify(msg);
        }

        @Override
        public void onHrefUrl(String url) {
            pListener.onHrefUrl(url);
        }
    }



    //对外开放接口

    //普通接口

    /**
     * 加载互动视频
     * @param pid 智令互动编辑器制作的互动视频项目id
     */
    public void loadIVideo(@NonNull String pid, @NonNull IVPlayerListener playerListener) {
        this.loadVideo(pid);
        this.pListener = playerListener;
    }



    //播控接口
    /**
     * 播放互动视频
     */
    public void play() {
        player.setPlayWhenReady(true);
    }

    /**
     * 暂停互动视频
     */
    public void pause() {
        player.setPlayWhenReady(false);
    }

    /**
     * 停止互动视频
     */
    public void stop() {
        player.stop();
    }

    /**
     * 当前是否正在播放
     * @return true 正在播放，false未播放
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }

    /**
     * 设置当前播放位置，单位是毫秒，此函数请慎用
     * @param msec 时间，毫秒
     */
    public void seekTo(int msec) {
        player.seekTo(msec);
    }





}
