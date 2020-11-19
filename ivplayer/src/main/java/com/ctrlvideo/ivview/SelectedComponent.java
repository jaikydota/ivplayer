package com.ctrlvideo.ivview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctrlvideo.comment.net.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.NativeViewUtils;

import java.util.List;

/**
 * 选项选择类组件
 */
public class SelectedComponent extends RelativeLayout {

    private VideoProtocolInfo.EventComponent eventComponent;

    private float videoWidth;
    private float videoHeight;
    private float parentWidth;
    private float parentHeight;


    public SelectedComponent(Context context) {
        this(context, null);
    }

    public SelectedComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectedComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initParmas(float videoWidth, float videoHeight, float parentWidth, float parentHeight) {

        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;

        float ratio = videoWidth / videoHeight;
        if (ratio >= parentWidth / parentHeight) {


            this.videoWidth = parentWidth;
            this.videoHeight = parentHeight / videoWidth * videoHeight;
        } else {

            this.videoWidth = parentHeight / videoHeight * videoWidth;
            this.videoHeight = parentHeight;
        }


    }

    public void initEventComponent(VideoProtocolInfo.EventComponent eventComponent) {
        this.eventComponent = eventComponent;

        List<VideoProtocolInfo.EventOption> options = eventComponent.options;
        if (options == null || options.isEmpty())
            return;

        for (VideoProtocolInfo.EventOption option : options) {
            VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;

            boolean align_screen = option.align_screen;
            if (optionStyle != null) {

                RelativeLayout container = new RelativeLayout(getContext());
//                container.setBackgroundColor(Color.parseColor("#D81B60"));
                container.setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));


                VideoProtocolInfo.EventOptionFilter optionFilter = optionStyle.filter;
                if (optionFilter != null) {
                    container.setAlpha(optionFilter.opacity / 100);
                }


                float width = 0;
                float height = 0;

                float left = 0;
                float top = 0;


                if (align_screen) {

                    width = parentWidth * optionStyle.width / 100;
                    height = parentHeight * optionStyle.height / 100;

                    left = parentWidth * optionStyle.left / 100;
                    top = parentHeight * optionStyle.top / 100;

                } else {
                    width = videoWidth * optionStyle.width / 100;
                    height = videoHeight * optionStyle.height / 100;

                    left = videoWidth * optionStyle.left / 100 + ((parentWidth - videoWidth) / 2);
                    top = videoHeight * optionStyle.top / 100 + ((parentHeight - videoHeight) / 2);

                }

                LayoutParams containerParmas = new LayoutParams((int) width, (int) height);
                containerParmas.leftMargin = (int) left;
                containerParmas.topMargin = (int) top;

                container.setRotation(optionStyle.rotate);


                //文字
                TextView textView = new TextView(getContext());
                LayoutParams textLayoutParmas = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                textLayoutParmas.addRule(RelativeLayout.CENTER_IN_PARENT);
                textView.setText(optionStyle.text);
                textView.setTextColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.color)));

                if ("vertical-lr".equals(optionStyle.writing_mode)) {
                    textView.setRotation(90);
                }



                container.addView(textView, textLayoutParmas);


                addView(container, containerParmas);

//                optionStyle.align_screen
            }
        }


    }


}
