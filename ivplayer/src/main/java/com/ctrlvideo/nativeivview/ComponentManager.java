package com.ctrlvideo.nativeivview;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.ctrlvideo.comment.net.VideoProtocolInfo;

import java.io.File;
import java.util.List;

public class ComponentManager {

    private String TAG = "ComponentManager";

    private Context mContext;

    private float videoWidth;
    private float videoHeight;
    private float parentWidth;
    private float parentHeight;
    private IComponentListener iComponentListener;


    private RelativeLayout rootView;


    public void initParmas(RelativeLayout rootView, VideoProtocolInfo videoProtocolInfo, IComponentListener iComponentListener) {
        this.rootView = rootView;
        this.mContext = rootView.getContext();


        this.parentWidth = rootView.getMeasuredWidth();
        this.parentHeight = rootView.getMeasuredHeight();
        this.iComponentListener = iComponentListener;


        float videoWidth = 0;
        float videoHeight = 0;

        VideoProtocolInfo.ReleaseInfo releaseInfo = videoProtocolInfo.release_info;
        if (releaseInfo != null) {
            VideoProtocolInfo.VideoParams params = releaseInfo.v_params;
            if (params != null) {

                videoWidth = params.width;
                videoHeight = params.height;

                float ratio = videoWidth / videoHeight;
                if (ratio >= parentWidth / parentHeight) {
                    this.videoWidth = parentWidth;
                    this.videoHeight = parentHeight / videoWidth * videoHeight;
                } else {
                    this.videoWidth = parentHeight / videoHeight * videoWidth;
                    this.videoHeight = parentHeight;
                }
            }
        }


    }


    /**
     * 事件范围内 ui 渲染
     *
     * @param eventComponent
     */
    public void eventScopeIn(VideoProtocolInfo.EventComponent eventComponent) {


        String classify = eventComponent.classify;
        String type = eventComponent.type;
        if (EventClassify.TE.getClassify().equals(classify)) {
            if (EventType.SELECT.getType().equals(type)) {
                initSelectedComponent(eventComponent);
            } else if (EventType.CLICK.getType().equals(type)) {
                initClickComponent(eventComponent);
            }

        }
    }


    /**
     * 事件范围外 ui 渲染
     *
     * @param eventComponent
     */
    public void eventScopeOut(VideoProtocolInfo.EventComponent eventComponent) {
        View componentView = rootView.findViewWithTag(eventComponent.event_id);
        if (componentView != null) {
            rootView.removeView(componentView);
        }
    }

    /**
     * 事件结束点
     *
     * @param eventComponent
     */
    public void eventEnd(VideoProtocolInfo.EventComponent eventComponent) {


        String classify = eventComponent.classify;
        String type = eventComponent.type;
        if (EventClassify.TE.getClassify().equals(classify)) {
            if (EventType.SELECT.getType().equals(type) || EventType.CLICK.getType().equals(type)) {
                endSelectedComponent(eventComponent);
            }
        }
    }

    /**
     * 事件内
     *
     * @param eventComponent
     */
    public void eventIn(VideoProtocolInfo.EventComponent eventComponent) {


        String classify = eventComponent.classify;
        String type = eventComponent.type;
        if (EventClassify.TE.getClassify().equals(classify)) {
            if (EventType.PASSIVITY.getType().equals(type)) {//被动触发
                passiveTrigger(eventComponent);
            }
        }
    }

    /**
     * 被动触发
     *
     * @param eventComponent
     */
    private void passiveTrigger(VideoProtocolInfo.EventComponent eventComponent) {

        Log.d(TAG, "被动触发-------" + eventComponent.event_id);


        VideoProtocolInfo.EventFeature eventFeature = eventComponent.feature;
        if (eventFeature != null) {
            String choice = eventFeature.choice;
            if ("none".equals(choice)) {
                long skip_time = (long) (eventComponent.active_skip_time * 1000);
                if (skip_time >= 0 && iComponentListener != null) {
                    iComponentListener.onComponentSeek(skip_time);
                }
            } else if ("play".equals(choice)) {//播放
                if (iComponentListener != null) {
                    if (!iComponentListener.isVideoPlaying()) {
                        iComponentListener.ctrlPlayer(true);
                    }
                }
            } else if ("pause".equals(choice)) {//暂停
                if (iComponentListener != null) {
                    if (iComponentListener.isVideoPlaying()) {
                        iComponentListener.ctrlPlayer(false);
                    }
                }
            } else if ("href_url".equals(choice)) {//跳转链接
                if (iComponentListener != null) {
                    iComponentListener.hrefUrl(eventFeature.href_url);
                }
            }
        }

    }


