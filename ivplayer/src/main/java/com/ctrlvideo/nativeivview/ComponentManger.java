package com.ctrlvideo.nativeivview;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ctrlvideo.comment.net.VideoProtocolInfo;

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


    public void initComponent(VideoProtocolInfo.EventComponent eventComponent) {

        String classify = eventComponent.classify;
        String type = eventComponent.type;
        if ("TE".equals(classify)) {
            if ("select".equals(type)) {
                initSelectedComponent(eventComponent);
            }

        }
    }

    public void componentEnd(VideoProtocolInfo.EventComponent eventComponent) {

        SelectedComponent selectedComponent = rootView.findViewWithTag(eventComponent.event_id);
        if (selectedComponent != null) {
            rootView.removeView(selectedComponent);
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

                    Log.d("ComponentManger","onOptionSelected==="+option);

                    setSelectedComponentResult(eventComponent, option);
                }
            });
            rootView.addView(selectedComponent, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        }
    }


    /**
     * 选中组件
     *
     * @param eventComponent
     * @param option
     */
    private void setSelectedComponentResult(VideoProtocolInfo.EventComponent eventComponent, int option) {

        SelectedComponent selectedComponentResult = rootView.findViewWithTag(createId(eventComponent.event_id));
        if (selectedComponentResult == null) {
            selectedComponentResult = new SelectedComponent(mContext);
            selectedComponentResult.setTag(createId(eventComponent.event_id));
            selectedComponentResult.setComponentOption(option, eventComponent, parentWidth, parentHeight, videoWidth, videoHeight);
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

    }


    private String createId(String id) {
        return id + "_result";
    }

}
