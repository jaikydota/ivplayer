package com.ctrlvideo.nativeivview.component;

public interface IComponentListener {

    void onEventCallback(String action);

    void onComponentSeek(long position);

//    boolean isVideoPlaying();

    void ctrlPlayer(boolean play);

    void hrefUrl(String href_url);

    void callPhone(String call_phone);

}
