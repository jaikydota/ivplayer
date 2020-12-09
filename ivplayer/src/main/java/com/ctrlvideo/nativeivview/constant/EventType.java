package com.ctrlvideo.nativeivview.constant;

public enum EventType {

    SELECT("select"),
    CLICK("click"),
    PASSIVITY("passivity");

    private String type;

    EventType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
