package com.ctrlvideo.nativeivview.component;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.ctrlvideo.ivplayer.PlayerState;
import com.ctrlvideo.nativeivview.audioplayer.SoundManager;
import com.ctrlvideo.nativeivview.constant.EventClassify;
import com.ctrlvideo.nativeivview.constant.EventType;
import com.ctrlvideo.nativeivview.model.EventActionInfoCallback;
import com.ctrlvideo.nativeivview.model.EventPresentInfoCallback;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;
import com.ctrlvideo.nativeivview.widget.OptionView;

import java.io.File;
import java.util.List;

public class ComponentManager {

    private String TAG = "ComponentManager";

    private Context mContext;

    private float videoWidth;
    private float videoHeight;

    private IComponentListener iComponentListener;

    private RelativeLayout rootView;


    public void initParmas(Context context, RelativeLayout rootView, VideoProtocolInfo videoProtocolInfo, IComponentListener iComponentListener) {
        this.rootView = rootView;
        this.mContext = context;


        this.iComponentListener = iComponentListener;


        VideoProtocolInfo.ReleaseInfo releaseInfo = videoProtocolInfo.release_info;
        if (releaseInfo != null) {
            VideoProtocolInfo.VideoParams params = releaseInfo.v_params;
            if (params != null) {

                videoWidth = params.width;
                videoHeight = params.height;
            }
        }

    }

    //    private boolean videoPlaying;
    private String status;

