package com.ctrlvideo.nativeivview.model;

import com.google.gson.Gson;

/**
 * 互动事件回调
 */
public abstract class EventCallback {


//    interact_info：互动点信息，当初始化完成后回调，data将返回精简的互动事件信息数组。
//    present：事件曝光，当互动事件出现时回调，data为具体出现的事件数据。
//    action：互动行为，当用户操作互动事件时回调，data为操作信息。

    public static final String INTERACT_INFO = "interact_info";
    public static final String PRESENT = "present";
    public static final String ACTION = "action";
    public String status = getStatus();

    public abstract String getStatus();

    public String toJson() {
        return new Gson().toJson(this);
    }

}
