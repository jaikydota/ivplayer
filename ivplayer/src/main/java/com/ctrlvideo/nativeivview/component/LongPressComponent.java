package com.ctrlvideo.nativeivview.component;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.ctrlvideo.nativeivview.audioplayer.SoundManager;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;
import com.ctrlvideo.nativeivview.widget.OptionView;

import java.io.File;
import java.util.List;


/**
 * 重复点击事件组件
 */
public class LongPressComponent extends BaseComponent {

    private String TAG = "LongPressComponent";


//    private VideoProtocolInfo.EventComponent eventComponent;

    private boolean loadFinish;

    public LongPressComponent(Context context) {
        this(context, null);
    }

    public LongPressComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongPressComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnOptionLongPressListener(OnOptionLongPressListener listener) {
        this.listener = listener;
    }

    public void setOnShowResultListener(OnComponentResultListener listener) {

        this.showResultListener = listener;
    }

    private OnOptionLongPressListener listener;
    private OnComponentResultListener showResultListener;

    public interface OnOptionLongPressListener {
        void onOptionLongPress(int option);
    }

    /**
     * 检查全部控件是否加载完成 主要针对控件在视频前面几秒
     */
    public void checkLoadFinish() {

        if (loadFinish) {
            return;
        }


        boolean finish = true;
        int count = getChildCount();
        for (int index = 0; index < count; index++) {

            View view = getChildAt(index);
            if (view instanceof OptionView) {
                OptionView optionView = (OptionView) view;
                if (!optionView.isLoadFinish()) {
                    finish = false;
                    optionView.reload();
                }
            }
        }
        loadFinish = finish;

    }


    public void initComponent(int status, VideoProtocolInfo.EventComponent eventComponent) {

        this.eventComponent = eventComponent;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options == null || options.isEmpty())
            return;


