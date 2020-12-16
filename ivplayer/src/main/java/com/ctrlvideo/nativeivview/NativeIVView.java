package com.ctrlvideo.nativeivview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.ctrlvideo.comment.IVViewListener;
import com.ctrlvideo.comment.IView;
import com.ctrlvideo.comment.ViewState;
import com.ctrlvideo.ivplayer.PlayerState;
import com.ctrlvideo.ivplayer.R;
import com.ctrlvideo.nativeivview.activity.WebActivity;
import com.ctrlvideo.nativeivview.audioplayer.SoundManager;
import com.ctrlvideo.nativeivview.component.ComponentManager;
import com.ctrlvideo.nativeivview.component.IComponentListener;
import com.ctrlvideo.nativeivview.model.EventIntractInfoCallback;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.net.HttpClient;
import com.ctrlvideo.nativeivview.net.callback.DownloadCallback;
import com.ctrlvideo.nativeivview.net.callback.GetIVideoInfoCallback;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;
import com.ctrlvideo.nativeivview.widget.ControllerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class NativeIVView extends RelativeLayout implements LifecycleObserver, IView, IComponentListener {

    private String TAG = "NativeIVView";


    //事件控件层
    protected RelativeLayout rlWVContainer;

    // 请求协议路径
    private String url;

    //是否测试网环境
    private boolean isTestEnv = true;

    private IVViewListener listener;

    private Handler handler = new Handler();

    //当前视图状态
    private String nowViewStatus = ViewState.STATE_LOADING;

    //协议
    protected VideoProtocolInfo videoProtocolInfo;

    private ComponentManager componentManager;

    // 播控页面
    private ControllerView mControllerView;

    //需要下载的资源
    private Map<String, Long> resourseMap = new HashMap<String, Long>();
    //正在下载的资源
    private List<String> downloading = new ArrayList<>();
    //已经下载的资源
    private List<String> downloadFinish = new ArrayList<>();

    //轮询间隔
    private long delay = 36;

    // 视频播放状态
    private String playerState;

    //后台播放音效
    private boolean playBackground = false;

    public NativeIVView(Context context) {
        this(context, null);
    }

    public NativeIVView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NativeIVView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }

        LayoutInflater.from(getContext()).inflate(R.layout.view_native, this);

        rlWVContainer = findViewById(R.id.rlWVContainer);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mControllerView != null) {
                    mControllerView.onClick();
                }
            }
        });
    }

    @Override
    public void initIVView(@Nullable String config_url, @Nullable String channel, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext) {
        config_url = config_url == null ? "" : config_url;
        url = config_url;
        init(ivViewListener, mContext);
    }

    @Override
    public void initIVViewPid(@Nullable String pid, @Nullable String channel, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext) {
        pid = pid == null ? "" : pid;
        url = isTestEnv ? "https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=" + pid : "https://apiive.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=" + pid;

        init(ivViewListener, mContext);
    }

    /**
     * 播放器状态
     *
     * @param status
     */
    @Override
    public void onPlayerStateChanged(String status) {

        this.playerState = status;

        if (nowViewStatus.equals(ViewState.STATE_READIED)) {
            if (componentManager != null) {
                componentManager.setVideoPlayerStatus(status);
            }
            if (mControllerView != null) {
                mControllerView.setVideoPlayerStatus(status);
            }
        }
    }

    @Override
    public void setPureMode(boolean isOpen) {

    }

    /**
     * 音效允许后台播放
     *
     * @param playBackground
     */
    public void setPlaySoundBackground(boolean playBackground) {
        this.playBackground = playBackground;
    }

    @Override
    public void release() {

        if (handler != null) {
            handler.removeCallbacks(mTicker);
        }


        SoundManager.getInstance().release();
        rlWVContainer.removeAllViews();
        if (mControllerView != null) {
            removeView(mControllerView);
        }
    }

    private void init(IVViewListener ivViewListener, Activity mContext) {

        this.listener = ivViewListener;
        if (mContext instanceof LifecycleOwner)
            ((LifecycleOwner) mContext).getLifecycle().addObserver(this);

        release();
        initData();
    }


    /**
     * 加载互动协议数据
     */
    private void initData() {


        nowViewStatus = ViewState.STATE_GET_IV_INFO;
        if (listener != null) {
            listener.onIVViewStateChanged(nowViewStatus, nowViewStatus);
        }
        if (url.startsWith("http")) {
            HttpClient.getInstanse().getIVideoInfo(url, new GetIVideoInfoCallback() {

                @Override
                protected void onFailure(String error) {
                    LogUtils.d("onFailure", error);


                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onError("get_config_failed");
                            }
                        }
                    });
                }

                @Override
                protected void onResponse(VideoProtocolInfo videoProtocolInfo) {

                    post(new Runnable() {
                        @Override
                        public void run() {
//                        LogUtils.d("onResponse", videoProtocolInfo.protocol.auto_indent + "");
                            onLoadVideoInfoFinish(videoProtocolInfo);
                        }
                    });
                }
            });
        } else {
            if (listener != null) {
                listener.onError("get_config_failed");
            }
        }
    }


    /**
     * 请求数据完成
     *
     * @param videoProtocolInfo
     */
    private void onLoadVideoInfoFinish(VideoProtocolInfo videoProtocolInfo) {

        this.videoProtocolInfo = videoProtocolInfo;
        if (componentManager == null) {
            componentManager = new ComponentManager();
        }
        componentManager.initParmas(getContext(), rlWVContainer, videoProtocolInfo, this);

        initControlView(videoProtocolInfo.release_info);

        nowViewStatus = ViewState.STATE_READIED;
        if (listener != null) {
            listener.onIVViewStateChanged(nowViewStatus, videoProtocolInfo.release_info.url);
            listener.onEventCallback(new EventIntractInfoCallback(videoProtocolInfo).toJson());
        }
        preloadResouse();

        if (handler != null) {
            handler.removeCallbacks(mTicker);
            handler.post(mTicker);
        }
    }


    /**
     * 初始化播控
     *
     * @param releaseInfo
     */
    private void initControlView(VideoProtocolInfo.ReleaseInfo releaseInfo) {

        if (releaseInfo != null) {
            VideoProtocolInfo.PlayerController playerController = releaseInfo.getPlayerController();
//            if (playerController.isShowContrller()) {
            mControllerView = new ControllerView(getContext());
            mControllerView.setOnControllerListener(new ControllerView.OnControllerListener() {

                @Override
                public void onPlayOrPause(boolean play) {

                    ctrlPlayer(play);
                }

                @Override
                public void onRestart() {
//                        onComponentSeek(0);
                    ctrlPlayer(true);
                }
            });
            mControllerView.initController(Math.max(getMeasuredWidth(), getMeasuredHeight()), playerController);
            addView(mControllerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

//            }
        }
    }


    /**
     * 预加载资源
     */
    private void preloadResouse() {

        resourseMap.clear();

        if (videoProtocolInfo == null) return;

        VideoProtocolInfo.Protocol protocol = videoProtocolInfo.protocol;
        if (protocol == null) return;

        List<VideoProtocolInfo.EventRail> eventRails = protocol.event_list;
        if (eventRails == null || eventRails.isEmpty()) return;

        for (VideoProtocolInfo.EventRail eventRail : eventRails) {
            List<VideoProtocolInfo.EventComponent> eventComponents = eventRail.obj_list;

            if (eventComponents != null && !eventComponents.isEmpty()) {

                for (VideoProtocolInfo.EventComponent eventComponent : eventComponents) {

                    long loadTime = (long) (eventComponent.start_time * 1000 - 10000);
                    if (loadTime < 0) {
                        loadTime = 0;
                    }

                    List<VideoProtocolInfo.EventOption> eventOptions = eventComponent.options;
                    if (eventOptions != null && !eventOptions.isEmpty()) {

                        for (VideoProtocolInfo.EventOption eventOption : eventOptions) {

                            VideoProtocolInfo.EventOptionCustom optionCustom = eventOption.custom;
                            if (optionCustom != null) {

                                VideoProtocolInfo.EventOptionStatus clickDefault = optionCustom.click_default;
                                addResourseMap(clickDefault.image_url, loadTime);
                                addResourseMap(clickDefault.audio_url, loadTime);

                                VideoProtocolInfo.EventOptionStatus clickOn = optionCustom.click_on;
                                addResourseMap(clickOn.image_url, loadTime);
                                addResourseMap(clickOn.audio_url, loadTime);

                                VideoProtocolInfo.EventOptionStatus clickEnded = optionCustom.click_ended;
                                addResourseMap(clickEnded.image_url, loadTime);
                                addResourseMap(clickEnded.audio_url, loadTime);

                                VideoProtocolInfo.EventOptionStatus clickFailed = optionCustom.click_failed;
                                addResourseMap(clickFailed.image_url, loadTime);
                                addResourseMap(clickFailed.audio_url, loadTime);

                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加需要下载的资源
     *
     * @param url
     * @param time
     */
    private void addResourseMap(String url, long time) {
        if (!NativeViewUtils.isNullOrEmptyString(url) && !new File(NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(url)).exists()) {
            if (!resourseMap.containsKey(url) || time < resourseMap.get(url)) {
                resourseMap.put(url, time);
            }
        }
    }


    // 轮询器
    private final Runnable mTicker = new Runnable() {
        public void run() {


            if (listener != null) {
                long currentPosition = listener.getPlayerCurrentTime();

                downLoadResouse(currentPosition);
                dealwithProtocol(currentPosition);

                long now = SystemClock.uptimeMillis();
                long next = now + (delay - now % delay);

                if (handler != null) {
                    handler.removeCallbacks(mTicker);
                    handler.postAtTime(mTicker, next);
                }
            }
        }
    };

    /**
     * 下载资源文件
     *
     * @param currentPosition
     */
    private void downLoadResouse(long currentPosition) {

        Set<String> keys = resourseMap.keySet();

        for (String key : keys) {
            Long time = resourseMap.get(key);
            if (currentPosition > time) {
//                LogUtils.d("downLoadResouse", "下载文件---" + key);


                File file = new File(NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(key));
                if (!file.exists() && !downloading.contains(key)) {
                    HttpClient.getInstanse().download(key, NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(key), new DownloadCallback() {
                        @Override
                        public void onDownloadStart(String url) {
                            LogUtils.d(TAG, "onDownloadStart----url---" + url);
                            downloading.add(url);
                        }

                        @Override
                        public void onDownloadFailed(String url, String error) {
                            downloading.remove(url);
                            LogUtils.d(TAG, "onDownloadFailed----url---" + url);

                        }

                        @Override
                        public void onDownloadSuccess(String url, File file) {
                            LogUtils.d(TAG, "onDownloadSuccess----url---" + url);
                            downloading.remove(url);

                            downloadFinish.add(url);
                        }

                        @Override
                        public void onDownloading(String url, int progress) {
//                            LogUtils.d("downLoadResouse", "onDownloadStart----url---" + url);
                        }
                    });
                }
            }
        }

        // 删除已经下载好的资源
        if (downloadFinish != null) {
            for (String url : downloadFinish) {
                resourseMap.remove(url);
            }
            downloadFinish.clear();
        }

    }


    /**
     * 处理协议
     */
    private void dealwithProtocol(long currentPosition) {

//        LogUtils.d("dealwithProtocol", new Gson().toJson(resourseMap));

        if (videoProtocolInfo == null)
            return;

        VideoProtocolInfo.Protocol protocol = videoProtocolInfo.protocol;
        if (protocol == null)
            return;

        List<VideoProtocolInfo.EventRail> eventRails = protocol.event_list;
        if (eventRails == null || eventRails.isEmpty())
            return;

        VideoProtocolInfo.ReleaseInfo releaseInfo = videoProtocolInfo.release_info;
        if (releaseInfo == null)
            return;

        VideoProtocolInfo.VideoParams videoParams = releaseInfo.v_params;
        if (videoParams == null)
            return;

        //事件轨道
        for (VideoProtocolInfo.EventRail eventRail : eventRails) {

            // 隐藏轨道
            if (eventRail.hide_track) {
                continue;
            }
            List<VideoProtocolInfo.EventComponent> eventComponents = eventRail.obj_list;
            if (eventComponents != null && !eventComponents.isEmpty()) {


                //事件组件
                for (VideoProtocolInfo.EventComponent eventComponent : eventComponents) {

                    long startTime = (long) (eventComponent.start_time * 1000);
                    long endTime = (long) (eventComponent.end_time * 1000);

//                    LogUtils.d(TAG, "startTime=" + startTime + "---endTime=" + endTime);


//                    float startFrame = startTime / 40;
//                    float endFrame = endTime / 40;
//                    LogUtils.d(TAG, "startFrame=" + startFrame + "---endFrame=" + endFrame);


                    //事件触发 UI渲染
                    if (currentPosition >= startTime && currentPosition < endTime) {
                        componentManager.eventScopeIn(eventComponent);
                    } else {
                        componentManager.eventScopeOut(eventComponent);
                    }


                    if (eventComponent.startIsActive && (currentPosition < startTime || currentPosition >= (startTime + 40))) {//事件开始点
                        eventComponent.startIsActive = false;
                    } else if (eventComponent.endIsActive && (currentPosition < (endTime - 40) || currentPosition >= endTime)) {//事件结束点
                        eventComponent.endIsActive = false;
                    }


                    if (eventComponent.eventIsActive && (currentPosition < startTime || currentPosition >= endTime)) {//事件范围内
                        eventComponent.eventIsActive = false;
                    }


                    if (!eventComponent.startIsActive && currentPosition >= startTime && currentPosition < (startTime + 40)) {
                        LogUtils.d(TAG, "事件开始----" + currentPosition + "----------" + eventComponent.event_id);
                        eventComponent.startIsActive = true;

                    } else if (!eventComponent.endIsActive && currentPosition >= (endTime - 40) && currentPosition < endTime) {

                        LogUtils.d(TAG, "事件结束----" + currentPosition + "----------" + eventComponent.event_id);
                        eventComponent.endIsActive = true;
                        componentManager.eventEnd(eventComponent);
                    }

                    //事件范围内
                    if (!eventComponent.eventIsActive && currentPosition >= startTime && currentPosition < endTime && currentPosition > 0) {
                        eventComponent.eventIsActive = true;

                        LogUtils.d(TAG, "事件范围内----" + currentPosition + "----------" + eventComponent.event_id);
                        componentManager.eventIn(eventComponent);

                    }
                }

            }
        }
    }

    /**
     * 事件信息回调
     *
     * @param action
     */
    @Override
    public void onEventCallback(String action) {
        if (listener != null) {
            listener.onEventCallback(action);
        }
    }

    @Override
    public void onComponentSeek(long position) {
        if (listener != null) {
            listener.seekToTime(position);
        }
    }

    @Override
    public void onShowBottomControllerView(boolean show) {
        if (mControllerView != null) {
            mControllerView.setBottomViewShowable(show);
        }
    }

    @Override
    public void ctrlPlayer(boolean play) {
        if (play && PlayerState.STATE_ONPLAY.equals(playerState))
            return;

        if (!play && !PlayerState.STATE_ONPLAY.equals(playerState))
            return;

        if (listener != null) {

            listener.ctrlPlayer(play ? "play" : "pause");
        }
    }

    /**
     * 跳转网页链接
     *
     * @param href_url
     */
    @Override
    public void onHrefUrl(String href_url) {
        if (listener != null) {
            boolean impl = listener.onHrefUrl(href_url);
            if (!impl) {

                Intent intent = new Intent(getContext(), WebActivity.class);
                intent.putExtra("href_url", href_url);
                getContext().startActivity(intent);
            }
        }
    }

    /**
     * 拨打电话
     *
     * @param call_phone
     */
    @Override
    public void callPhone(String call_phone) {
        if (listener != null) {
            boolean impl = listener.onCallPhone(call_phone);
            if (!impl) {

                if (Build.VERSION.SDK_INT < 23 || getContext().checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Intent mIntent = new Intent(Intent.ACTION_DIAL);
                    mIntent.setData(Uri.parse("tel:" + call_phone));
                    getContext().startActivity(mIntent);
                }

            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        //重置后恢复
        LogUtils.d(TAG, "onResume");

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        //重置后恢复
        LogUtils.d(TAG, "onPause");
        if (!playBackground) {
            SoundManager.getInstance().release();
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy");

//        if (handler != null) {
//            handler.removeCallbacks(mTicker);
//            handler=null;
//        }
//
//        SoundManager.getInstance().release();

        release();
    }
}
