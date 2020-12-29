package com.ctrlvideo.nativeivview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class GifView extends View {

    private Movie movie;

    private boolean isPaused = false;

    private long movieStart = 0;

    private int currentAnimationTime = 0;

    private int DEFAULT_MOVIE_VIEW_DURATION = 1000;

    private boolean isVisible = true;

    public GifView(Context context) {
        this(context, null);
    }

    public GifView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint paint;

    private void init() {
        paint = new Paint();
    }


    public void setGifPath(String path, float saturate,float contrast) {





        try {
            long time=System.currentTimeMillis();
            FileInputStream inputStream = new FileInputStream(path);
            movie = Movie.decodeStream(inputStream);

            Log.d("GifView","耗时---"+(System.currentTimeMillis()-time));
            requestLayout();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        ColorMatrix imageMatrix = new ColorMatrix();


        ColorMatrix saturateMatrix = new ColorMatrix();
        saturateMatrix.setSaturation(saturate);
        imageMatrix.postConcat(saturateMatrix);


        ColorMatrix contrastMatrix = new ColorMatrix();
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        contrastMatrix.set(new float[]{
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0});
        imageMatrix.postConcat(contrastMatrix);


        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));



    }

    public void play() {
        if (this.isPaused) {
            this.isPaused = false;
            /**
             * Calculate new movie start time, so that it resumes from the same
             * frame.
             */
            movieStart = android.os.SystemClock.uptimeMillis() - currentAnimationTime;
            invalidate();
        }
    }

    public void pause() {
        if (!this.isPaused) {
            this.isPaused = true;
            invalidate();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (movie != null) {
            if (!isPaused) {
                updateAnimationTime();
                drawMovieFrame(canvas);
                invalidateView();
            } else {
                drawMovieFrame(canvas);
            }
        }
    }

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (movieStart == 0L) {
            movieStart = now;
        }

        if (movie != null) {
            int duration = movie.duration();
            if (duration == 0) {
                duration = DEFAULT_MOVIE_VIEW_DURATION;
            }
            currentAnimationTime = (int) ((now - movieStart) % duration);
        }
    }

    @SuppressLint("NewApi")
    private void invalidateView() {
        if (isVisible) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                postInvalidateOnAnimation();
            } else {
                invalidate();
            }
        }
    }


    private void drawMovieFrame(Canvas canvas) {
        if (movie != null) {
            movie.setTime(currentAnimationTime);
            canvas.save();

            Rect targetRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());

            paint.setColor(Color.RED);
            canvas.drawRect(targetRect, paint);

            canvas.scale(((float) getMeasuredWidth()) / ((float) movie.width()), ((float) getMeasuredHeight()) / ((float) movie.height()));
            movie.draw(canvas, 0f, 0f, paint);


            canvas.restore();

        }


    }


    @Override
    public void onScreenStateChanged(int screenState) {
        super.onScreenStateChanged(screenState);
        isVisible = screenState == View.SCREEN_STATE_ON;
        invalidateView();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        isVisible = visibility == View.VISIBLE;
        invalidateView();
    }

}
