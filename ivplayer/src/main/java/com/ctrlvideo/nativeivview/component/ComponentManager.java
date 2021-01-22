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

public class ComponentManager implements BaseComponent.OnComponentOptionListener, OnComponentResultListener {

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

            if (EventType.SELECT.getType().equals(type) || EventType.CLICK.getType().equals(type) || EventType.RAPIDCLICK.getType().equals(type) || EventType.LONGPRESS.getType().equals(type)) {
                initComponentView(eventComponent);
            }
        }
    }


    /**
     * 事件范围外 ui 渲染
     *
     * @param eventComponent
     */
    public void eventScopeOut(VideoProtocolInfo.EventComponent eventComponent) {
        View view = rootView.findViewWithTag(eventComponent.event_id);
        if (view != null) {
//            if (view instanceof BaseComponent) {
//                BaseComponent baseComponent = (BaseComponent) view;
//                baseComponent.releaseResourceLoadFailTime();
//            }
            rootView.removeView(view);
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
            if (EventType.SELECT.getType().equals(type) || EventType.CLICK.getType().equals(type) || EventType.RAPIDCLICK.getType().equals(type) || EventType.LONGPRESS.getType().equals(type)) {
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
                    consumePassivePause();
                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }
            } else if ("play".equals(choice)) {//播放
                if (iComponentListener != null) {
//                    if (!videoPlaying) {
//                        iComponentListener.ctrlPlayer(true);
//                    }

                    videoPlay(true);
                    consumePassivePause();

                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }


            } else if ("pause".equals(choice)) {//暂停
                if (iComponentListener != null) {
//                    if (videoPlaying) {
//                        iComponentListener.ctrlPlayer(false);
//                    }
//                    videoPlay(false);
                    triggerPassivePause();
                    iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
                }

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
            iComponentListener.onShowBottomControllerView(!passivePause);
        }

    }


    private void initComponentView(VideoProtocolInfo.EventComponent eventComponent) {
        BaseComponent component = rootView.findViewWithTag(eventComponent.event_id);
        if (component == null) {

            String type = eventComponent.type;

            if (EventType.SELECT.getType().equals(type)) {
                component = new SelectedComponent(mContext);
            } else if (EventType.CLICK.getType().equals(type)) {
                component = new ClickComponent(mContext);
            } else if (EventType.RAPIDCLICK.getType().equals(type)) {
                component = new RepeatClickComponent(mContext);
            } else if (EventType.LONGPRESS.getType().equals(type)) {
                component = new LongPressComponent(mContext);
            }
            if (component != null) {
                component.setTag(eventComponent.event_id);
                component.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                component.setOnComponentOptionListener(this);
                component.initComponent(OptionView.STATUS_DEFAULT, eventComponent);
                rootView.addView(component, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

//                if (!component.isLoadFinish()) {
//                    Log.d("initComponentView", "第一次加载 资源未加载---" + eventComponent.event_id);
//                    if (!assetLoading) {
//                        assetLoading = true;
//                    }
//                } else {
//                    Log.d("initComponentView", "第一次加载 资源已经加载---" + eventComponent.event_id);
//                    assetLoading = false;
//                }
            }

        } else {


//            if (assetLoading) {
//                Log.d("initComponentView", "资源未加载 再一次加载--- " + eventComponent.event_id);
            component.checkLoadFinish();
//                if (component.isLoadFinish()) {
//
//                    Log.d("initComponentView", "资源加载完成---" + eventComponent.event_id);
//                    assetLoading = false;
//                }
//            }

//            if (!component.isLoadFinish()) {
//                Log.d("initComponentView", "不是第一次加载 资源未加载");
//            } else {
//                Log.d("initComponentView", "不是第一次加载 资源已经加载");
//            }


        }

//        boolean load = checkMediaResourceLoad();
//        if (load == assetLoading) {
//            assetLoading = !load;
//            if (assetLoading) {
//                LogUtils.d("initComponentView", "正在加载中---" + eventComponent.event_id);
//            } else {
//                LogUtils.d("initComponentView", "资源加载完成---" + eventComponent.event_id);
//            }
//
//        }
    }

    public boolean checkMediaResourceLoad() {

        boolean load = true;

        int count = rootView.getChildCount();
        for (int index = 0; index < count; index++) {
            View view = rootView.getChildAt(index);
            if (view instanceof BaseComponent) {
                BaseComponent baseComponent = (BaseComponent) view;
                if (!baseComponent.isLoadFinish()) {
                    load = false;
                    break;
                }
            }
        }
        return load;
    }

    private boolean assetLoading;

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
            } else if (EventType.RAPIDCLICK.getType().equals(eventComponent.type)) {
                setRepeatClickComponentResult(eventComponent, false);
            } else if (EventType.LONGPRESS.getType().equals(eventComponent.type)) {
                setLongPressComponentResult(eventComponent, false);
            }
        }
    }

    /**
     * 单击组件
     *
     * @param eventComponent
     * @param result
     */
    private void setClickComponentResult(VideoProtocolInfo.EventComponent eventComponent, boolean result) {
//        iComponentListener.onEventActionCallback(new EventActionInfoCallback(eventComponent, result).toJson());

//        LogUtils.d(TAG, "单击选择-----" + result);

//        if ((result && hasTriggersucceedSuStyle(eventComponent)) || (!result && hasTriggerFailStyle(eventComponent))) {


        boolean resultView = false;
        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                VideoProtocolInfo.EventOption option = options.get(i);
                if (!option.hide_option) {
                    if (option.hasResultView(result)) {
                        resultView = true;
                        break;
                    }
                }
            }
        }

        LogUtils.d("setClickComponentResult", "resultView=" + resultView);

        if (resultView) {
            ClickComponent clickComponent = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (clickComponent == null) {

                clickComponent = new ClickComponent(mContext);
                clickComponent.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                clickComponent.setTag(createId(eventComponent.event_id));
                clickComponent.setComponentOption(result, eventComponent);
                clickComponent.setOnShowResultListener(this);
                rootView.addView(clickComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }


//        }


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

                        if (PlayerState.STATE_ONPLAY.equals(status)) {
                            videoPlay(false);
                        } else {
                            videoPlay(true);
                            consumePassivePause();
                        }
                        iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());

                    }
                } else if ("play".equals(choice)) {//播放
                    if (iComponentListener != null) {
                        videoPlay(true);
                        consumePassivePause();
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
//        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
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
     * 重复点击触发
     *
     * @param eventComponent
     * @param result
     */
    private void setRepeatClickComponentResult(VideoProtocolInfo.EventComponent eventComponent, boolean result) {
//        iComponentListener.onEventActionCallback(new EventActionInfoCallback(eventComponent, result).toJson());

        LogUtils.d(TAG, "重复点击-----" + result);

        boolean resultView = false;
        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                VideoProtocolInfo.EventOption option = options.get(i);
                if (!option.hide_option) {
                    if (option.hasResultView(result)) {
                        resultView = true;
                        break;
                    }
                }
            }
        }
        LogUtils.d("setRepeatClickComponentResult", "resultView=" + resultView);

        if (resultView) {


            RepeatClickComponent repeatClickComponent = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (repeatClickComponent == null) {

                repeatClickComponent = new RepeatClickComponent(mContext);
                repeatClickComponent.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                repeatClickComponent.setTag(createId(eventComponent.event_id));
                repeatClickComponent.setComponentOption(result, eventComponent);
                repeatClickComponent.setOnShowResultListener(this);
                rootView.addView(repeatClickComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }


        if (result) {//触发功能

            long ended_skip_time = (long) (eventComponent.active_skip_time * 1000);
            if (ended_skip_time >= 0 && iComponentListener != null) {
                videoSeek(ended_skip_time);
                consumePassivePause();
                iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
            }

        } else {//未触发
            long ended_skip_time = (long) (eventComponent.ended_skip_time * 1000);
            if (ended_skip_time >= 0) {
                videoSeek(ended_skip_time);
                iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, false).toJson());
            }
        }

        //触发音效
//        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
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
     * 长按结果
     *
     * @param eventComponent
     * @param result
     */
    private void setLongPressComponentResult(VideoProtocolInfo.EventComponent eventComponent, boolean result) {
//        iComponentListener.onEventActionCallback(new EventActionInfoCallback(eventComponent, result).toJson());

        LogUtils.d(TAG, "长按-----" + result);

        boolean resultView = false;
        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                VideoProtocolInfo.EventOption option = options.get(i);
                if (!option.hide_option) {
                    if (option.hasResultView(result)) {
                        resultView = true;
                        break;
                    }
                }
            }
        }

        if (resultView) {
            LongPressComponent longPressComponent = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (longPressComponent == null) {

                longPressComponent = new LongPressComponent(mContext);
                longPressComponent.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                longPressComponent.setTag(createId(eventComponent.event_id));
                longPressComponent.setComponentOption(result, eventComponent);
                longPressComponent.setOnShowResultListener(this);
                rootView.addView(longPressComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }

        LogUtils.d("setLongPressComponentResult", "resultView=" + resultView);


        if (result) {//触发功能

            long ended_skip_time = (long) (eventComponent.active_skip_time * 1000);
            if (ended_skip_time >= 0 && iComponentListener != null) {
                videoSeek(ended_skip_time);
                consumePassivePause();
                iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, true).toJson());
            }

        } else {//未触发
            long ended_skip_time = (long) (eventComponent.ended_skip_time * 1000);
            if (ended_skip_time >= 0) {
                videoSeek(ended_skip_time);
                iComponentListener.onEventCallback(new EventActionInfoCallback(eventComponent, false).toJson());
            }
        }

        //触发音效
