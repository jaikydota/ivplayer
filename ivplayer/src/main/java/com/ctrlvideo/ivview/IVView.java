package com.ctrlvideo.ivview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.ctrlvideo.comment.IVViewListener;
import com.ctrlvideo.comment.IView;
import com.ctrlvideo.comment.ViewState;
import com.ctrlvideo.ivplayer.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Author by Jaiky, Date on 2020/4/8.
 */
@SuppressLint("NewApi")
public class IVView extends RelativeLayout implements IView {

    protected String TAG = "IVSDKView";

    protected WebView webView;
    protected RelativeLayout rlWVContainer;
    private MyChromeClient chromeClient = null;

    //事件监听器
    private IVViewListener listener = null;

    //项目地址
    private String mPid;
    //播放配置的url
    private String config_url = "";

    //当前视图状态
    private String nowViewStatus = ViewState.STATE_LOADING;

    //是否纯净模式
    private boolean isPureMode = false;
    //是否使用本地录音模式
    private boolean isSelfRecord = false;

    //是否测试网环境
    private boolean isTestEnv = false;

    //当前currentTime
    private String lastTime = "0";

    //是否启用功能
    private boolean enableFeature = true;

    public IVView(Context context) {
        super(context);
        initView(context);
    }

    public IVView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public IVView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (isInEditMode()) {
            return;
        }
        RelativeLayout inflate = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_webview, this, true);
        rlWVContainer = findViewById(R.id.rlWVContainer);
        webView = findViewById(R.id.webvContainer);
        webView.setBackgroundColor(0);
        webView.getBackground().setAlpha(0);

        //安卓5.0以上启用功能
        enableFeature = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

        try {
            String userAgentString = webView.getSettings().getUserAgentString();
            Log.d(TAG, "UA: " + userAgentString);
            String[] ugArr = userAgentString.split(" ");
            for (String info : ugArr) {
                if (info.startsWith("Chrome")) {
                    String version = info.split("/")[1];
                    int versionInt = Integer.parseInt(version.split("\\.")[0]);
                    Log.d(TAG, "Webview Chorme Version: " + version + " " + versionInt);
                    //小于41的版本不开启
                    if (versionInt < 41) {
                        enableFeature = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (IVUtils.isBBKStudentPhone())
            enableFeature = false;
    }


    @Override
    public void onResume() {
        //重置后恢复
        setPureMode(false);
    }

    @Override
    public void onPause() {
        //暂停后，设置纯净模式
        setPureMode(true);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "IVView onDestroy");
    }

    @Override
    public void release() {

    }

    //是否启用
    public boolean isEnable() {
        return enableFeature;
    }

//    @Override
//    public void initIVView(@Nullable String pid, @Nullable String config_url, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext) {
//        this.initIVView(pid, config_url, ivViewListener, mContext, false);
//    }

    @Override
    public void initIVViewPid(@Nullable String pid, @Nullable String channel, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext) {
        this.mPid = pid == null ? "" : pid;
        init(ivViewListener, mContext);
    }

    @Override
    public void initIVView(@Nullable String config_url, @Nullable String channel, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext) {
        this.config_url = config_url == null ? "" : config_url;
        init(ivViewListener, mContext);
    }

    private void init(IVViewListener ivViewListener, Activity mContext) {
        nowViewStatus = ViewState.STATE_LOADING;

        lastTime = "0";
        lastEventState = "";
        lastEventTime = 0L;

        interruptEvent();


        this.listener = ivViewListener;

        if (enableFeature) {
            initWebView();
        } else {
            //如果小于API 21,通知无法使用
            this.listener.onError("android_api_too_lower");
        }
    }

//    @Override
//    public void initIVView(@Nullable String pid, @Nullable String config_url, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext, boolean openTestEnv) {
//        Log.d(TAG, "initIVView: " + pid + " OpenTestEnv: " + openTestEnv);
//        nowViewStatus = ViewState.STATE_LOADING;
//
//        lastTime = "0";
//        lastEventState = "";
//        lastEventTime = 0L;
//
//        interruptEvent();
//
//        this.mPid = pid == null ? "" : pid;
//        this.config_url = config_url == null ? "" : config_url;
//        this.listener = ivViewListener;
//
//        if (enableFeature) {
//            //绑定生命周期
//            if (mContext instanceof LifecycleOwner)
//                ((LifecycleOwner) mContext).getLifecycle().addObserver(this);
//
//            initWebView();
//        } else {
//            //如果小于API 21,通知无法使用
//            this.listener.onError("android_api_too_lower");
//        }
//    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void initWebView() {
        //移除webview，新建一个，防止出现白屏BUG（一直白屏BUG，和低版本一闪而逝白屏BUG）
        rlWVContainer.removeView(webView);
        webView = new WebView(this.getContext());
        webView.setId(R.id.webvContainer);
        webView.setBackgroundResource(android.R.color.transparent);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rlWVContainer.addView(webView, params);

        //设置基础属性
        webView.setBackgroundColor(0);
        webView.getBackground().setAlpha(0);
        webView.setVisibility(View.GONE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //webview 通讯参考 https://docs.qq.com/doc/DTUpZcXlobWZVQm1G
        webView.addJavascriptInterface(this, "IvSDKAndroid");

        chromeClient = new MyChromeClient();
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);

        //部分设备即便满足了 SDK 的 API 大于 安卓4.4，仍然不支持该配置，try后解决
        try {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setLoadWithOverviewMode(true);

//        webView.loadUrl("http://192.168.3.156:3102/");
        //如果是测试环境
        if (isTestEnv)
            webView.loadUrl("https://ivetest.ctrlvideo.com/jssdk/native024.html");
        else
            webView.loadUrl("https://ive.ctrlvideo.com/jssdk/native025.html");

//        webView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                Log.d(TAG, "onTouchEvent ");
//                switch (event.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        //do something......
//                        Log.d(TAG, "ACTION_DOWN ");
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//
//                        //do something......
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        //do something......
//
//                        break;
//                }
//                return false;
//            }
//        });

    }


    /**
     * 是否纯净模式
     * @return true 纯净模式， false 非纯净模式
     */
    public boolean isPureMode() {
        return isPureMode;
    }

    /**
     * 打开纯净模式
     *
     * @param isOpen 是否打开
     */
    @Override
    public void setPureMode(boolean isOpen) {
        if (enableFeature) {
            Log.d(TAG, "setPureMode: " + isOpen);
            isPureMode = isOpen;
            if (isOpen) {
                this.setVisibility(View.GONE);
                //事件内中断事件
                interruptEvent();
            } else
                this.setVisibility(View.VISIBLE);
        }
    }



    //使用本地录音模式
    public void useSelfRecord(boolean isOpen) {
        isSelfRecord = isOpen;
    }


    /**
     * 执行action行为
     */
    public void performAction(String action) {
        if (enableFeature) {
            Log.d(TAG, "performAction: " + action);

            //重置这个值
            lastEventState = "";
            lastEventTime = 0L;

            //中断事件行为
            if (action.equals(IVEvent.EventAction.INTERRUPT_EVENT)) {
                interruptEvent();
            }
            //中断事件行为，并重新进入起始点
            else if (action.equals(IVEvent.EventAction.SKIP_PREPARE_TIME)) {
                interruptEvent();
                if (nowEventPrepareTime >= 0) {
                    //向前1秒
                    long seekTime = nowEventPrepareTime - 1000;
                    if (seekTime < 0)
                        seekTime = 0;
                    listener.seekToTime(seekTime);
                    nowEventPrepareTime = -1;
                }
            }
        }
    }


    //调用JS开始暂停播放 status "onplay" 播放，"onpause" 暂停
    @Override
    public void onPlayerStateChanged(String status) {
        if (enableFeature) {
            Log.d(TAG, "evalJS OnPlayerStateChanged " + status);
            webView.evaluateJavascript("javascript:onSDKPlayerStateChanged('" + status + "')", null);
        }
    }

    //当播放器seek时
    public void onPlayerSeek(long time) {
        //重置这个值
        lastEventState = "";
        lastEventTime = 0L;
    }

    //调用语音识别结果
    public void recognResultSend(String result) {
        if (enableFeature) {
            Log.d(TAG, "evalJS recognTextSend " + result);
            webView.evaluateJavascript("javascript:recognTextSend('" + result + "')", null);
        }
    }


    //中断视频录制和录音
    private void interruptEvent() {
        stopSpeechRecord();
    }

    //停止录音
    private void stopSpeechRecord() {
        if (isSelfRecord)
            webView.evaluateJavascript("javascript:stopSpeechRecord()", null);
    }

    //SDK初始化
    private void evalJSNativeSDKInit(String pid, String config_url) {
        Log.d(TAG, "evalJS NativeSDKInit " + "javascript:nativeSDKInit('" + pid + "', 'ivplayer', '" + config_url + "', " + isSelfRecord + ")");
        webView.evaluateJavascript("javascript:nativeSDKInit('" + pid + "', 'ivplayer', '" + config_url + "', " + isSelfRecord + ")", null);
    }

    //手势识别结果上报
    private void sendGestureResult(String key) {
        Log.d(TAG, "evalJS gestureResultSend " + key);
        recognKeys = "";
        recognSuccNum = 0;
        webView.evaluateJavascript("javascript:gestureResultSend('success', '" + key + "')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String str) {

            }
        });
    }


    //需要识别的关键词
    private String recognKeys = "";
    //需要识别的key和识别成功次数
    private int recognSuccNum = 0;
    //当前事件的 prepareTime
    private long nowEventPrepareTime = -1;


    //TODO 解决部分机器上重复下发prepare的bug
    private String lastEventState = "";
    private long lastEventTime = 0L;

    /**
     * 录音状态改变
     *
     * @param state 状态
     */
    @JavascriptInterface
    public void onRecordChanged(String state, String time) {
        Log.d(TAG, "onRecordChanged " + state + "  time " + time);
        long mmTime = IVUtils.getMMTime(time);


        this.post(new Runnable() {
            @Override
            public void run() {
                //TODO 暂未找到bug原因，重复下发的不理会
                if (lastEventState.equals(state) && lastEventTime == mmTime)
                    return;
                lastEventState = state;
                lastEventTime = mmTime;

//                listener.onEventStateChanged(IVEvent.EVENT_SPEECHRECOGN, state, mmTime);

                if (state.equals("prepare")) {
                    nowEventPrepareTime = mmTime;
                } else if (state.equals("start")) {
                } else {
                    nowEventPrepareTime = -1;
                }
            }
        });
    }


    /**
     * web网页状态改变
     *
     * @param state 状态，"onReadied" 当ui逻辑初始化完成
     */
    @JavascriptInterface
    public void webPageStateChanged(String state, String data) {
        Log.d(TAG, "webPageStateChanged " + state + "  " + data);
        nowViewStatus = state;

        //如果是互动点信息下发
        if (state.equals(ViewState.STATE_GET_IV_INFO))
            data = data.replaceAll("#douhao#", ",");

//        Log.d(TAG, data);
        final String result = data;
        this.post(new Runnable() {
            @Override
            public void run() {
                listener.onIVViewStateChanged(nowViewStatus, result);
            }
        });
    }

    /**
     * 获取播放时间
     *
     * @return 当前播放时间，单位：秒 [如有小数带小数]
     */
    @JavascriptInterface
    public String getCurrentTime() {
        //纯净模式返回
        if (isPureMode) {
            Log.d("showGetCurrentTime", "getCurrentTime " + lastTime);
            return lastTime;
        } else {
            float time = ((float) listener.getPlayerCurrentTime() / 1000);
            lastTime = time + "";
            Log.d("showGetCurrentTime", "getCurrentTime " + lastTime);
            return lastTime;
        }
    }

    /**
     * 设置播放时间
     *
     * @param time 传过来的时间，单位：秒 [带小数的字符串]
     */
    @JavascriptInterface
    public void setCurrentTime(String time) {
        Log.d(TAG, "setCurrentTime " + time);

        long mmTime = IVUtils.getMMTime(time);
        this.post(new Runnable() {
            @Override
            public void run() {
                listener.seekToTime(mmTime);
            }
        });
    }

    /**
     * 执行播放/暂停
     *
     * @param state 是否播放，如播放，传“play”，暂停传“pause”
     */
    @JavascriptInterface
    public void playPauseVideo(final String state) {
        Log.d(TAG, "playPauseVideo " + state);

        this.post(new Runnable() {
            @Override
            public void run() {
                if (state.equals("play")) {
                    listener.ctrlPlayer("play");
                } else if (state.equals("pause")) {
                    listener.ctrlPlayer("pause");
                }
            }
        });
    }


    /**
     * 当view点击空白处时 [如点击webview中控件将阻止向上冒泡]
     *
     * @param info 点击区域信息
     */
    @JavascriptInterface
    public void ivViewOnClick(final String info) {
        Log.d(TAG, "ivViewOnClick " + info);

//        this.post(new Runnable() {
//            @Override
//            public void run() {
//                listener.onIVViewClick(info);
//            }
//        });
    }

    /**
     * 当webview发生错误时
     *
     * @param errorInfo 点击区域信息
     */
    @JavascriptInterface
    public void onIvViewError(final String errorInfo) {
        Log.d(TAG, "onIvViewError " + errorInfo);

        this.post(new Runnable() {
            @Override
            public void run() {
                listener.onError(errorInfo);
            }
        });
    }


    /**
     * 当webview收到自定义通知时
     *
     * @param msg 消息
     */
    @JavascriptInterface
    public void onCustomNotify(final String msg) {
        Log.d(TAG, "onCustomNotify " + msg);

        this.post(new Runnable() {
            @Override
            public void run() {
                listener.onCustomNotify(msg);
            }
        });
    }

    //webview chromeclient
    public class MyChromeClient extends WebChromeClient {


        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
        }

        @Override
        public void onHideCustomView() {
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            Log.d(TAG, "onPermissionRequest ");
            request.grant(request.getResources());
        }
    }


    class MyWebViewClient extends WebViewClient {

        // 重写父类方法，让新打开的网页在当前的WebView中显示
        //当返回true时，你点任何链接都是失效的，需要你自己跳转。return false时webview会自己跳转。
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            evalJSNativeSDKInit(mPid, config_url);
            super.onPageFinished(view, url);
            //页面加载完成后，初始化sdk
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e(TAG, "Webview onReceivedError: " + errorCode + " " + description + " " + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }


}
