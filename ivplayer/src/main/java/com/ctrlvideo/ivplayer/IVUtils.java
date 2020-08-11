package com.ctrlvideo.ivplayer;

import android.os.Looper;

/**
 * Author by Jaiky, Date on 2020/6/1.
 */
public class IVUtils {

    public static long getMMTime(String time) {
        float setTime = Float.parseFloat(time);
        setTime = setTime * 1000;
        return (long) setTime;
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
