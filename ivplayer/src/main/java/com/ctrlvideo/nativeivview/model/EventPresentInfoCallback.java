package com.ctrlvideo.nativeivview.model;

public class EventPresentInfoCallback extends EventCallback {

    public EventPresentInfoCallback(VideoProtocolInfo.EventComponent eventComponent) {

        PresentInfo presentInfo = new PresentInfo();
        presentInfo.event_id = eventComponent.event_id;
        presentInfo.name = eventComponent.name;
        presentInfo.type = eventComponent.type;

        data = presentInfo;
    }

    @Override
    public String getStatus() {
        return PRESENT;
    }

    public PresentInfo data;

    public class PresentInfo {

        public String event_id;// 事件唯一id
        public String name;//事件名称，在编辑器里可定义此名称
        public String type;//事件类型，可查看“事件类型表”获取类型

    }
}
