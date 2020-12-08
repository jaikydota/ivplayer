package com.ctrlvideo.comment.net;

import java.util.List;

public class EventActionInfoCallback extends EventCallback {

    public EventActionInfoCallback(VideoProtocolInfo.EventComponent eventComponent, int optionIndex) {

        ActionInfo actionInfo = new ActionInfo();
        actionInfo.event_id = eventComponent.event_id;
        actionInfo.name = eventComponent.name;
        actionInfo.type = eventComponent.type;
        actionInfo.branch = -1;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null && options.size() > optionIndex) {
            VideoProtocolInfo.EventOption option = options.get(optionIndex);
            actionInfo.option_id = option.option_id;
            actionInfo.option_name = option.option_name;
        }
        data = actionInfo;
    }

    public EventActionInfoCallback(VideoProtocolInfo.EventComponent eventComponent, boolean result) {

        ActionInfo actionInfo = new ActionInfo();
        actionInfo.event_id = eventComponent.event_id;
        actionInfo.name = eventComponent.name;
        actionInfo.type = eventComponent.type;
        actionInfo.branch = result ? 1 : 0;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null && !options.isEmpty()) {
            VideoProtocolInfo.EventOption option = options.get(0);
            actionInfo.option_id = option.option_id;
            actionInfo.option_name = option.option_name;
        }
        data = actionInfo;
    }

    @Override
    public String getStatus() {
        return ACTION;
    }

    public ActionInfo data;

    public class ActionInfo {

        public String event_id;// 事件唯一id
        public String name;//事件名称，在编辑器里可定义此名称
        public String type;//事件类型，可查看“事件类型表”获取类型
        public int branch;//select选择事件值统一为 -1，其他成功失败类事件，如果操作成功为 1，未操作为 0
        public String option_id;//控件唯一id（仅单击 click、选择 select会有控件id返回，其他事件返回空字符串）
        public String option_name;//控件自定义名称（仅单击 click、选择 select会有返回，其他事件返回空字符串）

    }
}
