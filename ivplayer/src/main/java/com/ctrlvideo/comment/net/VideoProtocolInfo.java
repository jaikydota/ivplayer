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
        // 组件列表
        public List<EventComponent> obj_list;
        // 隐藏轨道
        public boolean hide_track;
    }

    /**
     * 事件组件
     */
    public class EventComponent {

        //互动组件类型
        public String obj_type;
        public String type;
        //控件
        public List<EventOption> options;


        public String classify;
        public float start_time;
        public float end_time;
        public float duration;
        public String event_id;
        public boolean time_limit;


    }

    /**
     * 事件控件
     */
    public class EventOption {

        public String option_id;
        public EventOptionCustom custom;
        public EventOptionStyle layout_style;
        public boolean align_screen;
        public boolean hide_option;
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
        public float rotate;
        public float font_size;
        public String text;//文字
        public String color;//字体颜色
        public String writing_mode;//方向
        public EventOptionFilter filter;//混合选项


        public float top;
        public float left;
        public float width;
        public float height;


    }

    public class EventOptionFilter {

        public float opacity;
    }


    /**
     * 视频信息
     */
    public class ReleaseInfo {

        public String url;

        public VideoParams v_params;
    }

    public class VideoParams {

        public float duration;
        public int width;
        public int height;
        public int fps;
        public String v_bitRate;
    }

}
