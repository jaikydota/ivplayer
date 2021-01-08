package com.ctrlvideo.ivplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;

import com.ctrlvideo.comment.IVViewListener;
import com.ctrlvideo.comment.IView;
import com.ctrlvideo.comment.ViewState;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.google.android.exoplayer2.PlaybackParameters;
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
        if (isInEditMode()) {
            return;
        }
        RelativeLayout inflate = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_player, this, true);
        playerView = findViewById(R.id.video_view);
        ivView = findViewById(R.id.ivViewContainer);
//

    }

    ;


    private void initializePlayer() {
        //创建简单exo播放器

        if (player != null) {
            player.release();
        }

        player = new SimpleExoPlayer.Builder(mContext).build();
        playerView.setPlayer(player);

        //隐藏播放器的自带控制条，进度条等
        playerView.setUseController(false);
        //监听播放器状态事件
        player.addListener(new ComponentListener());

    }

    public void release() {
//        if (player != null) {
//            player.release();
//        }

        if (player != null) {
            player.release();
            playerView.setPlayer(null);
        }

        ivView.release();
    }

    private void loadVideo(String config_url) {
        //ivView初始化，此处传config_url
        ivView.initIVView(config_url, null, new IVListener(), (Activity) mContext);
    }

    private void loadVidePid(String pid) {
        //ivView初始化，此处传pid
        ivView.initIVViewPid(pid, null, new IVListener(), (Activity) mContext);
    }

    private String playerStatus;


    //播放器状态改变listener
    private class ComponentListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


//            if (playWhenReady && playbackState == Player.STATE_READY) {
//                LogUtils.d(TAG, "onPlayerStateChanged: playing media");
//            }
            switch (playbackState) {
                case Player.STATE_IDLE:

                    LogUtils.d(TAG, "onPlayerStateChanged: playing media---STATE_IDLE");

                    break;
                case Player.STATE_BUFFERING:
                    LogUtils.d(TAG, "onPlayerStateChanged: playing media---STATE_BUFFERING");

                    playerStatus = PlayerState.STATE_LOADED;
                    ivView.onPlayerStateChanged(playerStatus);
                    if (pListener != null) {
                        pListener.onStateChanged(playerStatus);
                    }


                    break;
                //当播放器播放或暂停时
                case Player.STATE_READY:
                    LogUtils.d(TAG, "onPlayerStateChanged: playing media---STATE_READY---" + playWhenReady);
                    ivView.onPlayerStateChanged(PlayerState.STATE_READY);

                    playerStatus = playWhenReady ? PlayerState.STATE_ONPLAY : PlayerState.STATE_ONPAUSE;
                    ivView.onPlayerStateChanged(playerStatus);
                    if (pListener != null) {
                        pListener.onStateChanged(playerStatus);
                    }

                    break;
                //当播放器 播放结束[到视频结尾]时
                case Player.STATE_ENDED:
                    LogUtils.d(TAG, "onPlayerStateChanged: playing media---STATE_ENDED");
                    playerStatus = PlayerState.STATE_END;
                    ivView.onPlayerStateChanged(playerStatus);
                    if (pListener != null) {
                        pListener.onStateChanged(playerStatus);
                    }


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
        public void onIVViewStateChanged(String state, String videoUrl) {
            if (state.equals(ViewState.STATE_READIED)) {
                initializePlayer();
                // 创建资源
                DefaultDataSourceFactory dataSourceFactory =
                        new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Ivsdk"), null);
                //播放器使用vid的视频
                Uri mp4VideoUri = Uri.parse(videoUrl);
                MediaSource videoSource =
                        new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
                // 准备
                player.prepare(videoSource);
                //开始播放
                player.setPlayWhenReady(true);


                if (pListener != null) {
                    pListener.onStateChanged(PlayerState.STATE_LOADED);
                }
            }
        }

        @Override
        public void onEventCallback(String result) {
            LogUtils.d(TAG, "onEventCallback--- " + result);
            if (pListener != null) {
                pListener.onEventCallback(result);
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
                if (PlayerState.STATE_END.equals(playerStatus)) {
                    player.seekTo(0);
                } else {
                    player.setPlayWhenReady(true);
                }

            } else if (state.equals("pause")) {
                player.setPlayWhenReady(false);
            }
        }

        @Override
        public void setVideoRatio(float ratio) {
            PlaybackParameters playbackParameters = new PlaybackParameters(ratio, 1.0F);
            player.setPlaybackParameters(playbackParameters);
        }

        //        /**
//         * 当IvView点击时 [如点击IvView中控件将阻止向上冒泡，不会调用此方法]
//         *
//         * @param info 点击信息
//         */
//        @Override
//        public void onIVViewClick(String info) {
//            if (pListener != null) {
//                pListener.onViewClick(info);
//            }
//        }


        /**
         * 当IVView发生错误时
         *
         * @param errorType 错误信息
         */
        @Override
        public void onError(String errorType) {
            LogUtils.d(TAG, "onIvViewError " + errorType);
            if (pListener != null) {
                pListener.onError(errorType);
            }

        }

        /**
         * 当IVView收到自定义通知
         *
         * @param msg 通知内容
         */
        @Override
        public void onCustomNotify(String msg) {
            if (pListener != null) {
                pListener.onCustomNotify(msg);
            }

        }


        /**
         * 跳转网页链接回调
         *
         * @param url 链接
         * @return true：实现网页跳转    false：sdk内部消耗，内部实现简单网页跳转
         */
        @Override
        public boolean onHrefUrl(String url) {
            if (pListener != null) {
                pListener.onHrefUrl(url);
            }
            return false;

        }

        @Override
        public boolean onCallPhone(String phone) {
            if (pListener != null) {
                pListener.onCallPhone(phone);
            }
            return false;
        }

        @Override
        public void onProgressCallback(String seekList) {

            LogUtils.d(TAG, "onProgressCallback---" + seekList);
            if (pListener != null) {
                pListener.onProgressCallback(seekList);
            }
        }
    }


    //对外开放接口

    //普通接口

    /**
     * 加载互动视频
     *
     * @param config_url 静态url 路径
     */
    public void loadIVideo(@NonNull String config_url, @NonNull IVPlayerListener playerListener) {
        this.loadVideo(config_url);
        this.pListener = playerListener;
    }

    /**
     * 加载互动视频
     *
     * @param pid 智令互动编辑器制作的互动视频项目id
     */
    public void loadIVideoPid(@NonNull String pid, @NonNull IVPlayerListener playerListener) {
        this.loadVidePid(pid);
        this.pListener = playerListener;
    }


    //播控接口

    /**
     * 播放互动视频
     */
    public void play() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    /**
     * 暂停互动视频
     */
    public void pause() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    /**
     * 停止互动视频
     */
    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    /**
     * 当前是否正在播放
     *
     * @return true 正在播放，false未播放
     */
    public boolean isPlaying() {
        if (player != null) {
            return player.isPlaying();
        }
        return false;
    }

    /**
     * 设置当前播放位置，单位是毫秒，此函数请慎用
     *
     * @param msec 时间，毫秒
     */
    public void seekTo(int msec) {
        if (player != null) {
            player.seekTo(msec);
        }
    }


}
