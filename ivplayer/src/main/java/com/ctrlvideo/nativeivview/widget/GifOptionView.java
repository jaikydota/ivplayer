package com.ctrlvideo.nativeivview.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctrlvideo.ivplayer.R;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GifOptionView extends RelativeLayout {

    private ImageView bgImage;
    private GifImageView gifView;
    private TextView textView;

    public GifOptionView(Context context) {
        this(context, null);
    }

    public GifOptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {


        LayoutInflater.from(getContext()).inflate(R.layout.view_option_gif, this);

        bgImage = findViewById(R.id.image_bg);
        gifView = findViewById(R.id.gif_view);
        textView = findViewById(R.id.text_view);

    }

    public void setData(VideoProtocolInfo.EventOption eventOption, File file) {


        VideoProtocolInfo.EventOptionStyle layout_style = eventOption.layout_style;
        if (layout_style != null) {

//            setRotation(layout_style.rotate);

            ColorDrawable drawable = new ColorDrawable(Color.parseColor(NativeViewUtils.transformColor(layout_style.base_color)));
            Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);

            bgImage.setImageDrawable(drawable);


            if (!NativeViewUtils.isNullOrEmptyString(layout_style.text)) {
                textView.setText(layout_style.text);
                textView.setTextColor(Color.parseColor(NativeViewUtils.transformColor(layout_style.color)));

                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, eventOption.getTextSize());


                if ("vertical-lr".equals(layout_style.writing_mode)) {
                    textView.setEms(1);
                } else {
                    textView.setMaxLines(1);
                }
            }
//


            VideoProtocolInfo.EventOptionFilter optionFilter = layout_style.filter;
            if (optionFilter != null) {

                setAlpha(optionFilter.opacity / 100);


                ColorMatrix imageMatrix = new ColorMatrix();

                //饱和度
                try {
                    float saturate = Float.parseFloat(optionFilter.saturate.replace("%", "").trim()) / 100;
                    ColorMatrix saturateMatrix = new ColorMatrix();
                    saturateMatrix.setSaturation(saturate);
                    imageMatrix.postConcat(saturateMatrix);

                } catch (Exception e) {

                }
                //对比度
                try {

                    float contrast = (Float.parseFloat(optionFilter.contrast.replace("%", "").trim()) / 100) - 1.0f;
                    LogUtils.d("OptionView", "contrast=" + contrast);
//                contrast = 1.1f;
                    // -1 --- 1   0 原图
                    ColorMatrix contrastMatrix = new ColorMatrix();
                    float scale = contrast + 1.f;
                    float translate = (-.5f * scale + .5f) * 255.f;
//                LogUtils.d("OptionView", "contrast=" + contrast + "----scale=" + scale + "----translate=" + translate);
                    contrastMatrix.set(new float[]{
                            scale, 0, 0, 0, translate,
                            0, scale, 0, 0, translate,
                            0, 0, scale, 0, translate,
                            0, 0, 0, 1, 0});
                    imageMatrix.postConcat(contrastMatrix);

                } catch (Exception e) {

                }

                ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(imageMatrix);

                bgImage.setColorFilter(colorMatrixColorFilter);
                gifView.setColorFilter(colorMatrixColorFilter);
                textView.getPaint().setColorFilter(colorMatrixColorFilter);


            }


        }


        try {
            GifDrawable gifFromPath = new GifDrawable(file.getAbsolutePath());
            gifView.setImageDrawable(gifFromPath);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
