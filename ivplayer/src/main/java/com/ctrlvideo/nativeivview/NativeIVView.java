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
import com.ctrlvideo.comment.net.GetIVideoInfoCallback;
import com.ctrlvideo.comment.net.HttpClient;
import com.ctrlvideo.comment.net.VideoProtocolInfo;
import com.ctrlvideo.ivplayer.R;


public class NativeIVView extends RelativeLayout implements LifecycleObserver, IView {


    protected String TAG = "NativeIVView";

    //轮询间隔
    private long delay = 1000 / 25;

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
        HttpClient.getInstanse().getIVideoInfo(new GetIVideoInfoCallback() {

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
        nowViewStatus = ViewState.STATE_READIED;
        listener.onIVViewStateChanged(nowViewStatus, videoProtocolInfo.release_info.url);

//        Log.d(TAG, "currentTime=" + current);

        String url=videoProtocolInfo.protocol.event_list.get(2).obj_list.get(0).options.get(0).custom.click_default.image_url;

        Log.d(TAG, "url=" + url);


        getHandler().removeCallbacks(mTicker);
        getHandler().post(mTicker);
    }

    private final Runnable mTicker = new Runnable() {
        public void run() {


            long current = listener.getPlayerCurrentTime();
            Log.d(TAG, "currentTime=" + current);
            dealwithProtocol();

            long now = SystemClock.uptimeMillis();
            long next = now + (delay - now % delay);
            getHandler().postAtTime(mTicker, next);

        }
    };


    /**
     * 处理协议
     */
    private void dealwithProtocol() {

    }

    @Override
    public void onPlayerStateChanged(String status) {


        if (nowViewStatus.equals(ViewState.STATE_READIED)) {

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

    }
}