//        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
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
                    if (option.hasResultView(i == optionIndex)) {
                        resultView = true;
                        break;
                    }
                }
            }
        }

        LogUtils.d("setSelectedComponentResult", "resultView=" + resultView);

        if (resultView) {

            //显示出发后效果
            SelectedComponent selectedComponentResult = rootView.findViewWithTag(createId(eventComponent.event_id));
            if (selectedComponentResult == null) {

                selectedComponentResult = new SelectedComponent(mContext);
                selectedComponentResult.initParmas(rootView.getMeasuredWidth(), rootView.getMeasuredHeight(), videoWidth, videoHeight);
                selectedComponentResult.setTag(createId(eventComponent.event_id));
                selectedComponentResult.setComponentOption(optionIndex, eventComponent);
                selectedComponentResult.setOnShowResultListener(this);
                rootView.addView(selectedComponentResult, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        }

        if (options != null) {
            VideoProtocolInfo.EventOption option = options.get(optionIndex);

//            LogUtils.d("ComponentResult", "optionIndex=" + optionIndex + "---" + option.skip_start_time);

            //跳转帧
            if (option.skip_start_time >= 0) {
                if (iComponentListener != null) {
                    videoSeek((long) (option.skip_start_time * 1000));
                    consumePassivePause();
//                    showBottomCtrllerView();
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

    private boolean passivePause = false;

    /**
     * 消耗被动暂停
     */
    private void consumePassivePause() {

        if (passivePause) {
            passivePause = false;
            showBottomCtrllerView();
            videoPlay(true);
        }

    }

    /**
     * 触发被动暂停
     */
    private void triggerPassivePause() {
        passivePause = true;
        showBottomCtrllerView();
        videoPlay(false);
    }


    private String createId(String id) {
        return id + "_result";
    }

    @Override
    public void onOptionSelected(int optionIndex, VideoProtocolInfo.EventComponent eventComponent) {
        setSelectedComponentResult(eventComponent, optionIndex);
    }

    @Override
    public void onOptionClick(VideoProtocolInfo.EventComponent eventComponent) {
        setClickComponentResult(eventComponent, true);
    }

    @Override
    public void onOptionRepeatClick(VideoProtocolInfo.EventComponent eventComponent) {
        setRepeatClickComponentResult(eventComponent, true);
    }

    @Override
    public void onOptionLongPress(VideoProtocolInfo.EventComponent eventComponent) {
        setLongPressComponentResult(eventComponent, true);
    }


    @Override
    public void onShowResultFinish(String enent_id) {
        View view = rootView.findViewWithTag(createId(enent_id));
        if (view != null) {
            rootView.removeView(view);
        }
    }

    public void resume() {

        int count = rootView.getChildCount();
        for (int index = 0; index < count; index++) {
            View view = rootView.getChildAt(index);
            if (view instanceof BaseComponent) {
                BaseComponent baseComponent = (BaseComponent) view;
                baseComponent.resume();
            }
        }



    }

    public void pause() {
        int count = rootView.getChildCount();
        for (int index = 0; index < count; index++) {
            View view = rootView.getChildAt(index);
            if (view instanceof BaseComponent) {
                BaseComponent baseComponent = (BaseComponent) view;
                baseComponent.pause();
            }
        }
    }

    //    public void release() {
//        rootView.removeAllViews();
//    }
}
