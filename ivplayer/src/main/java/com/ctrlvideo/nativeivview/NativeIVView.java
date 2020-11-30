package com.ctrlvideo.nativeivview;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
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
import com.ctrlvideo.comment.net.DownloadCallback;
import com.ctrlvideo.comment.net.GetIVideoInfoCallback;
import com.ctrlvideo.comment.net.HttpClient;
import com.ctrlvideo.comment.net.VideoProtocolInfo;
import com.ctrlvideo.ivplayer.R;

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
        rlWVContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onIVViewClick("");
            }
        });
    }

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
                Log.d("onFailure", error);


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
                        Log.d("onResponse", videoProtocolInfo.protocol.auto_indent + "");
                        onLoadVideoInfoFinish(videoProtocolInfo);
                    }
                });
            }
        });
    }


    private void onLoadVideoInfoFinish(VideoProtocolInfo videoProtocolInfo) {

        this.videoProtocolInfo = videoProtocolInfo;
        nowViewStatus = ViewState.STATE_READIED;
        listener.onIVViewStateChanged(nowViewStatus, videoProtocolInfo.release_info.url);


        SoundManager.getInstance().release();
        rlWVContainer.removeAllViews();
        componentManger = new ComponentManger();
        componentManger.initParmas(rlWVContainer, videoProtocolInfo, this);

//        Log.d(TAG, "currentTime=" + current);

//        String url = videoProtocolInfo.protocol.event_list.get(2).obj_list.get(0).options.get(0).custom.click_default.image_url;

//        Log.d(TAG, "url=" + url);
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
        if (!NativeViewUtils.isNullOrEmptyString(url) && !new File(NativeViewUtils.getDowmloadFilePath(), NativeViewUtils.getFileName(url)).exists()) {
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


            long newTime = System.currentTimeMillis();


            Log.d(TAG, "currentTime=" + currentPosition + "---------------offst=" + (newTime - systemTime));

            systemTime = newTime;

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
//                Log.d("downLoadResouse", "下载文件---" + key);


                File file = new File(NativeViewUtils.getDowmloadFilePath(), NativeViewUtils.getFileName(key));
                if (!file.exists() && !downloading.contains(key)) {
                    HttpClient.getInstanse().download(key, NativeViewUtils.getDowmloadFilePath(), NativeViewUtils.getFileName(key), new DownloadCallback() {
                        @Override
                        public void onDownloadStart(String url) {
                            Log.d(TAG, "onDownloadStart----url---" + url);
                            downloading.add(url);
                        }

                        @Override
                        public void onDownloadFailed(String url, String error) {
                            downloading.remove(url);
                            Log.d(TAG, "onDownloadFailed----url---" + url);

                        }

                        @Override
                        public void onDownloadSuccess(String url, File file) {
                            Log.d(TAG, "onDownloadSuccess----url---" + url);
                            downloading.remove(url);

                            downloadFinish.add(url);
                        }

                        @Override
                        public void onDownloading(String url, int progress) {
//                            Log.d("downLoadResouse", "onDownloadStart----url---" + url);
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


//        Log.d("downLoadResouse", "-----------------------------------");

    }

    //正在下载的资源
    private List<String> downloading = new ArrayList<>();
    //已经下载的资源
    private List<String> downloadFinish = new ArrayList<>();

    private ComponentManger componentManger;


    /**
     * 处理协议
     */
    private void dealwithProtocol(long currentPosition) {

//        Log.d("dealwithProtocol", new Gson().toJson(resourseMap));

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

//                    Log.d(TAG, "startTime=" + startTime + "---endTime=" + endTime);


//                    float startFrame = startTime / 40;
//                    float endFrame = endTime / 40;
//                    Log.d(TAG, "startFrame=" + startFrame + "---endFrame=" + endFrame);


                    //事件触发
                    if (currentPosition >= startTime && currentPosition < endTime) {
                        componentManger.eventTrigger(eventComponent);
                    } else {
                        componentManger.eventJumpout(eventComponent);
                    }

                    if (eventComponent.endIsActive && (currentPosition < (endTime - 40) || currentPosition >= endTime)) {
                        eventComponent.endIsActive = false;
                    }

                    if (!eventComponent.endIsActive && currentPosition >= (endTime - 40) && currentPosition < endTime) {

                        Log.d(TAG, "事件结束----" + currentPosition);
                        eventComponent.endIsActive = true;
                        componentManger.componentEnd(eventComponent);
                    }
                }

            }
        }


    }


    private boolean videoPlaying;

    @Override
    public void onPlayerStateChanged(String status) {


        if (nowViewStatus.equals(ViewState.STATE_READIED)) {
            videoPlaying = "onplay".equals(status);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        //重置后恢复
        Log.d(TAG, "onResume");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        getHandler().removeCallbacks(mTicker);

        SoundManager.getInstance().release();


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

    @Override
    public boolean isVideoPlaying() {
        return videoPlaying;
    }

    @Override
    public void ctrlPlayer(boolean play) {
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
            listener.onHrefUrl(href_url);
        }
    }

}
