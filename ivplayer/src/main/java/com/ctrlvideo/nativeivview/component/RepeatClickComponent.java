package com.ctrlvideo.nativeivview.component;


import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.LogUtils;


/**
 * 重复点击事件组件
 */
public class RepeatClickComponent extends BaseComponent {

    private String TAG = "RepeatClickComponent";


    public RepeatClickComponent(Context context) {
        this(context, null);
    }

    public RepeatClickComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RepeatClickComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onOptionTriggerAfter(int optionIndex) {
        super.onOptionTriggerAfter(optionIndex);


        clickCount++;

//                    Log.d(TAG, "click_num=" + eventComponent.click_num);
        if (handler != null) {
            handler.removeMessages(MSG_REPEAT_CLICK);
        }

        if (clickCount >= eventComponent.click_num) {
            clickCount = eventComponent.click_num;
            if (listener != null) {
                listener.onOptionRepeatClick(eventComponent);
            }
        } else {
            if (handler != null) {
                handler.sendEmptyMessageDelayed(MSG_REPEAT_CLICK, intervalClickTime);
            }
        }

        LogUtils.d(TAG, "clickCount=" + clickCount);


    }

    private int clickCount = 0;
    private long intervalClickTime = 800;


    public void setComponentOption(boolean result, VideoProtocolInfo.EventComponent eventComponent) {

        setComponentOptionResult(result , eventComponent);

    }

    @Override
    protected void handleMsg(Message msg) {
        if (msg.what == MSG_REPEAT_CLICK) {

            clickCount--;
            if (clickCount > 0) {
                handler.sendEmptyMessageDelayed(MSG_REPEAT_CLICK, intervalClickTime);
            }


            LogUtils.d(TAG, "clickCount=" + clickCount);

        } else {
            super.handleMsg(msg);
        }
    }

}
