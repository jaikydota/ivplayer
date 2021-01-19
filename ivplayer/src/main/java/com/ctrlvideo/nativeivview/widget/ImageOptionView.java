package com.ctrlvideo.nativeivview.widget;

import android.app.Activity;
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctrlvideo.ivplayer.R;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;
import com.ctrlvideo.nativeivview.svgloader.SVG;
import com.ctrlvideo.nativeivview.utils.LogUtils;
import com.ctrlvideo.nativeivview.utils.NativeViewUtils;

import java.io.File;

import static com.ctrlvideo.nativeivview.widget.OptionView.STATUS_CLICK_ENDED;
import static com.ctrlvideo.nativeivview.widget.OptionView.STATUS_CLICK_FAILED;
import static com.ctrlvideo.nativeivview.widget.OptionView.STATUS_CLICK_ON;

public class ImageOptionView extends RelativeLayout {

    private ImageView optionImage;

    private ImageView imageView;
    private TextView textView;
    private View mRootView;


    public ImageOptionView(Context context) {
        this(context, null);
    }

    public ImageOptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        optionImage = new ImageView(getContext());
        optionImage.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(optionImage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        mRootView = LayoutInflater.from(getContext()).inflate(R.layout.view_option, null, false);

        imageView = mRootView.findViewById(R.id.view_image);
        textView = mRootView.findViewById(R.id.view_text);


    }

    public void setData(VideoProtocolInfo.EventOption eventOption, int status) {

        Bitmap bitmap = getBitmap(status, eventOption);
        if (bitmap != null) {
            optionImage.setImageBitmap(bitmap);
        }


        if (eventOption.layout_style != null && eventOption.layout_style.filter != null) {
            VideoProtocolInfo.EventOptionFilter optionFilter = eventOption.layout_style.filter;

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

            optionImage.setColorFilter(new ColorMatrixColorFilter(imageMatrix));

        }


    }


    private Bitmap getBitmap(int status, VideoProtocolInfo.EventOption option) {


        initData(status, option);


        mRootView.measure(View.MeasureSpec.makeMeasureSpec((int) option.getWidth(), MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec((int) option.getHeight(), View.MeasureSpec.EXACTLY));
        mRootView.layout(0, 0, (int) option.getWidth(), (int) option.getHeight());


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
        if (option == null) return;

        VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;
        if (optionStyle != null) {

            mRootView.setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
            //设置旋转角度
//            setRotation(optionStyle.rotate);

            VideoProtocolInfo.EventOptionFilter optionFilter = optionStyle.filter;


            if (optionFilter != null) {
                setAlpha(optionFilter.opacity / 100);
            }
            //设置文字样式

            if (!NativeViewUtils.isNullOrEmptyString(optionStyle.text)) {
                textView.setText(optionStyle.text);
                textView.setTextColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.color)));

//                float font_size = optionStyle.font_size;
//
//                float baseSize;
//
////            option.align_screen
//                boolean align_screen = option.align_screen;
//                if (align_screen) {
//                    baseSize = getTextBaseSize(parentWidth, parentHeight);
//                } else {
//                    baseSize = getTextBaseSize(videoWidth, videoHeight);
//                }
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, option.getTextSize());


                if ("vertical-lr".equals(optionStyle.writing_mode)) {
                    textView.setEms(1);
                } else {
                    textView.setMaxLines(1);
                }
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

                                Picture picture = svg.renderToPicture((int) option.getWidth(), (int) option.getHeight());

                                int width = picture.getWidth();
                                int height = picture.getHeight();


                                LogUtils.d("OptionView", "width=" + width + "----height=" + height);

                                PictureDrawable drawable = new PictureDrawable(picture);

                                imageView.setImageDrawable(drawable);


                            } catch (Exception e) {
//                                e.printStackTrace();
                            }


                        } else {
                            Bitmap backgroundBitmap = BitmapFactory.decodeFile(path);
                            imageView.setImageBitmap(backgroundBitmap);
//                            imageView.setImageBitmap(decodeBitmap(path,getContext()));
                        }

                    }
                }
            }

        }


    }

    private int getScreenHeight(Activity context) {

        return context.getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高
    }

    private int getScreenWidth(Activity context) {

        return context.getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）

    }


    private Bitmap decodeBitmap(String localPath, Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 置为true,仅仅返回图片的分辨率
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(localPath, opts);
        // 得到原图的分辨率;
        int srcHeight = opts.outHeight;
        int srcWidth = opts.outWidth;
        // 得到设备的分辨率
        int screenHeight = getScreenHeight((Activity) context);
        int screenWidth = getScreenWidth((Activity) context);
        // 通过比较得到合适的比例值;
        // 屏幕的 宽320 高 480 ,图片的宽3000 ,高是2262  3000/320=9  2262/480=5,,使用大的比例值
        int scale = 1;
        int sx = srcWidth / screenWidth;
        int sy = srcHeight / screenHeight;
        if (sx >= sy && sx > 1) {
            scale = sx;
        }
        if (sy >= sx && sy > 1) {
            scale = sy;
        }
        // 根据比例值,缩放图片,并加载到内存中;
        // 置为false,让BitmapFactory.decodeFile()返回一个图片对象
        opts.inJustDecodeBounds = false;
        // 可以把图片缩放为原图的1/scale * 1/scale
        opts.inSampleSize = scale;
        // 得到缩放后的bitmap
//        Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/lp.jpg", opts);
        Bitmap bm = BitmapFactory.decodeFile(localPath, opts);
        return bm;
    }


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
