package com.ctrlvideo.ivplayer;

/**
 * Author by Jaiky, Date on 2020/4/8.
 */
public interface IVPlayerListener {

    /**
     * 当IVPlayer状态改变时调用
     * @param state 状态，ViewState.STATE_READIED 初始化完成
     */
    void onStateChanged(String state);


    /**
     * 当IVPlayer点击时 [如点击IVPlayer中控件将阻止向上冒泡，不会调用此方法]
     * @param info 点击信息
     */
    void onViewClick(String info);


    /**
     * 当事件状态改变时
     * @param eType 事件类型
     * @param state 状态，"start" 事件开始，"end" 事件结束, "succeed" 触发成功跳帧
     * @param time long类型，毫秒
     */
    void onEventStateChanged(String eType, String state, long time);


    /**
     * 当IVPlayer发生错误时
     * @param errorType 错误信息
     */
    void onError(String errorType);

    /**
     * 当IVView收到自定义通知
     * @param msg 通知内容
     */
    void onCustomNotify(String msg);
}
