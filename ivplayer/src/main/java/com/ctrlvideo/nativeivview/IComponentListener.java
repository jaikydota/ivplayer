package com.ctrlvideo.nativeivview;

public interface IComponentListener {
    void onComponentSeek(long position);

    void onComponentEnd(String eventComponentId);
}
