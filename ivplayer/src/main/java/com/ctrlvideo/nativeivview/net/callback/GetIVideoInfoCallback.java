package com.ctrlvideo.nativeivview.net.callback;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.google.gson.Gson;

public abstract class GetIVideoInfoCallback extends CallbackImpl {


    @Override
    protected void converData(String data) {

        VideoProtocolInfo info = new Gson().fromJson(data, VideoProtocolInfo.class);

        onResponse(info);
    }

    protected abstract void onResponse(VideoProtocolInfo videoProtocolInfo);

}
