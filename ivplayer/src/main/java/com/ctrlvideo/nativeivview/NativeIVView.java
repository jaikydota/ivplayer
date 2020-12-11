package com.ctrlvideo.nativeivview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected String TAG = "NativeIVView";

    //轮询间隔
    private long delay = 36;
    private long hideControllerViewDelay = 5000;

    //当前视图状态
    private String nowViewStatus = ViewState.STATE_LOADING;

    //是否测试网环境
    private boolean isTestEnv = false;

    //当前currentTime
    private String lastTime = "0";

    //项目地址
    private String mPid;
    //播放配置的url
    private String config_url = "";

    protected RelativeLayout rlWVContainer;

    //事件监听器
    private IVViewListener listener = null;

    protected VideoProtocolInfo videoProtocolInfo;


    public NativeIVView(Context context) {
        super(context);
        initView(context);
    }

    public NativeIVView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NativeIVView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (isInEditMode()) {
            return;
        }
        LayoutInflater.from(context).inflate(R.layout.view_native, this, true);

        rlWVContainer = findViewById(R.id.rlWVContainer);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIVViewClick("");
                if (mControllerView != null) {
//                    showControllerView(mControllerView.getVisibility() == View.GONE);

                    mControllerView.onClick();
                }


//                showControllerView(mControllerView.getVisibility() == View.GONE);
            }
        });

    }


//    private void showControllerView(boolean show) {
//
//        if (mControllerView != null) {
//            if (show) {
//
//                mControllerView.setVisibility(View.VISIBLE);
//                handler.removeMessages(MES_HIDEVIEW);
//                handler.sendEmptyMessageDelayed(MES_HIDEVIEW, hideControllerViewDelay);
//
//            } else {
//
//                mControllerView.setVisibility(View.GONE);
//                handler.removeMessages(MES_HIDEVIEW);
//            }
//        }
//    }

    Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

//            showControllerView(false);
        }
    };


    /**
     * 初始化播控
     *
     * @param releaseInfo
     */
    private void initControlView(VideoProtocolInfo.ReleaseInfo releaseInfo) {
        if (mControllerView != null) {
            removeView(mControllerView);
            mControllerView = null;
        }

        if (releaseInfo != null) {
            VideoProtocolInfo.PlayerController playerController = releaseInfo.getPlayerController();
            if (playerController.isShowContrller()) {
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
                mControllerView.initController(playerController);
                addView(mControllerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


//                showControllerView(true);
            }
        }
    }


    private ControllerView mControllerView;


    @Override
    public void initIVView(@Nullable String pid, @Nullable String config_url, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext) {
        this.initIVView(pid, config_url, ivViewListener, mContext, false);
    }

    @Override
    public void initIVView(@Nullable String pid, @Nullable String config_url, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext, boolean openTestEnv) {


        isTestEnv = openTestEnv;
        lastTime = "0";

        this.mPid = pid == null ? "" : pid;
        this.config_url = config_url == null ? "" : config_url;
        this.listener = ivViewListener;

        if (mContext instanceof LifecycleOwner)
            ((LifecycleOwner) mContext).getLifecycle().addObserver(this);
        initData();

    }

    /**
     * 加载互动协议数据
     */
    private void initData() {
        nowViewStatus = ViewState.STATE_GET_IV_INFO;
        HttpClient.getInstanse().getIVideoInfo(mPid, new GetIVideoInfoCallback() {

            @Override
            protected void onFailure(String error) {
                LogUtils.d("onFailure", error);


                post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError("get_config_failed");
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
    }


    private void onLoadVideoInfoFinish(VideoProtocolInfo videoProtocolInfo) {

        this.videoProtocolInfo = videoProtocolInfo;
        componentManager = new ComponentManager();
        nowViewStatus = ViewState.STATE_READIED;

        initControlView(videoProtocolInfo.release_info);

        listener.onIVViewStateChanged(nowViewStatus, videoProtocolInfo.release_info.url);
        listener.onEventCallback(new EventIntractInfoCallback(videoProtocolInfo).toJson());


        SoundManager.getInstance().release();
        rlWVContainer.removeAllViews();

        componentManager.initParmas(rlWVContainer, videoProtocolInfo, this);

//        LogUtils.d(TAG, "currentTime=" + current);

//        String url = videoProtocolInfo.protocol.event_list.get(2).obj_list.get(0).options.get(0).custom.click_default.image_url;

//        LogUtils.d(TAG, "url=" + url);
        preloadResouse();

        getHandler().removeCallbacks(mTicker);
        getHandler().post(mTicker);
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


    private Map<String, Long> resourseMap = new HashMap<String, Long>();


    private long systemTime;


    private final Runnable mTicker = new Runnable() {
        public void run() {


            long currentPosition = listener.getPlayerCurrentTime();


//            long newTime = System.currentTimeMillis();
//            LogUtils.d(TAG, "currentTime=" + currentPosition + "---------------offst=" + (newTime - systemTime));
//            systemTime = newTime;

            downLoadResouse(currentPosition);
            dealwithProtocol(currentPosition);

            long now = SystemClock.uptimeMillis();
            long next = now + (delay - now % delay);
            getHandler().removeCallbacks(mTicker);
            getHandler().postAtTime(mTicker, next);

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


//        LogUtils.d("downLoadResouse", "-----------------------------------");

    }

    //正在下载的资源
    private List<String> downloading = new ArrayList<>();
    //已经下载的资源
    private List<String> downloadFinish = new ArrayList<>();

    private ComponentManager componentManager;


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


    // 视频播放状态
    private String playerState;

    @Override
    public void onPlayerStateChanged(String status) {
        LogUtils.d(TAG, "onPlayerStateChanged----status=" + status);

        this.playerState = status;

        if (nowViewStatus.equals(ViewState.STATE_READIED)) {
//            videoPlaying = "onplay".equals(status);
            componentManager.setVideoPlayerStatus(status);
            if (mControllerView != null) {
                mControllerView.setVideoPlayerStatus(status);
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        //重置后恢复
        LogUtils.d(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy");

        getHandler().removeCallbacks(mTicker);
        if (handler != null) {
            handler.removeMessages(MES_HIDEVIEW);
            handler = null;
        }


        SoundManager.getInstance().release();


    }

    private int MES_HIDEVIEW = 1000;


    @Override
    public void onEventCallback(String action) {
        if (listener != null) {
            listener.onEventCallback(action);
        }
    }

    @Override
    public void onShowBottomControllerView(boolean show) {

        if (mControllerView != null) {
            mControllerView.setBottomViewShowable(show);
        }
    }

    /**
     * 组件回调
     *
     * @param position
     */
    @Override
    public void onComponentSeek(long position) {
        if (listener != null) {
            listener.seekToTime(position);
        }
    }

//    @Override
//    public boolean isVideoPlaying() {
//        return videoPlaying;
//    }

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

    @Override
    public void callPhone(String call_phone) {

        if (listener != null) {
            listener.onHrefUrl("tel:" + call_phone);
        }
    }

    @Override
    public void hrefUrl(String href_url) {
        if (listener != null) {
            boolean impl = listener.onHrefUrl(href_url);
            if (!impl) {

                Intent intent=new Intent(getContext(), WebActivity.class);
                intent.putExtra("href_url",href_url);
                getContext().startActivity(intent);
            }
        }
    }

}
