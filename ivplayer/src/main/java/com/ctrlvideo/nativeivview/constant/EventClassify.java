package com.ctrlvideo.nativeivview.constant;

public enum EventClassify {

    TE("TE");

    private  String classify;

    EventClassify(String classify) {
        this.classify = classify;
    }

    public String getClassify() {
        return classify;
    }
}