        for (int i = 0; i < options.size(); i++) {

            VideoProtocolInfo.EventOption option = options.get(i);

            if (option.hide_option) {
                continue;
            }

            if ("progresser".equals(option.type)) {
                continue;
            }

            option.containerWidth = parentWidth;
            option.containerHeight = parentHeight;
            option.videoWidth = videoWidth;
            option.videoHeight = videoHeight;


//            boolean align_screen = option.align_screen;


//            float width = 0;
//            float height = 0;
//
//            float left = 0;
//            float top = 0;
//
//            VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
//            if (optionStyle != null) {
//                if (align_screen) {
//                    width = parentWidth * optionStyle.width / 100;
//                    height = parentHeight * optionStyle.height / 100;
//                    left = parentWidth * optionStyle.left / 100;
//                    top = parentHeight * optionStyle.top / 100;
//                } else {
//                    width = videoWidth * optionStyle.width / 100;
//                    height = videoHeight * optionStyle.height / 100;
//                    left = videoWidth * optionStyle.left / 100 + ((parentWidth - videoWidth) / 2);
//                    top = videoHeight * optionStyle.top / 100 + ((parentHeight - videoHeight) / 2);
//                }
//            }


            OptionView optionView = new OptionView(getContext());
            optionView.setTag(option.option_id);
//            optionView.initParmas(parentWidth, parentHeight, videoWidth, videoHeight, width, height);
            optionView.setOption(status, option);

            if (option.blink) {
                AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
                alphaAnimation1.setDuration(1000);
                alphaAnimation1.setRepeatCount(Animation.INFINITE);
                alphaAnimation1.setRepeatMode(Animation.RESTART);
                optionView.setAnimation(alphaAnimation1);
                alphaAnimation1.start();
            }

            int finalI = i;
            optionView.setOnOptionViewListener(new OptionView.OnOptionViewListener() {
                @Override
                public void onTrigger() {

//                    LogUtils.d(TAG, "onTrigger");

                    optionView.setOption(OptionView.STATUS_CLICK_ON, option);


                    if (option.custom != null && option.custom.click_on != null) {

                        String audioUrl = option.custom.click_on.audio_url;
                        if (!NativeViewUtils.isNullOrEmptyString(audioUrl)) {

                            File localFile = new File(NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(audioUrl));
                            if (localFile.exists()) {
                                SoundManager.getInstance().play(localFile.getAbsolutePath());
                            }
                        }
                    }

                    optionIndex = finalI;
                    if (handler != null) {
                        handler.sendEmptyMessageDelayed(1, eventComponent.longpress_time * 1000);
                    }

                }

                @Override
                public void onTriggerAfter() {
//                    LogUtils.d(TAG, "onTriggerAfter");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);

                    if (handler != null) {
                        handler.removeMessages(1);
                    }

                }

                @Override
                public void onTriggerCancel() {

//                    LogUtils.d(TAG, "onTriggerCancel");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);

                    handler.removeMessages(1);
                }
            });


            LayoutParams containerParmas = new LayoutParams((int) option.getWidth(), (int) option.getHeight());
            containerParmas.leftMargin = (int) option.getLeft();
            containerParmas.topMargin = (int) option.getTop();
            addView(optionView, containerParmas);
        }
    }


    public void setComponentOption(boolean result, VideoProtocolInfo.EventComponent eventComponent) {

        this.eventComponent = eventComponent;
        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options == null || options.isEmpty())
            return;


        for (int i = 0; i < options.size(); i++) {

            VideoProtocolInfo.EventOption option = options.get(i);

            if (option != null) {


                if (option.hide_option) {
                    continue;
                }

//                boolean align_screen = option.align_screen;
//
//                float width = 0;
//                float height = 0;
//
//                float left = 0;
//                float top = 0;
//
//                VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
//                if (optionStyle != null) {
//                    if (align_screen) {
//                        width = parentWidth * optionStyle.width / 100;
//                        height = parentHeight * optionStyle.height / 100;
//                        left = parentWidth * optionStyle.left / 100;
//                        top = parentHeight * optionStyle.top / 100;
//                    } else {
//                        width = videoWidth * optionStyle.width / 100;
//                        height = videoHeight * optionStyle.height / 100;
//                        left = videoWidth * optionStyle.left / 100 + ((parentWidth - videoWidth) / 2);
//                        top = videoHeight * optionStyle.top / 100 + ((parentHeight - videoHeight) / 2);
//                    }
//                }


                OptionView optionView = new OptionView(getContext());
                optionView.setTag(option.option_id);
//                optionView.initParmas(parentWidth, parentHeight, videoWidth, videoHeight, width, height);
                optionView.setTag(option.option_id);

                if (option.blink) {
                    AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.1f, 1.0f);
                    alphaAnimation1.setDuration(1000);
                    alphaAnimation1.setRepeatCount(Animation.INFINITE);
                    alphaAnimation1.setRepeatMode(Animation.RESTART);
                    optionView.setAnimation(alphaAnimation1);
                    alphaAnimation1.start();
                }


                int displayTime;


                if (result) {
                    optionView.setOption(OptionView.STATUS_CLICK_ENDED, option);

                    if (option.custom == null || option.custom.click_ended == null || NativeViewUtils.isNullOrEmptyString(option.custom.click_ended.image_url)) {
                        continue;
                    } else {
                        displayTime = option.custom.click_ended.display_time;
                    }

                } else {

                    optionView.setOption(OptionView.STATUS_CLICK_FAILED, option);

                    if (option.custom == null || option.custom.click_failed == null || NativeViewUtils.isNullOrEmptyString(option.custom.click_failed.image_url)) {
                        continue;
                    } else {
                        displayTime = option.custom.click_failed.display_time;
                    }

                }


                LayoutParams containerParmas = new LayoutParams((int) option.getWidth(), (int) option.getHeight());
                containerParmas.leftMargin = (int) option.getLeft();
                containerParmas.topMargin = (int) option.getTop();
                addView(optionView, containerParmas);


                if (handler != null) {
                    Message message = new Message();
                    message.obj = option.option_id;
                    handler.sendMessageDelayed(message, displayTime * 1000);
                }


            }

        }


    }

    private int optionIndex;

    Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {


                if (listener != null) {
                    listener.onOptionLongPress(optionIndex);
                }


                Log.d(TAG, "longpress=");

            } else {
                String optionId = (String) msg.obj;


                View view = findViewWithTag(optionId);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }


                List<VideoProtocolInfo.EventOption> options = eventComponent.options;
                if (options == null || options.isEmpty())
                    return;


                boolean allGone = true;

                for (VideoProtocolInfo.EventOption option : options) {
                    View optionView = findViewWithTag(option.option_id);
                    if (optionView != null && optionView.getVisibility() == View.VISIBLE) {
                        allGone = false;
                    }

                }

                if (allGone && showResultListener != null) {
                    showResultListener.onShowResultFinish(eventComponent.event_id);
                }
            }


        }
    };

}
