package com.ctrlvideo.nativeivview.component;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.ctrlvideo.nativeivview.audioplayer.SoundManager;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;
import com.ctrlvideo.nativeivview.widget.OptionView;

import java.io.File;
import java.util.List;


/**
 * 选择类事件组件
 */
public class SelectedComponent extends RelativeLayout {

    private String TAG = "SelectedComponent";


    private VideoProtocolInfo.EventComponent eventComponent;

    public SelectedComponent(Context context) {
        this(context, null);
    }

    public SelectedComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnSelectedListener(OnSelectedListener listener) {

        this.listener = listener;
    }

    public void setOnShowResultListener(OnComponentResultListener listener) {

        this.showResultListener = listener;
    }

    private OnSelectedListener listener;
    private OnComponentResultListener showResultListener;
    private boolean loadFinish;


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

    public interface OnSelectedListener {
        void onOptionSelected(int option);
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
                }

                @Override
                public void onTriggerAfter() {
                    LogUtils.d("SelectedComponent", "onTriggerAfter");

                    optionView.setOption(OptionView.STATUS_DEFAULT, option);

                    if (listener != null) {
                        listener.onOptionSelected(finalI);
                    }


                }

                @Override
                public void onTriggerCancel() {

                    LogUtils.d("SelectedComponent", "onTriggerCancel");

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


    public void setComponentOption(int optionIndex, VideoProtocolInfo.EventComponent eventComponent, float parentWidth, float parentHeight, float videoWidth, float videoHeight) {

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


                int displayTime;

                if (i == optionIndex) {
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
