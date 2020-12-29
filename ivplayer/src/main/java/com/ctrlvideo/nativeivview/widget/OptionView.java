package com.ctrlvideo.nativeivview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;

import java.io.File;

public class OptionView extends RelativeLayout {


    private int status = STATUS_DEFAULT;

    public static final int STATUS_DEFAULT = 0;
    public static final int STATUS_CLICK_ON = 1;
    public static final int STATUS_CLICK_ENDED = 2;
    public static final int STATUS_CLICK_FAILED = 3;



    private ImageView optionImage;

    public OnOptionViewListener listener;
    private VideoProtocolInfo.EventOption option;


    public void setOnOptionViewListener(OnOptionViewListener listener) {
        this.listener = listener;
    }

    public void reload() {
        load();
    }



    public interface OnOptionViewListener {

        void onTrigger();

        void onTriggerAfter();

        void onTriggerCancel();

    }


    public OptionView(Context context) {
        this(context, null);
    }

    public OptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        optionImage = new ImageView(getContext());

        addView(optionImage, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (status == STATUS_DEFAULT || status == STATUS_CLICK_ON) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:


                    if (listener != null) {
                        listener.onTrigger();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if (listener != null) {
                        listener.onTriggerAfter();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (listener != null) {
                        listener.onTriggerCancel();
                    }
                    break;

            }
            return true;
        }
        return true;
    }
    private void load() {


        VideoProtocolInfo.EventOptionCustom optionCustom = option.custom;
        if (optionCustom != null) {


            VideoProtocolInfo.EventOptionStatus optionStatus = optionCustom.click_default;
            if (status == STATUS_CLICK_ON) {
                optionStatus = optionCustom.click_on;

            } else if (status == STATUS_CLICK_ENDED) {
                optionStatus = optionCustom.click_ended;
            } else if (status == STATUS_CLICK_FAILED) {
                optionStatus = optionCustom.click_failed;
            }

            if (optionStatus != null) {

                String imageUrl = optionStatus.image_url;

                if (NativeViewUtils.isNullOrEmptyString(imageUrl) && status == STATUS_CLICK_ON)
                    return;

                if (!NativeViewUtils.isNullOrEmptyString(imageUrl) && imageUrl.endsWith("gif")) {
                    loadGifView(optionStatus);
                } else {
                    loadView(optionStatus);
                }
            }
        }
    }

    private void loadGifView(VideoProtocolInfo.EventOptionStatus status) {


        File localFile = new File(NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(status.image_url));
        if (localFile.exists()) {

            int count = getChildCount();

            GifOptionView gifOptionView;
            if (count > 0) {
                View view = getChildAt(0);
                if (view instanceof GifOptionView) {
                    gifOptionView = (GifOptionView) getChildAt(0);
                } else {
                    gifOptionView = new GifOptionView(getContext());
                    addView(gifOptionView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    removeView(view);
                }
            } else {
                gifOptionView = new GifOptionView(getContext());
                addView(gifOptionView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            gifOptionView.setData(option, localFile);
            loadFinish = true;
        } else {
            loadFinish = false;
        }
    }

    private void loadView(VideoProtocolInfo.EventOptionStatus optionStatus) {
        int count = getChildCount();
        ImageOptionView imageOptionView;
        if (count > 0) {
            View view = getChildAt(0);
            if (view instanceof ImageOptionView) {
                imageOptionView = (ImageOptionView) view;
            } else {
                imageOptionView = new ImageOptionView(getContext());
                addView(imageOptionView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                removeView(view);
            }
        } else {
            imageOptionView = new ImageOptionView(getContext());
            addView(imageOptionView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        if (NativeViewUtils.isNullOrEmptyString(optionStatus.image_url)) {
            loadFinish = true;
        } else {
            File localFile = new File(NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(optionStatus.image_url));
            if (localFile.exists()) {
                loadFinish = true;
            } else {
                loadFinish = false;
            }
        }

        imageOptionView.setData(option, status);
    }

    public void setOption(int status, VideoProtocolInfo.EventOption option) {
        this.status = status;
        this.option = option;


        VideoProtocolInfo.EventOptionStyle layout_style = option.layout_style;
        if (layout_style != null) {
            setRotation(layout_style.rotate);
        }

        reload();

    }

    private boolean loadFinish = true;

    public boolean isLoadFinish() {
        return loadFinish;
    }


}
