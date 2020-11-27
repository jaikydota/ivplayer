package com.ctrlvideo.nativeivview;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ctrlvideo.comment.net.VideoProtocolInfo;

import java.util.List;

public class ComponentManger {

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
     * 事件触发
     *
     * @param eventComponent
     */
    public void eventTrigger(VideoProtocolInfo.EventComponent eventComponent) {

        String classify = eventComponent.classify;
        String type = eventComponent.type;
        if ("TE".equals(classify)) {
            if ("select".equals(type)) {
                initSelectedComponent(eventComponent);
            }

        }
    }


    /**
     * 跳出事件范围
     *
     * @param eventComponent
     */
    public void eventJumpout(VideoProtocolInfo.EventComponent eventComponent) {
        SelectedComponent selectedComponent = rootView.findViewWithTag(eventComponent.event_id);
        if (selectedComponent != null) {
            rootView.removeView(selectedComponent);
        }
    }

    /**
     * 事件结束点
     *
     * @param eventComponent
     */
    public void componentEnd(VideoProtocolInfo.EventComponent eventComponent) {


        String classify = eventComponent.classify;
        String type = eventComponent.type;
        if ("TE".equals(classify)) {
            if ("select".equals(type)) {
                endSelectedComponent(eventComponent);
            }
        }
    }

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

    private void endSelectedComponent(VideoProtocolInfo.EventComponent eventComponent) {

        if (!eventComponent.time_limit) {

            if (iComponentListener != null) {
                iComponentListener.onComponentSeek((long) (eventComponent.start_time * 1000));

            }
        } else {
            setSelectedComponentResult(eventComponent, eventComponent.default_skip_option);
        }

    }


    /**
     * 选中组件
     *
     * @param eventComponent
     * @param optionIndex
     */
    private void setSelectedComponentResult(VideoProtocolInfo.EventComponent eventComponent, int optionIndex) {

        SelectedComponent selectedComponentResult = rootView.findViewWithTag(createId(eventComponent.event_id));
        if (selectedComponentResult == null) {
            selectedComponentResult = new SelectedComponent(mContext);
            selectedComponentResult.setTag(createId(eventComponent.event_id));
            selectedComponentResult.setComponentOption(optionIndex, eventComponent, parentWidth, parentHeight, videoWidth, videoHeight);
            selectedComponentResult.setOnShowResultListener(new SelectedComponent.OnShowResultListener() {
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




        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options != null) {


            VideoProtocolInfo.EventOption option = options.get(optionIndex);

            Log.d("ComponentResult","optionIndex="+optionIndex+"---"+option.skip_start_time);

            if (option.skip_start_time >= 0) {
                if (iComponentListener != null) {
                    iComponentListener.onComponentSeek((long) (option.skip_start_time * 1000));
                }
            }


        }


    }


    private String createId(String id) {
        return id + "_result";
    }

}
