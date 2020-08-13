package com.ctrlvideo.ivview;

import android.os.Looper;

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
