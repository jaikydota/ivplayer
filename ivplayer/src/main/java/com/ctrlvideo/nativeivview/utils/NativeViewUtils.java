package com.ctrlvideo.nativeivview.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

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

        return "#" + strColor;

    }

    /**
     * 获取本地资源文件路径
     *
     * @return
     */
    public static String getDowmloadFilePath(Context context) {

        String path=new File(context.getCacheDir(), "ivsdk").getAbsolutePath();
        Log.d("getDowmloadFilePath", "path=" + path);
        return path;

//        return new File(Environment.getExternalStorageDirectory(), "ivsdk").getAbsolutePath();
    }

    /**
     * 根据url 生成本地文件名字
     *
     * @param url
     * @return
     */
    public static String getFileName(String url) {
//
//        String url="https://res-1300249927.file.myqcloud.com/media/3/103/image/3103483810874634/source.png";

        String newUrl = url.substring(url.indexOf("//") + 2, url.lastIndexOf(".")).replace("/", "_").replace(".", "_").replace("-", "_");
        Log.d("getFileName", "newUrl=" + newUrl);

        String suffix = url.substring(url.lastIndexOf("."));
        Log.d("getFileName", "suffix=" + suffix);

//        String name = url.substring(url.indexOf("//") + 2).replace("/", "-");


//        Log.d("getFileName", "url=" + url + "----name=" + name);

        return newUrl + suffix;
    }


    public static boolean isNullOrEmptyString(String str) {

        return (str == null || TextUtils.isEmpty(str));

    }
}
