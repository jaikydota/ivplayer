package com.ctrlvideo.nativeivview.model;

import android.content.Context;

import com.ctrlvideo.nativeivview.utils.NativeViewUtils;
import com.ctrlvideo.nativeivview.widget.OptionView;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.math.BigDecimal;
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
        public List<VideoRail> video_list;//视频轨
        public String project_id;//项目ID
    }

    /**
     * 事件轨道
     */
    public class VideoRail {
        public float duration;
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
        public String name;


        public String classify;
        public float start_time;
        public float end_time;
        public float duration;
        public String event_id;
        public boolean time_limit;
        //默认选中
        public int default_skip_option;
        public EventFeature feature;

        public float active_skip_time;//点击跳转
        public float ended_skip_time;//未点击跳转
        public int click_num;
        public int longpress_time;


        //事件开始
        public boolean startIsActive;
        //事件结束点
        public boolean endIsActive;
        //事件范围内
        public boolean eventIsActive;

        public boolean isMediaAssetsDownload(Context context) {

            boolean downloadFinish = true;

            if (options != null && !options.isEmpty()) {
                for (EventOption eventOption : options) {
                    EventOptionCustom eventOptionCustom = eventOption.custom;
                    if (eventOptionCustom != null) {
                        EventOptionStatus optionStatus = eventOptionCustom.click_default;
                        if (optionStatus != null) {

                            String url = optionStatus.image_url;
                            if (!NativeViewUtils.isNullOrEmptyString(url) && !new File(NativeViewUtils.getDowmloadFilePath(context), NativeViewUtils.getFileName(url)).exists()) {
                                downloadFinish = false;
                                break;
                            }
                        }
                    }
                }
            }
            return downloadFinish;
        }
    }

    /**
     * 事件功能
     */
    public class EventFeature {

        public String call_phone;
        public String href_url;
        public String choice;
        public String miniprogram;
        public String miniprogram_path;


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
        public float skip_start_time;
        public boolean blink;//闪烁;
        public String option_name;
        public String type;
        public float containerWidth;
        public float containerHeight;
        public float videoWidth;
        public float videoHeight;


        public float getWidth() {
            float width = 0;
            if (layout_style != null) {
                if (align_screen) {
                    width = containerWidth * layout_style.width / 100;
                } else {
                    width = videoWidth * layout_style.width / 100;
                }
            }
            return width;
        }

        public float getHeight() {
            float height = 0;
            if (layout_style != null) {
                if (align_screen) {
                    height = containerHeight * layout_style.height / 100;
                } else {
                    height = videoHeight * layout_style.height / 100;
                }
            }
            return height;
        }

        public float getLeft() {
            float left = 0;
            if (layout_style != null) {
                if (align_screen) {
                    left = containerWidth * layout_style.left / 100;
                } else {
                    left = videoWidth * layout_style.left / 100 + ((containerWidth - videoWidth) / 2);
                }
            }
            return left;
        }

        public float getTop() {
            float top = 0;
            if (layout_style != null) {
                if (align_screen) {
                    top = containerHeight * layout_style.top / 100;
                } else {
                    top = videoHeight * layout_style.top / 100 + ((containerHeight - videoHeight) / 2);
                }
            }
            return top;
        }


        public float getTextSize() {

            float textSize = 0;
            if (layout_style != null) {

                float font_size = layout_style.font_size;

                float baseSize;
                if (align_screen) {
                    baseSize = getTextBaseSize(containerWidth, containerHeight);
                } else {
                    baseSize = getTextBaseSize(videoWidth, videoHeight);
                }


                textSize = font_size * baseSize;

            }

            return textSize;
        }


        private float getTextBaseSize(float width, float height) {

            BigDecimal bg = new BigDecimal((width + height) / 100);
            return bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        }

        /**
         * 是否有触发成功失败显示的样式
         *
         * @param result
         * @return
         */
        public boolean hasResultView(boolean result) {

            if (custom != null) {
                EventOptionStatus optionStatus;
                if (result) {
                    optionStatus = custom.click_ended;
                } else {
                    optionStatus = custom.click_failed;
                }
                if (optionStatus != null) {
                    return !NativeViewUtils.isNullOrEmptyString(optionStatus.image_url);
                }
            }
            return false;
        }

        public EventOptionStatus getOptionStatus(int status) {

            if (custom != null) {

                EventOptionStatus optionStatus = custom.click_default;
                if (status == OptionView.STATUS_CLICK_ON) {
                    optionStatus = custom.click_on;
                } else if (status == OptionView.STATUS_CLICK_ENDED) {
                    optionStatus = custom.click_ended;
                } else if (status == OptionView.STATUS_CLICK_FAILED) {
                    optionStatus = custom.click_failed;
                }
                return optionStatus;
            }
            return null;
        }
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
        //饱和度
        public String saturate;
        //对比度
        public String contrast;
        //亮度
        public String brightness;
        //模糊度
        public float blur;
    }


    /**
     * 视频信息
     */
    public class ReleaseInfo {

        public String url;

        public VideoParams v_params;
        public String player_controller;

        public PlayerController getPlayerController() {
            if (player_controller != null) {
                return new Gson().fromJson(player_controller, PlayerController.class);
            }
            return new PlayerController();
        }
    }

    public class PlayerController {

        public boolean show_start_btn;
        public boolean show_playPause_btn;

        public boolean isShowContrller() {
            return show_start_btn || show_playPause_btn;
        }
    }

    public class VideoParams {

        public float duration;
        public int width;
        public int height;
        public int fps;
        public String v_bitRate;
    }

}
