package com.ctrlvideo.comment.net;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoProtocolInfo {

    public Protocol protocol;


    public ReleaseInfo release_info;


    /**
     * 互动协议信息
     */
    public class Protocol {

        public boolean auto_indent;
        public List<EventRail> event_list;//事件轨
    }

    /**
     * 事件轨道
     */
    public class EventRail {
        public List<EventComenet> obj_list;
    }

    /**
     * 事件组件
     */
    public class EventComenet {

        //互动组件类型
        public String obj_type;
        //控件
        public List<EventOption> options;

        public String classify;

    }

    /**
     * 事件控件
     */
    public class EventOption {

        public String option_id;
        public EventOptionCustom custom;
        public EventOptionStyle layout_style;
    }

    /**
     * 控件功能
     */
    public class EventOptionCustom {
        public String status;
        @SerializedName("default")
        public EventOptionStatus click_default;
        public EventOptionStatus click_on;
        public EventOptionStatus click_ended;
        public EventOptionStatus click_failed;
    }

    /**
     * 控件状态
     */
    public class EventOptionStatus {

        public String image_objid;
        public int display_time;
        public String audio_objid;
        public int seqframe_rate;
        public String image_name;
        public String audio_name;
        public String audio_url;
        public String audio_mode;
        public int is_modify;
        public String image_url;
        public String image_type;


    }

    public class EventOptionStyle {

        public String base_color;
        public int rotate;
        public int font_size;
    }


    /**
     * 视频信息
     */
    public class ReleaseInfo {

        public String url;
    }

}
