package com.ctrlvideo.nativeivview.component;


import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.widget.OptionView;

import java.util.List;


/**
 * 选择类事件组件
 */
public class SelectedComponent extends BaseComponent {

    private String TAG = "SelectedComponent";

    public SelectedComponent(Context context) {
        this(context, null);
    }

    public SelectedComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onOptionTriggerAfter(int optionIndex) {
        super.onOptionTriggerAfter(optionIndex);

        if (listener != null) {
            listener.onOptionSelected(optionIndex, eventComponent);
        }
    }

    public void setComponentOption(int optionIndex, VideoProtocolInfo.EventComponent eventComponent) {
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

                if (!option.hasResultView(optionIndex == i)) {
                    continue;
                }


                OptionView optionView = new OptionView(getContext());
                optionView.setTag(option.option_id);
//                optionView.initParmas(parentWidth, parentHeight, videoWidth, videoHeight, width, height);
//                optionView.setTag(option.option_id);


                int displayTime;

                if (i == optionIndex) {
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


}
