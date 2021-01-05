package com.ctrlvideo.nativeivview.component;


import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;


/**
 * 重复点击事件组件
 */
public class LongPressComponent extends BaseComponent {

    private String TAG = "LongPressComponent";


    public LongPressComponent(Context context) {
        this(context, null);
    }

    public LongPressComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LongPressComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onOptionTrigger(int optionIndex) {
        super.onOptionTrigger(optionIndex);

//        this.optionIndex = optionIndex;
        if (handler != null) {
            handler.sendEmptyMessageDelayed(1, eventComponent.longpress_time * 1000);
        }
    }

    @Override
    protected void onOptionTriggerAfter(int optionIndex) {
        super.onOptionTriggerAfter(optionIndex);
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    @Override
    protected void onOptionTriggerCancel(int optionIndex) {
        super.onOptionTriggerCancel(optionIndex);
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    public void setComponentOption(boolean result, VideoProtocolInfo.EventComponent eventComponent) {

        setComponentOptionResult(result, eventComponent);
    }

    @Override
    protected void handleMsg(Message msg) {
        if (msg.what == 1) {


            if (listener != null) {
                listener.onOptionLongPress(eventComponent);
            }
//            LogUtils.d(TAG, "longpress=");

        } else {
            super.handleMsg(msg);
        }
    }

}
