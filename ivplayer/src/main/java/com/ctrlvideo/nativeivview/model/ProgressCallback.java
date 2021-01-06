package com.ctrlvideo.nativeivview.model;

import java.util.List;

public class ProgressCallback {

    public static final String TYPE_EVENT = "event";
    public static final String TYPE_NODE = "node";

    public String type;
    public List<VideoNodeInterval> list;
}
