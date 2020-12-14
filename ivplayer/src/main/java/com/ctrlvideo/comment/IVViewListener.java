package com.ctrlvideo.comment;

/**
 * Author by Jaiky, Date on 2020/4/8.
 */
public interface IVViewListener {


    /**
     * 当IVView状态改变时调用
     *
     * @param state    状态，ViewState.STATE_READIED 初始化完成
     * @param videoUrl 视频播放地址
     */
    void onIVViewStateChanged(String state, String videoUrl);

    /**
     * 获取当前播放时间
     *
     * @return long类型，毫秒
     */
    long getPlayerCurrentTime();

    /**
     * seek到播放器某个时间
     *
     * @param time long类型，毫秒
     */
    void seekToTime(long time);

    /**
     * 控制播放器，如使播放器 “播放”或“暂停”
     *
     * @param state "play" 播放视频，"pause" 粘贴视频
     */
    void ctrlPlayer(String state);

    /**
     * 当IVView点击时 [如点击IvView中控件将阻止向上冒泡，不会调用此方法]
     *
     * @param info 点击信息
     */
    void onIVViewClick(String info);


//    /**
//     * 当事件状态改变时
//     *
//     * @param eType 事件类型，ViewState.EVENT_SPEECHRECOGN 语音识别事件，ViewState.EVENT_GESTURE 手势事件
//     * @param state 状态，"prepare" 事件即将开始，"start" 事件开始，"end" 事件结束, "succeed" 触发成功跳帧
//     * @param time  long类型，毫秒
//     */
//    void onEventStateChanged(String eType, String state, long time);


    /**
     * 互动事件信息状态回调（互动点信息、互动曝光信息、互动操作信息）
     *
     * @param result 互动信息json字符串数据
     */
    void onEventCallback(String result);


    /**
     * 当IVView发生错误时
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
     * 当IVView收到跳转网页链接回调
     *
     * @param url 链接
     * @return true：实现网页跳转    false：sdk内部消耗，内部实现简单网页跳转
     */
    boolean onHrefUrl(String url);


    /**
     * 当IVView收到跳转网页链接回调
     *
     * @param phone 电话号码
     * @return true：实现拨打电话    false：sdk内部消耗，如果APP 授权打电话权限，内部实现拨打电话
     */
    boolean onCallPhone(String phone);
}