    /**
     * 播放器状态
     *
     * @param status
     */
    public void setVideoPlayerStatus(String status) {
        this.status = status;
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
            if (EventType.SELECT.getType().equals(type) || EventType.CLICK.getType().equals(type)) {
                iComponentListener.onEventCallback(new EventPresentInfoCallback(eventComponent).toJson());
            } else if (EventType.PASSIVITY.getType().equals(type)) {//被动触发
                passiveTrigger(eventComponent);
            }
        }

//        listener.onEventCallback(new EventPresentInfoCallback(eventComponent).toJson());
    }

    /**
     * 被动触发
     *
     * @param eventComponent
     */
    private void passiveTrigger(VideoProtocolInfo.EventComponent eventComponent) {

        LogUtils.d(TAG, "被动触发-------" + eventComponent.event_id);


        VideoProtocolInfo.EventFeature eventFeature = eventComponent.feature;
        if (eventFeature != null) {
            String choice = eventFeature.choice;
            if ("none".equals(choice)) {
                long skip_time = (long) (eventComponent.active_skip_time * 1000);
                if (skip_time >= 0 && iComponentListener != null) {
                    videoSeek(skip_time);
                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }
            } else if ("play".equals(choice)) {//播放
                if (iComponentListener != null) {
//                    if (!videoPlaying) {
//                        iComponentListener.ctrlPlayer(true);
//                    }

                    videoPlay(true);

                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }

                needPlay = false;
                showBottomCtrllerView();

            } else if ("pause".equals(choice)) {//暂停
                if (iComponentListener != null) {
//                    if (videoPlaying) {
//                        iComponentListener.ctrlPlayer(false);
//                    }
                    videoPlay(false);
                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }
                needPlay = true;

                showBottomCtrllerView();

            } else if ("href_url".equals(choice)) {//跳转链接
                if (iComponentListener != null) {
                    iComponentListener.onHrefUrl(eventFeature.href_url);
                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }
            }
        }

    }

    /**
     * 视频跳转
     */
    private void videoSeek(long position) {

        if (iComponentListener != null) {
            if (PlayerState.STATE_END.equals(status)) {//视频结尾处视频自动暂停
                iComponentListener.ctrlPlayer(false);
            }
            iComponentListener.onComponentSeek(position);


        }
    }

    /**
     * 视频播放或者暂停
     *
     * @param play
     */
    private void videoPlay(boolean play) {
        if (iComponentListener != null) {
            iComponentListener.ctrlPlayer(play);
        }
    }


    /**
     * 是否显示底部播控
     */
    private void showBottomCtrllerView() {

        if (iComponentListener != null) {
            iComponentListener.onShowBottomControllerView(!needPlay);
        }

    }

    //自动播放
    private boolean needPlay;

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
            selectedComponent.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
            selectedComponent.initComponent(OptionView.STATUS_DEFAULT, eventComponent);
            selectedComponent.setOnSelectedListener(new SelectedComponent.OnSelectedListener() {
                @Override
                public void onOptionSelected(int option) {

                    LogUtils.d("ComponentManger", "onOptionSelected===" + option);

                    setSelectedComponentResult(eventComponent, option);
                }
            });
            rootView.addView(selectedComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {

            selectedComponent.checkLoadFinish();
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
            clickComponent.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
            clickComponent.setTag(eventComponent.event_id);
            clickComponent.initComponent(OptionView.STATUS_DEFAULT, eventComponent);
            clickComponent.setOnOptionClickListener(new ClickComponent.OnOptionClickListener() {
                @Override
                public void onOptionClick(int option) {
//                    LogUtils.d(TAG, "option===" + option);

                    setClickComponentResult(eventComponent, true);
                }
            });
            rootView.addView(clickComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } else {

            clickComponent.checkLoadFinish();
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

                videoSeek((long) (eventComponent.start_time * 1000));

//                iComponentListener.onComponentSeek((long) (eventComponent.start_time * 1000));

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
//        iComponentListener.onEventActionCallback(new EventActionInfoCallback(eventComponent, result).toJson());

        LogUtils.d(TAG, "单击选择-----" + result);

        if ((result && hasTriggersucceedSuStyle(eventComponent)) || (!result && hasTriggerFailStyle(eventComponent))) {


            ClickComponent clickComponent = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (clickComponent == null) {

                clickComponent = new ClickComponent(mContext);
                clickComponent.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                clickComponent.setTag(createId(eventComponent.event_id));
                clickComponent.setComponentOption(result, eventComponent);
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
                        videoSeek(ended_skip_time);
                        consumePassivePause();
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                    }
                } else if ("toggle_playpause".equals(choice)) {//播放或者暂停
                    if (iComponentListener != null) {

//                        if (PlayerState.STATE_ONPLAY.equals(status)){
//                            iComponentListener.ctrlPlayer(false);
//                        }else {
//                            iComponentListener.ctrlPlayer(true);
//                        }

                        videoPlay(!PlayerState.STATE_ONPLAY.equals(status));

//                        if (videoPlaying) {
//                            iComponentListener.ctrlPlayer(false);
//                        } else {
//                            iComponentListener.ctrlPlayer(true);
//                        }
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());

                    }
                } else if ("play".equals(choice)) {//播放
                    if (iComponentListener != null) {
//                        if (!videoPlaying) {
//                            iComponentListener.ctrlPlayer(true);
//                        }
                        videoPlay(true);
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                    }
                } else if ("pause".equals(choice)) {//暂停
                    if (iComponentListener != null) {
//                        if (videoPlaying) {
//                            iComponentListener.ctrlPlayer(false);
//                        }
                        videoPlay(false);
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                    }
                } else if ("href_url".equals(choice)) {//跳转链接
                    if (iComponentListener != null) {
                        iComponentListener.onHrefUrl(eventFeature.href_url);
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                    }

                } else if ("call_phone".equals(choice)) {//打电话
                    if (iComponentListener != null) {
                        iComponentListener.callPhone(eventFeature.call_phone);
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                    }

                }
            }

        } else {//未触发
            long ended_skip_time = (long) (eventComponent.ended_skip_time * 1000);
            if (ended_skip_time >= 0) {
                videoSeek(ended_skip_time);
                iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, false).toJson());
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
                selectedComponentResult.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                selectedComponentResult.setTag(createId(eventComponent.event_id));
                selectedComponentResult.setComponentOption(optionIndex, eventComponent);
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

            LogUtils.d("ComponentResult", "optionIndex=" + optionIndex + "---" + option.skip_start_time);

            //跳转帧
            if (option.skip_start_time >= 0) {
                if (iComponentListener != null) {
                    videoSeek((long) (option.skip_start_time * 1000));
                    consumePassivePause();
                    showBottomCtrllerView();
                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, optionIndex).toJson());
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

    /**
     * 消耗被动暂停
     */
    private void consumePassivePause() {
        if (needPlay) {
            needPlay = false;

            videoPlay(true);
//            if (!videoPlaying) {
//                iComponentListener.ctrlPlayer(true);
//            }
        }
    }


    private String createId(String id) {
        return id + "_result";
    }

//    public void release() {
//        rootView.removeAllViews();
//    }
}
