package com.ctrlvideo.ivview;

import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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


    public static boolean checkAvailable(Context context) {
        boolean enableFeature = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

        try {
            WebView webView = new WebView(context);
            String userAgentString = webView.getSettings().getUserAgentString();
            String[] ugArr = userAgentString.split(" ");
            for (String info: ugArr){
                if (info.startsWith("Chrome")) {
                    String version = info.split("/")[1];
                    int versionInt = Integer.parseInt(version.split("\\.")[0]);
                    //小于41的版本不开启
                    if (versionInt < 41) {
                        enableFeature = false;
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            enableFeature = false;
        }

        if (isBBKStudentPhone())
            enableFeature = false;

        return enableFeature;
    }

    public static boolean isBBKStudentPhone() {
        boolean s2 = TextUtils.equals(Build.MODEL, "S2")
                && TextUtils.equals(Build.BRAND, "EEBBK");

        boolean s3 = TextUtils.equals(Build.MODEL, "S3")
                && TextUtils.equals(Build.BRAND, "EEBBK");

        return s2 || s3;
    }

    public static Map<String, Object> getMap(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            Iterator<String> keyIter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyIter.hasNext())
            {
                key = keyIter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

}
