package com.ctrlvideo.nativeivview.utils;

import android.util.Log;

public class LogUtils {


    private static boolean open = true;

    public static void v(String tag, String msg) {
        if (open) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (open) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (open) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (open) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (open) {
            Log.e(tag, msg);
        }
    }


}
