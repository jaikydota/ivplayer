package com.ctrlvideo.nativeivview.component;


import android.content.Context;
import android.util.AttributeSet;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;


/**
 * 单击类事件组件
 */
public class ClickComponent extends BaseComponent {

    private String TAG = "ClickComponent";

    public ClickComponent(Context context) {
        this(context, null);
    }

    public ClickComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClickComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onOptionTriggerAfter(int optionIndex) {
        super.onOptionTriggerAfter(optionIndex);
        if (listener != null) {
            listener.onOptionClick(eventComponent);
        }
    }

    public void setComponentOption(boolean result, VideoProtocolInfo.EventComponent eventComponent) {
        setComponentOptionResult(result , eventComponent);
    }

}
