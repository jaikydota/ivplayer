package com.ctrlvideo.nativeivview;

import android.util.Log;

public class NativeViewUtils {


    /**
     * 将rgba 转换为 argb 十六进制
     *
     * @param rgba
     * @return
     */
    public static String transformColor(String rgba) {


        String strColor = "";

        String[] colors = rgba.substring(rgba.indexOf("(") + 1, rgba.indexOf(")")).split(",");

        for (int i = 0; i < colors.length; i++) {
            String sds = Integer.toHexString((int) Float.parseFloat(colors[i].trim()));

            if (i == 3) {
                int alpha = (int) Math.round(Float.parseFloat(colors[i].trim()) * 255);
                sds = Integer.toHexString(alpha);
            }
            if (sds.length() < 2) {
                sds = "0" + sds;
            }
            if (i == 3) {
                strColor = sds.toUpperCase() + strColor;
            } else {
                strColor = strColor + sds.toUpperCase();
            }
        }

        Log.d("transformColor", "rgba=" + rgba + "----strColor=" + strColor);
        return "#" + strColor;

    }

}
