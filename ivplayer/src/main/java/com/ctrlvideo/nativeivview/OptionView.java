package com.ctrlvideo.nativeivview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

        VideoProtocolInfo.EventOptionStyle optionStyle = option.layout_style;

        if (optionStyle != null) {

            VideoProtocolInfo.EventOptionFilter optionFilter = optionStyle.filter;

            // 设置背景
            imageView.setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));

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


                        String path = localFile.getAbsolutePath();
                        if (path.endsWith("svg")) {

                            Drawable drawable = BitmapDrawable.createFromPath(path);

                            imageView.setImageDrawable(drawable);

//                            try {
//                                //获取assets目录下的svg图片的相对路径
////                                String replaceUrl = url.replace("file:///android_asset/", "");
//                                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//
//                                SVG svg = new SVGBuilder().readFromInputStream(new FileInputStream(path)).build();
//
////                                Canvas canvas=new Canvas();
////                                canvas.drawPicture(svg.getPicture());
//                                //github上的svg.createDrawable()没有了,现在只有这个方法
//                                PictureDrawable drawable = svg.getDrawable();
////                                drawable.draw(canvas);
//                                imageView.setImageDrawable(drawable);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }


                        } else {
                            imageView.setImageBitmap(BitmapFactory.decodeFile(localFile.getAbsolutePath()));


//                            VideoProtocolInfo.EventOptionFilter optionFilter = optionStyle.filter;
//
//                            if (optionFilter != null) {
//                                //饱和度
//                                String saturate = optionFilter.saturate;
//                                String contrast = optionFilter.contrast;
//                                String brightness = optionFilter.brightness;
//                                if (saturate != null) {
//
////                                    imageView.setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
//
//
//                                    Bitmap backgroundBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//
//
//                                    Bitmap bitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//                                    bitmap.eraseColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
//
//                                    Canvas canvas = new Canvas(bitmap);
//                                    Paint paint = new Paint();
////                                    paint.setColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
//
//                                    Rect mSrcRect = new Rect(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
//                                    Rect mDestRect = new Rect(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
//
//
//                                    canvas.drawBitmap(backgroundBitmap, mSrcRect, mDestRect, paint);
//
//
//                                    imageView.setImageBitmap(bitmap);
//
//
////                                    imageView.setBackgroundColor(Color.parseColor(NativeViewUtils.transformColor(optionStyle.base_color)));
//
//                                    saturate = saturate.replace("%", "").trim();
//                                    contrast = contrast.replace("%", "").trim();
//                                    brightness = brightness.replace("%", "").trim();
//                                    try {
//                                        float floatSaturate = Float.parseFloat(saturate) / 100;
//                                        float floatContrast = Float.parseFloat(contrast) / 100;
//                                        float floatBrightness = Float.parseFloat(brightness) / 100;
//
//                                        ColorMatrix imageMatrix = new ColorMatrix();
//                                        imageMatrix.postConcat(hueMatrix);
//
//
//                                        ColorMatrix saturationMatrix = new ColorMatrix();
//                                        saturationMatrix.setSaturation(floatSaturate);
//                                        imageMatrix.postConcat(saturationMatrix);
//
//                                        ColorMatrix lumMatrix = new ColorMatrix();
//                                        lumMatrix.setScale(floatBrightness, floatBrightness, floatBrightness, 1);
//
//                                        imageMatrix.postConcat(lumMatrix);
//
//
////                                        float value = (float) ((floatSontrast + 64) / 128.0 );
////                                        float value = floatContrast;
////                                        cm.set(new float[]{
////                                                value, 0, 0, 0, 0,
////                                                0, value, 0, 0, 0,
////                                                0, 0, value, 0, 0, 0,
////                                                0, 0, 1, 0
////                                        });
////                                        cm.setSaturation(floatSaturate); // 设置饱和度:0为纯黑白，饱和度为0；1为饱和度为100，即原图；
//
//                                        ColorMatrixColorFilter mGrayColorFilter = new ColorMatrixColorFilter(imageMatrix);
//
//
//                                        imageView.setColorFilter(mGrayColorFilter);
//
//                                    } catch (Exception e) {
//
//                                    }
//                                }
//                            }

                        }

                    }
                }
            }
        }


    }
}
