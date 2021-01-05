package com.ctrlvideo.nativeivview.component;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import com.ctrlvideo.nativeivview.audioplayer.SoundManager;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;
import com.ctrlvideo.nativeivview.widget.OptionView;

import java.io.File;
import java.util.List;

public class BaseComponent extends FrameLayout {

    protected VideoProtocolInfo.EventComponent eventComponent;

    private boolean loadFinish;

    private float videoWidthPixel;
    private float videoHeightPixel;

    protected float parentWidth;
    protected float parentHeight;

    protected float videoWidth;
    protected float videoHeight;


    public BaseComponent(Context context) {
        this(context, null);
    }

    public BaseComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initParmas(float parentWidth, float parentHeight, float videoWidthPixel, float videoHeightPixel) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.videoWidthPixel = videoWidthPixel;
        this.videoHeightPixel = videoHeightPixel;

        calculateVideoSize();
    }

    protected void calculateVideoSize() {

        float ratio = videoWidthPixel / videoWidthPixel;
        if (ratio >= (parentWidth / parentHeight)) {
            this.videoWidth = parentWidth;
            this.videoHeight = parentWidth / videoWidthPixel * videoHeightPixel;
        } else {
            this.videoWidth = parentHeight / videoHeightPixel * videoWidthPixel;
            this.videoHeight = parentHeight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);

//        LogUtils.d("onMeasure", "width=" + width + "----height=" + height + "----count=" + count);


        if (parentWidth != width && parentHeight != height) {
            LogUtils.d("BaseComponent", "重新调整子控件位置");
            parentWidth = width;
            parentHeight = height;
            calculateVideoSize();
            resetOptionView();
        }

    }

    private void resetOptionView() {

        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            OptionView optionView = (OptionView) getChildAt(index);

            String tagId = (String) optionView.getTag();
            VideoProtocolInfo.EventOption option = getOption(tagId);


            if (option != null) {

                option.containerWidth = parentWidth;
                option.containerHeight = parentHeight;
                option.videoWidth = videoWidth;
                option.videoHeight = videoHeight;


//                float width = 0;
//                float height = 0;
//                float left = 0;
//                float top = 0;
//
//                VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
//                if (optionStyle != null) {
//                    boolean align_screen = option.align_screen;
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

                LayoutParams layoutParams = (LayoutParams) optionView.getLayoutParams();

                layoutParams.width = (int) option.getWidth();
                layoutParams.height = (int) option.getHeight();
                layoutParams.leftMargin = (int) option.getLeft();
                layoutParams.topMargin = (int) option.getTop();

                optionView.setLayoutParams(layoutParams);
            }


        }

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


    public VideoProtocolInfo.EventOption getOption(String option_id) {

        if (eventComponent != null) {
            List<VideoProtocolInfo.EventOption> options = eventComponent.options;
            for (VideoProtocolInfo.EventOption option : options) {
                if (option_id.equals(option.option_id)) {
                    return option;
                }
            }
        }
        return null;
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

                    LogUtils.d("SelectedComponent", "onTrigger");

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

                    onOptionTrigger(finalI);
                }

                @Override
                public void onTriggerAfter() {
                    LogUtils.d("SelectedComponent", "onTriggerAfter");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);

                    onOptionTriggerAfter(finalI);


                }

                @Override
                public void onTriggerCancel() {

                    LogUtils.d("SelectedComponent", "onTriggerCancel");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);

                    onOptionTriggerCancel(finalI);
                }
            });


            LayoutParams containerParmas = new LayoutParams((int) option.getWidth(), (int) option.getHeight());
            containerParmas.leftMargin = (int) option.getLeft();
            containerParmas.topMargin = (int) option.getTop();
            addView(optionView, containerParmas);
        }


    }


    protected void setComponentOptionResult(boolean result, VideoProtocolInfo.EventComponent eventComponent) {


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

                if (!option.hasResultView(result)) {
                    continue;
                }


                OptionView optionView = new OptionView(getContext());
                optionView.setTag(option.option_id);
//                optionView.initParmas(parentWidth, parentHeight, videoWidth, videoHeight, width, height);
//                optionView.setTag(option.option_id);


                int displayTime;

                if (result) {
                    optionView.setOption(OptionView.STATUS_CLICK_ENDED, option);

//                    if (option.custom == null || option.custom.click_ended == null || NativeViewUtils.isNullOrEmptyString(option.custom.click_ended.image_url)) {
//                        continue;
//                    } else {
                    displayTime = option.custom.click_ended.display_time;
//                    }


                } else {
                    optionView.setOption(OptionView.STATUS_CLICK_FAILED, option);

//                    if (option.custom == null || option.custom.click_failed == null || NativeViewUtils.isNullOrEmptyString(option.custom.click_failed.image_url)) {
//                        continue;
//                    } else {
                    displayTime = option.custom.click_failed.display_time;
//                    }
                }


                LayoutParams containerParmas = new LayoutParams((int) option.getWidth(), (int) option.getHeight());
                containerParmas.leftMargin = (int) option.getLeft();
                containerParmas.topMargin = (int) option.getTop();
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
            handleMsg(msg);
        }
    };

    protected void handleMsg(Message msg){
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



    protected void onOptionTrigger(int optionIndex) {

    }

    protected void onOptionTriggerAfter(int optionIndex) {

    }

    protected void onOptionTriggerCancel(int optionIndex) {

    }

    protected OnComponentOptionListener listener;
    protected OnComponentResultListener showResultListener;

    public void setOnComponentOptionListener(OnComponentOptionListener listener) {
        this.listener = listener;
    }

    public void setOnShowResultListener(OnComponentResultListener listener) {

        this.showResultListener = listener;
    }

    public interface OnComponentOptionListener {
        void onOptionSelected(int optionIndex, VideoProtocolInfo.EventComponent eventComponent);

        void onOptionClick(VideoProtocolInfo.EventComponent eventComponent);

        void onOptionRepeatClick(VideoProtocolInfo.EventComponent eventComponent);

        void onOptionLongPress(VideoProtocolInfo.EventComponent eventComponent);
    }
}
