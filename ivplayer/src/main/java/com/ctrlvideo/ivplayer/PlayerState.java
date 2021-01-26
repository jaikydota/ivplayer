package com.ctrlvideo.ivplayer;

/**
 * Author by Jaiky, Date on 2020/4/8.
 */
public interface PlayerState {
    String STATE_READY = "ready";//视频初始化完成
    String STATE_LOADING = "loading";//视频正在缓冲中
    String STATE_ONPAUSE = "onpause";//视频暂停
    String STATE_ONPLAY = "onplay";//视频播放
    String STATE_END = "end";//视频播放结束
}
