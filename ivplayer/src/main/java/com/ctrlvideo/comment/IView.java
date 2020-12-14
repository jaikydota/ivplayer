package com.ctrlvideo.comment;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IView {

    /**
     * 初始化
     *
     * @param config_url     静态url
     * @param channel
     * @param ivViewListener
     * @param mContext
     */
    void initIVView(@Nullable String config_url, @Nullable String channel, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext);

    /**
     * @param pid            智令互动IVE编辑器项目 ID
     * @param channel
     * @param ivViewListener
     * @param mContext
     */
    void initIVViewPid(@Nullable String pid, @Nullable String channel, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext);


    /**
     * 本地播放器状态
     *
     * @param status
     */
    void onPlayerStateChanged(String status);

    /**
     * 设置纯净模式
     *
     * @param isOpen
     */
    void setPureMode(boolean isOpen);

}
