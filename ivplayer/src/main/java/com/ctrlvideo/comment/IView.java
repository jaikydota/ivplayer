package com.ctrlvideo.comment;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface IView {

    void initIVView(@Nullable String pid, @Nullable String config_url, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext);

    void initIVView(@Nullable String pid, @Nullable String config_url, @NonNull IVViewListener ivViewListener, @NonNull Activity mContext, boolean openTestEnv);

    void onPlayerStateChanged(String status);
}
