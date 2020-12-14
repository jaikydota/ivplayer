package com.ctrlvideo.ivplayer;

/**
 * Author by Jaiky, Date on 2020/4/8.
 */
public interface IVPlayerListener {

    /**
     * 当IVPlayer状态改变时调用
     *
     * @param state 状态，ViewState.STATE_READIED 初始化完成
     */
    void onStateChanged(String state);


    /**
     * 当IVPlayer点击时 [如点击IVPlayer中控件将阻止向上冒泡，不会调用此方法]
     *
     * @param info 点击信息
     */
    void onViewClick(String info);


    void onEventCallback(String result);


    /**
     * 当IVPlayer发生错误时
     *
     * @param errorType 错误信息
     */
    void onError(String errorType);

    /**
     * 当IVView收到自定义通知
     *
     * @param msg 通知内容
     */
    void onCustomNotify(String msg);

    /**
     * 当IVView收到跳转链接通知
     *
     * @param url
     */
    void onHrefUrl(String url);

    /**
     * 当IVView收到打电话通知
     *
     * @param phone
     */
    void onCallPhone(String phone);

}
