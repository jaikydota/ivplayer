package com.ctrlvideo.nativeivview;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.ctrlvideo.comment.net.VideoProtocolInfo;

import java.io.File;
import java.util.List;


/**
 * 单击类事件组件
 */
public class ClickComponent extends RelativeLayout {

    private String TAG = "ClickComponent";


    private VideoProtocolInfo.EventComponent eventComponent;

    public ClickComponent(Context context) {
        this(context, null);
    }

    public ClickComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.listener = listener;
    }

    public void setOnShowResultListener(OnComponentResultListener listener) {

        this.showResultListener = listener;
    }

    private OnOptionClickListener listener;
    private OnComponentResultListener showResultListener;

    public interface OnOptionClickListener {
        void onOptionClick(int option);
    }


    public void initComponent(int status, VideoProtocolInfo.EventComponent eventComponent, float parentWidth, float parentHeight, float videoWidth, float videoHeight) {

        this.eventComponent = eventComponent;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options == null || options.isEmpty())
            return;


        for (int i = 0; i < options.size(); i++) {

            VideoProtocolInfo.EventOption option = options.get(i);

            if (option.hide_option) {
                continue;
            }

            OptionView optionView = new OptionView(getContext());
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

                    Log.d(TAG, "onTrigger");

                    optionView.setOption(OptionView.STATUS_CLICK_ON, option);


                    if (option.custom != null && option.custom.click_on != null) {

                        String audioUrl = option.custom.click_on.audio_url;
                        if (!NativeViewUtils.isNullOrEmptyString(audioUrl)) {

                            File localFile = new File(NativeViewUtils.getDowmloadFilePath(), NativeViewUtils.getFileName(audioUrl));
                            if (localFile.exists()) {
                                SoundManager.getInstance().play(localFile.getAbsolutePath());
                            }
                        }
                    }
                }

                @Override
                public void onTriggerAfter() {
                    Log.d(TAG, "onTriggerAfter");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);

                    if (listener != null) {
                        listener.onOptionClick(finalI);
                    }


                }

                @Override
                public void onTriggerCancel() {

                    Log.d(TAG, "onTriggerCancel");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);
                }
            });


            boolean align_screen = option.align_screen;


            float width = 0;
            float height = 0;

            float left = 0;
            float top = 0;

            VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
            if (optionStyle != null) {
                if (align_screen) {
                    width = parentWidth * optionStyle.width / 100;
                    height = parentHeight * optionStyle.height / 100;
                    left = parentWidth * optionStyle.left / 100;
                    top = parentHeight * optionStyle.top / 100;
                } else {
                    width = videoWidth * optionStyle.width / 100;
                    height = videoHeight * optionStyle.height / 100;
                    left = videoWidth * optionStyle.left / 100 + ((parentWidth - videoWidth) / 2);
                    top = videoHeight * optionStyle.top / 100 + ((parentHeight - videoHeight) / 2);
                }
            }

            LayoutParams containerParmas = new LayoutParams((int) width, (int) height);
            containerParmas.leftMargin = (int) left;
            containerParmas.topMargin = (int) top;
            addView(optionView, containerParmas);
        }
    }


    public void setComponentOption(boolean result, VideoProtocolInfo.EventComponent eventComponent, float parentWidth, float parentHeight, float videoWidth, float videoHeight) {

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

                OptionView optionView = new OptionView(getContext());
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

                boolean align_screen = option.align_screen;

                float width = 0;
                float height = 0;

                float left = 0;
                float top = 0;

                VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
                if (optionStyle != null) {
                    if (align_screen) {
                        width = parentWidth * optionStyle.width / 100;
                        height = parentHeight * optionStyle.height / 100;
                        left = parentWidth * optionStyle.left / 100;
                        top = parentHeight * optionStyle.top / 100;
                    } else {
                        width = videoWidth * optionStyle.width / 100;
                        height = videoHeight * optionStyle.height / 100;
                        left = videoWidth * optionStyle.left / 100 + ((parentWidth - videoWidth) / 2);
                        top = videoHeight * optionStyle.top / 100 + ((parentHeight - videoHeight) / 2);
                    }
                }

                LayoutParams containerParmas = new LayoutParams((int) width, (int) height);
                containerParmas.leftMargin = (int) left;
                containerParmas.topMargin = (int) top;
                addView(optionView, containerParmas);


                Message message = new Message();
                message.obj = option.option_id;
                handler.sendMessageDelayed(message, displayTime * 1000);

            }

        }


    }

    Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

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
    };

}
