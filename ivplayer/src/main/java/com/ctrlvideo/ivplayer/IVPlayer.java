package com.ctrlvideo.ivplayer;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleObserver;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;


/**
 * Author by Jaiky, Date on 2020/4/8.
 */
@SuppressLint("NewApi")
public class IVPlayer extends RelativeLayout implements LifecycleObserver {

    protected String TAG = "IVSDKView";

    public IVPlayer(Context context) {
        super(context);
        initView(context);
    }

    public IVPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public IVPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        if (isInEditMode()){
            return;
        }
        RelativeLayout inflate = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.view_player, this, true);
//        webView = findViewById(R.id.webvContainer);
//        webView.setBackgroundColor(0);
//        webView.getBackground().setAlpha(0);
//        webView.setVisibility(View.INVISIBLE);
    };




}
