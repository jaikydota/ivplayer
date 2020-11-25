package com.ctrlvideo.nativeivview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctrlvideo.comment.net.VideoProtocolInfo;
import com.ctrlvideo.ivplayer.R;

import java.io.File;

public class OptionView extends RelativeLayout {


    private int status = STATUS_DEFAULT;

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_CLICK_ON = 1;
    public static final int STATUS_CLICK_ENDED = 2;
    public static final int STATUS_CLICK_FAILED = 3;



    private ImageView imageView;
    private TextView textView;

    public OnOptionViewListener listener;

    public void setOnOptionViewListener(OnOptionViewListener listener) {
        this.listener = listener;
    }

    public interface OnOptionViewListener {

        void onTrigger();

        void onTriggerAfter();

    }


    public OptionView(Context context) {
        this(context, null);
    }

    public OptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.view_option, this);

        imageView = findViewById(R.id.view_image);
        textView = findViewById(R.id.view_text);

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (status == STATUS_DEFAULT || status == STATUS_CLICK_ON) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:


                    if (listener != null ) {
                        listener.onTrigger();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (listener != null ) {
                        listener.onTriggerAfter();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;

            }
            return true;
        }

        return super.onTouchEvent(event);


    }


    public void setOption(int status, VideoProtocolInfo.EventOption option) {

        this.status = status;

        VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
        if (optionStyle != null) {
            // 设置背景
            setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
            VideoProtocolInfo.EventOptionFilter optionFilter = optionStyle.filter;
            //设置透明度
            if (optionFilter != null) {
                setAlpha(optionFilter.opacity / 100);
            }

            //设置旋转角度
            setRotation(optionStyle.rotate);


            //设置文字样式
            textView.setText(optionStyle.text);
            textView.setTextColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.color)));

            if ("vertical-lr".equals(optionStyle.writing_mode)) {
                textView.setRotation(90);
            }
        }


        VideoProtocolInfo.EventOptionCustom optionCustom = option.custom;

        if (optionCustom != null) {
            VideoProtocolInfo.EventOptionStatus eventOptionStatus = optionCustom.click_default;
            if (status == STATUS_CLICK_ON) {
                eventOptionStatus = optionCustom.click_on;
            } else if (status == STATUS_CLICK_ENDED) {
                eventOptionStatus = optionCustom.click_ended;
            } else if (status == STATUS_CLICK_FAILED) {
                eventOptionStatus = optionCustom.click_failed;
            }
            if (eventOptionStatus != null) {
                String defaultImage = eventOptionStatus.image_url;
                if (!NativeViewUtils.isNullOrEmptyString(defaultImage)) {
                    File localFile = new File(NativeViewUtils.getDowmloadFilePath(), NativeViewUtils.getFileName(defaultImage));
                    if (localFile.exists()) {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                    }
                }
            }
        }


    }
}