    /**
     * 选择类组件
     *
     * @param eventComponent
     */
    private void initSelectedComponent(VideoProtocolInfo.EventComponent eventComponent) {


        SelectedComponent selectedComponent = rootView.findViewWithTag(eventComponent.event_id);


        if (selectedComponent == null) {


            selectedComponent = new SelectedComponent(mContext);
            selectedComponent.setTag(eventComponent.event_id);
            selectedComponent.initComponent(OptionView.STATUS_DEFAULT, eventComponent, parentWidth, parentHeight, videoWidth, videoHeight);
            selectedComponent.setOnSelectedListener(new SelectedComponent.OnSelectedListener() {
                @Override
                public void onOptionSelected(int option) {

                    Log.d("ComponentManger", "onOptionSelected===" + option);

                    setSelectedComponentResult(eventComponent, option);
                }
            });
            rootView.addView(selectedComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }

    /**
     * 单击类组件
     *
     * @param eventComponent
     */
    private void initClickComponent(VideoProtocolInfo.EventComponent eventComponent) {


        ClickComponent clickComponent = rootView.findViewWithTag(eventComponent.event_id);

        if (clickComponent == null) {

            clickComponent = new ClickComponent(mContext);
            clickComponent.setTag(eventComponent.event_id);
            clickComponent.initComponent(OptionView.STATUS_DEFAULT, eventComponent, parentWidth, parentHeight, videoWidth, videoHeight);
            clickComponent.setOnOptionClickListener(new ClickComponent.OnOptionClickListener() {
                @Override
                public void onOptionClick(int option) {
//                    Log.d(TAG, "option===" + option);

                    setClickComponentResult(eventComponent, true);
                }
            });
            rootView.addView(clickComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }


    /**
     * 事件结束点
     *
     * @param eventComponent
     */
    private void endSelectedComponent(VideoProtocolInfo.EventComponent eventComponent) {

        if (!eventComponent.time_limit) {

            if (iComponentListener != null) {
                iComponentListener.onComponentSeek((long) (eventComponent.start_time * 1000));

            }
        } else {
            if (EventType.SELECT.getType().equals(eventComponent.type)) {
                setSelectedComponentResult(eventComponent, eventComponent.default_skip_option);
            } else if (EventType.CLICK.getType().equals(eventComponent.type)) {
                setClickComponentResult(eventComponent, false);
            }
        }
    }

    /**
     * 判断是否有成功样式
     */
    private boolean hasTriggersucceedSuStyle(VideoProtocolInfo.EventComponent eventComponent) {

        boolean result = false;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {
            for (VideoProtocolInfo.EventOption option : options) {
                if (!option.hide_option) {
                    VideoProtocolInfo.EventOptionCustom optionCustom = option.custom;
                    if (optionCustom != null) {
                        VideoProtocolInfo.EventOptionStatus status = optionCustom.click_ended;
                        if (status != null) {
                            if (!NativeViewUtils.isNullOrEmptyString(status.image_url)) {
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断是否有失败样式
     */
    private boolean hasTriggerFailStyle(VideoProtocolInfo.EventComponent eventComponent) {
        boolean result = false;
        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {
            for (VideoProtocolInfo.EventOption option : options) {

                if (!option.hide_option) {
                    VideoProtocolInfo.EventOptionCustom optionCustom = option.custom;
                    if (optionCustom != null) {
                        VideoProtocolInfo.EventOptionStatus status = optionCustom.click_failed;
                        if (status != null) {
                            if (!NativeViewUtils.isNullOrEmptyString(status.image_url)) {
                                result = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * 单击组件
     *
     * @param eventComponent
     * @param result
     */
    private void setClickComponentResult(VideoProtocolInfo.EventComponent eventComponent, boolean result) {


        Log.d(TAG, "单击选择-----" + result);

        if ((result && hasTriggersucceedSuStyle(eventComponent)) || (!result && hasTriggerFailStyle(eventComponent))) {


            ClickComponent clickComponent = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (clickComponent == null) {

                clickComponent = new ClickComponent(mContext);
                clickComponent.setTag(createId(eventComponent.event_id));
                clickComponent.setComponentOption(result, eventComponent, parentWidth, parentHeight, videoWidth, videoHeight);
                clickComponent.setOnShowResultListener(new OnComponentResultListener() {
                    @Override
                    public void onShowResultFinish(String enent_id) {

                        ClickComponent view = rootView.findViewWithTag(createId(enent_id));
                        if (view != null) {
                            rootView.removeView(view);
                        }
                    }
                });
                rootView.addView(clickComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }


        if (result) {//触发功能

            VideoProtocolInfo.EventFeature eventFeature = eventComponent.feature;
            if (eventFeature != null) {

                String choice = eventFeature.choice;
                if ("none".equals(choice)) {//触发跳转
                    long ended_skip_time = (long) (eventComponent.active_skip_time * 1000);
                    if (ended_skip_time >= 0 && iComponentListener != null) {
                        iComponentListener.onComponentSeek(ended_skip_time);
                    }
                } else if ("toggle_playpause".equals(choice)) {//播放或者暂停
                    if (iComponentListener != null) {
                        if (iComponentListener.isVideoPlaying()) {
                            iComponentListener.ctrlPlayer(false);
                        } else {
                            iComponentListener.ctrlPlayer(true);
                        }
                    }
                } else if ("play".equals(choice)) {//播放
                    if (iComponentListener != null) {
                        if (!iComponentListener.isVideoPlaying()) {
                            iComponentListener.ctrlPlayer(true);
                        }
                    }
                } else if ("pause".equals(choice)) {//暂停
                    if (iComponentListener != null) {
                        if (iComponentListener.isVideoPlaying()) {
                            iComponentListener.ctrlPlayer(false);
                        }
                    }
                } else if ("href_url".equals(choice)) {//跳转链接
                    if (iComponentListener != null) {
                        iComponentListener.hrefUrl(eventFeature.href_url);
                    }
                } else if ("call_phone".equals(choice)) {//打电话
                    if (iComponentListener != null) {
                        iComponentListener.callPhone(eventFeature.call_phone);
                    }
                }
            }

        } else {//未触发
            long ended_skip_time = (long) (eventComponent.ended_skip_time * 1000);
            if (ended_skip_time >= 0) {
                iComponentListener.onComponentSeek(ended_skip_time);
            }
        }

        //触发音效
        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null && !options.isEmpty()) {
            VideoProtocolInfo.EventOption option = options.get(0);
            VideoProtocolInfo.EventOptionCustom optionCustom = option.custom;
            if (optionCustom != null) {
                VideoProtocolInfo.EventOptionStatus status;
                if (result) {
                    status = optionCustom.click_ended;
                } else {
                    status = optionCustom.click_failed;
                }

                if (status != null) {
                    String audioUrl = status.audio_url;
                    if (!NativeViewUtils.isNullOrEmptyString(audioUrl)) {

                        File localFile = new File(NativeViewUtils.getDowmloadFilePath(mContext), NativeViewUtils.getFileName(audioUrl));
                        if (localFile.exists()) {
                            SoundManager.getInstance().play(localFile.getAbsolutePath());
                        }
                    }
                }
            }
        }

    }


    /**
     * 选中组件
     *
     * @param eventComponent
     * @param optionIndex
     */
    private void setSelectedComponentResult(VideoProtocolInfo.EventComponent eventComponent, int optionIndex) {


        boolean resultView = false;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                VideoProtocolInfo.EventOption option = options.get(i);
                if (!option.hide_option) {
                    VideoProtocolInfo.EventOptionCustom optionCustom = option.custom;
                    if (optionCustom != null) {
                        if (i == optionIndex) {

                            if (optionCustom.click_ended != null && !NativeViewUtils.isNullOrEmptyString(optionCustom.click_ended.image_name)) {
                                resultView = true;
                            }

                        } else {
                            if (optionCustom.click_failed != null && !NativeViewUtils.isNullOrEmptyString(optionCustom.click_failed.image_name)) {
                                resultView = true;
                            }
                        }
                    }
                }
            }
        }

        if (resultView) {

            //显示出发后效果
            SelectedComponent selectedComponentResult = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (selectedComponentResult == null) {

                selectedComponentResult = new SelectedComponent(mContext);
                selectedComponentResult.setTag(createId(eventComponent.event_id));
                selectedComponentResult.setComponentOption(optionIndex, eventComponent, parentWidth, parentHeight, videoWidth, videoHeight);
                selectedComponentResult.setOnShowResultListener(new OnComponentResultListener() {
                    @Override
                    public void onShowResultFinish(String enent_id) {

                        SelectedComponent view = rootView.findViewWithTag(createId(enent_id));
                        if (view != null) {
                            rootView.removeView(view);
                        }
                    }
                });
                rootView.addView(selectedComponentResult, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }

        if (options != null) {
            VideoProtocolInfo.EventOption option = options.get(optionIndex);

            Log.d("ComponentResult", "optionIndex=" + optionIndex + "---" + option.skip_start_time);

            //跳转帧
            if (option.skip_start_time >= 0) {
                if (iComponentListener != null) {
                    iComponentListener.onComponentSeek((long) (option.skip_start_time * 1000));
                }
            }

            //触发音效
            if (option.custom != null && option.custom.click_ended != null) {

                String audioUrl = option.custom.click_ended.audio_url;
                if (!NativeViewUtils.isNullOrEmptyString(audioUrl)) {

                    File localFile = new File(NativeViewUtils.getDowmloadFilePath(mContext), NativeViewUtils.getFileName(audioUrl));
                    if (localFile.exists()) {
                        SoundManager.getInstance().play(localFile.getAbsolutePath());
                    }
                }
            }


        }


    }


    private String createId(String id) {
        return id + "_result";
    }

}
