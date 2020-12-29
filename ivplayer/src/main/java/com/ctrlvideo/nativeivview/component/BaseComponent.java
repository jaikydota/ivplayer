package com.ctrlvideo.nativeivview.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.widget.OptionView;

import java.util.List;

public class BaseComponent extends RelativeLayout {

    protected VideoProtocolInfo.EventComponent eventComponent;


    private float videoWidthPixel;
    private float videoHeightPixel;

    protected float parentWidth;
    protected float parentHeight;

    protected float videoWidth;
    protected float videoHeight;


    public BaseComponent(Context context) {
        this(context, null);
    }

    public BaseComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initParmas(float parentWidth, float parentHeight, float videoWidthPixel, float videoHeightPixel) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.videoWidthPixel = videoWidthPixel;
        this.videoHeightPixel = videoHeightPixel;

        calculateVideoSize();
    }

    protected void calculateVideoSize() {

        float ratio = videoWidthPixel / videoWidthPixel;
        if (ratio >= (parentWidth / parentHeight)) {
            this.videoWidth = parentWidth;
            this.videoHeight = parentWidth / videoWidthPixel * videoHeightPixel;
        } else {
            this.videoWidth = parentHeight / videoHeightPixel * videoWidthPixel;
            this.videoHeight = parentHeight;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);

//        LogUtils.d("onMeasure", "width=" + width + "----height=" + height + "----count=" + count);


        if (parentWidth != width && parentHeight != height) {
            LogUtils.d("BaseComponent", "重新调整子控件位置");
            parentWidth = width;
            parentHeight = height;
            calculateVideoSize();
            resetOptionView();
        }

    }

    private void resetOptionView() {

        int count = getChildCount();

        for (int index = 0; index < count; index++) {
            OptionView optionView = (OptionView) getChildAt(index);

            String tagId = (String) optionView.getTag();
            VideoProtocolInfo.EventOption option = getOption(tagId);


            if (option != null) {

                option.containerWidth = parentWidth;
                option.containerHeight = parentHeight;
                option.videoWidth = videoWidth;
                option.videoHeight = videoHeight;


//                float width = 0;
//                float height = 0;
//                float left = 0;
//                float top = 0;
//
//                VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
//                if (optionStyle != null) {
//                    boolean align_screen = option.align_screen;
//                    if (align_screen) {
//                        width = parentWidth * optionStyle.width / 100;
//                        height = parentHeight * optionStyle.height / 100;
//                        left = parentWidth * optionStyle.left / 100;
//                        top = parentHeight * optionStyle.top / 100;
//                    } else {
//                        width = videoWidth * optionStyle.width / 100;
//                        height = videoHeight * optionStyle.height / 100;
//                        left = videoWidth * optionStyle.left / 100 + ((parentWidth - videoWidth) / 2);
//                        top = videoHeight * optionStyle.top / 100 + ((parentHeight - videoHeight) / 2);
//                    }
//                }

                LayoutParams layoutParams = (LayoutParams) optionView.getLayoutParams();

                layoutParams.width = (int) option.getWidth();
                layoutParams.height = (int) option.getHeight();
                layoutParams.leftMargin = (int) option.getLeft();
                layoutParams.topMargin = (int) option.getTop();

                optionView.setLayoutParams(layoutParams);
            }


        }

    }


    public VideoProtocolInfo.EventOption getOption(String option_id) {

        if (eventComponent != null) {
            List<VideoProtocolInfo.EventOption> options = eventComponent.options;
            for (VideoProtocolInfo.EventOption option : options) {
                if (option_id.equals(option.option_id)) {
                    return option;
                }
            }
        }
        return null;
    }
}
