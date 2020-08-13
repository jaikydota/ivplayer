package com.ctrlvideo.ivview;

/**
 * Author by Jaiky, Date on 2020/5/28.
 */
public class IVEvent {

    /**
     * 事件，语音识别
     */
    public static final String EVENT_SPEECHRECOGN = "speechrecogn";

    /**
     * 事件，手势识别
     */
    public static final String EVENT_GESTURE = "gesture";



    //调用EventAction
    public interface EventAction {
        String INTERRUPT_EVENT = "interrupt_event";
        String SKIP_PREPARE_TIME = "skip_prepare_time";
    }

}
