package com.ctrlvideo.nativeivview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctrlvideo.androidsvg.SVG;
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
    private View mRootView;


    private ImageView optionImage;

    public OnOptionViewListener listener;

    public void setOnOptionViewListener(OnOptionViewListener listener) {
        this.listener = listener;
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

        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.view_option, null, false);

        imageView = mRootView.findViewById(R.id.view_image);
        textView = mRootView.findViewById(R.id.view_text);


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

    public void setOption(int status, VideoProtocolInfo.EventOption option) {
        this.status = status;
        post(new Runnable() {
            @Override
            public void run() {


                Bitmap bitmap = getBitmap(status, option);
                if (bitmap != null) {
                    optionImage.setImageBitmap(bitmap);
                }

            }
        });

        if (option.layout_style != null && option.layout_style.filter != null) {
            VideoProtocolInfo.EventOptionFilter optionFilter = option.layout_style.filter;

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
                Log.d("OptionView", "contrast=" + contrast);
//                contrast = 1.1f;
                // -1 --- 1   0 原图
                ColorMatrix contrastMatrix = new ColorMatrix();
                float scale = contrast + 1.f;
                float translate = (-.5f * scale + .5f) * 255.f;
//                Log.d("OptionView", "contrast=" + contrast + "----scale=" + scale + "----translate=" + translate);
                contrastMatrix.set(new float[]{
                        scale, 0, 0, 0, translate,
                        0, scale, 0, 0, translate,
                        0, 0, scale, 0, translate,
                        0, 0, 0, 1, 0});
                imageMatrix.postConcat(contrastMatrix);

            } catch (Exception e) {

            }

            optionImage.setColorFilter(new ColorMatrixColorFilter(imageMatrix));

        }

    }


    private Bitmap getBitmap(int status, VideoProtocolInfo.EventOption option) {


        initData(status, option);


        mRootView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY));
        mRootView.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());


        int w = mRootView.getMeasuredWidth();
        int h = mRootView.getMeasuredHeight();


        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.TRANSPARENT);
        /** 如果不设置canvas画布为白色，则生成透明 */

        mRootView.layout(0, 0, w, h);
        mRootView.draw(c);


        if (option.layout_style != null && option.layout_style.filter != null) {

            VideoProtocolInfo.EventOptionFilter optionFilter = option.layout_style.filter;

            float blur = optionFilter.blur;
            if (blur != 0) {
                return blurBitmap(bmp, blur * 2.5f);
            }
        }
        return bmp;
    }

    private void initData(int status, VideoProtocolInfo.EventOption option) {

        VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
        if (optionStyle != null) {

            mRootView.setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
            //设置旋转角度
            setRotation(optionStyle.rotate);

            VideoProtocolInfo.EventOptionFilter optionFilter = optionStyle.filter;


            if (optionFilter != null) {
                setAlpha(optionFilter.opacity / 100);
            }
            //设置文字样式
            textView.setText(optionStyle.text);
            textView.setTextColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.color)));


            if ("vertical-lr".equals(optionStyle.writing_mode)) {
                textView.setEms(1);
            } else {
                textView.setMaxLines(1);
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
                    File localFile = new File(NativeViewUtils.getDowmloadFilePath(getContext()), NativeViewUtils.getFileName(defaultImage));
                    if (localFile.exists()) {

                        String path = localFile.getAbsolutePath();
                        if (path.endsWith("svg")) {

                            try {
                                SVG svg = SVG.getFromFile(getContext(), path);
//                                imageView.setImageDrawable(new PictureDrawable(svg.renderToPicture()));

//                                post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.d("OptionView", "getMeasuredWidth=" + getMeasuredWidth() + "----getMeasuredHeight=" + getMeasuredHeight());
//                                    }
//                                });

//                                Log.d("OptionView", "getMeasuredWidth=" + getMeasuredWidth() + "----getMeasuredHeight=" + getMeasuredHeight());

                                Picture picture = svg.renderToPicture(getMeasuredWidth(),getMeasuredHeight());

                                int width = picture.getWidth();
                                int height =picture.getHeight();


                                Log.d("OptionView", "width=" + width + "----height=" + height);

                                PictureDrawable drawable = new PictureDrawable(picture);

                                imageView.setImageDrawable(drawable);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        } else {

                            Bitmap backgroundBitmap = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(backgroundBitmap);

                        }
                    }
                }
            }

        }


    }

    /**
     * 模糊图片
     *
     * @param bitmap
     * @param radius
     * @return
     */
    private Bitmap blurBitmap(Bitmap bitmap, float radius) {

        // Let's create an empty bitmap with the same size of the bitmap we want
        // to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(getContext());//RenderScript是Android在API 11之后增加的

        // Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Create the Allocations (in/out) with the Renderscript and the in/out
        // bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        // Set the radius of the blur
        blurScript.setRadius(radius);

        // Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        // Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        // recycle the original bitmap
        bitmap.recycle();

        // After finishing everything, we destroy the Renderscript.
        rs.destroy();
        return outBitmap;
    }


}
