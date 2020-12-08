package com.ctrlvideo.comment.net;

import java.util.ArrayList;
import java.util.List;

public class EventIntractInfoCallback extends EventCallback {


    public EventIntractInfoCallback(VideoProtocolInfo videoProtocolInfo) {

        data = new IntractInfo();


        Base base = new Base();

        List<EventInfo> eventInfos = new ArrayList<>();

        VideoProtocolInfo.Protocol protocol = videoProtocolInfo.protocol;
        if (protocol != null) {
            base.project_id = protocol.project_id;
            List<VideoProtocolInfo.EventRail> eventRails = protocol.event_list;
            if (eventRails != null) {
                for (VideoProtocolInfo.EventRail eventRail : eventRails) {

                    List<VideoProtocolInfo.EventComponent> eventComponents = eventRail.obj_list;
                    if (eventComponents != null) {

                        for (VideoProtocolInfo.EventComponent eventComponent : eventComponents) {

                            EventInfo eventInfo = new EventInfo();

                            eventInfo.start_time = eventComponent.start_time;
                            eventInfo.end_time = eventComponent.end_time;
                            eventInfo.duration = eventComponent.duration;
                            eventInfo.event_id = eventComponent.event_id;
                            eventInfo.name = eventComponent.name;
                            eventInfo.type = eventComponent.type;
                            List<Option> optionList = new ArrayList<>();

                            List<VideoProtocolInfo.EventOption> eventOptions = eventComponent.options;
                            if (eventOptions != null) {

                                for (VideoProtocolInfo.EventOption eventOption : eventOptions) {

                                    Option option = new Option();

                                    option.option_id = eventOption.option_id;
                                    option.option_name = eventOption.option_name;
                                    VideoProtocolInfo.EventOptionStyle layoutStyle = eventOption.layout_style;
                                    if (layoutStyle != null) {
                                        option.text = layoutStyle.text;
                                    }
                                    optionList.add(option);
                                }
                            }
                            eventInfo.options = optionList;

                            eventInfos.add(eventInfo);


                        }

                    }


                }
            }
        }

        data.base = base;
        data.event_info = eventInfos;


    }




    @Override
    public String getStatus() {
        return INTERACT_INFO;
    }

    public IntractInfo data;


    public class IntractInfo {

        public Base base;
        public List<EventInfo> event_info;

    }

    public class Base {

        public String project_id;
        public String guest_id;
    }

    public class EventInfo {


//        start_time: 10,[开始时间]
//        end_time: 20,[结束时间]
//        duration: 10,[持续时间]
//        event_id: "854512",[事件唯一id]
//        name: "点击去看剧情一",[事件名称]
//        type: "click",[事件类型如：click、select]
//        options: [], [控件信息数组]

        public float start_time;
        public float end_time;
        public float duration;
        public String event_id;
        public String name;
        public String type;
        public List<Option> options;


    }

    public class Option {
        //        option_name: "按钮1",
//        option_id: "954651",[控件唯一id]
//        text: "前往剧情一",[控件显示文字（如果没有文字的控件返回空字符串）]
        public String option_name;
        public String option_id;
        public String text;


    }

//    public EventIntractInfoCallback transform(VideoProtocolInfo videoProtocolInfo) {
//
//        EventIntractInfoCallback eventIntractInfoCallback = new EventIntractInfoCallback();
//
//        IntractInfo intractInfo = new IntractInfo();
//
//
//        eventIntractInfoCallback.data = intractInfo;
//
//
//        return eventIntractInfoCallback;
//
//
//    }


}
