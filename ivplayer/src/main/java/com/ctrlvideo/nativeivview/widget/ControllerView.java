package com.ctrlvideo.nativeivview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ctrlvideo.ivplayer.PlayerState;
import com.ctrlvideo.ivplayer.R;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;


public class ControllerView extends RelativeLayout {

    private ImageView mIvStart;
    private ImageView mIvStartOrPause;

    private LinearLayout mBottomView;

    private ProgressBar mIvLoading;

    public ControllerView(Context context) {
        this(context, null);
    }

    public ControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        LayoutInflater.from(getContext()).inflate(R.layout.view_player_control, this);

        mBottomView = findViewById(R.id.view_bottom_control);
        mIvStart = findViewById(R.id.iv_start);
        mIvStartOrPause = findViewById(R.id.iv_start_pause);
        mIvLoading = findViewById(R.id.iv_loading);

        mIvStartOrPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (listener != null) {

                    if (status.equals(PlayerState.STATE_READY)) {
                        listener.onPlayOrPause(true);
                    } else if (status.equals(PlayerState.STATE_ONPLAY)) {
                        listener.onPlayOrPause(false);
                    } else if (status.equals(PlayerState.STATE_ONPAUSE)) {
                        listener.onPlayOrPause(true);
                    } else if (status.equals(PlayerState.STATE_END)) {
                        listener.onRestart();
                    }
                }
            }
        });


        mIvStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayOrPause(true);
            }
        });

    }

    private VideoProtocolInfo.PlayerController playerController;

    /**
     * 设计图标准
     */
    private float startImageStandardSize = 82;
    private float startOrPauseImageStandardSize = 35;
    private float standardLine = 667;


    /**
     * 初始化播控配置
     *
     * @param videoStandardSize 以父视图长的一边作为标准适配
     * @param playerController
     */
    public void initController(float videoStandardSize, VideoProtocolInfo.PlayerController playerController) {


        int startImageSize = (int) (videoStandardSize * startImageStandardSize / standardLine);
        ViewGroup.LayoutParams startParams = mIvStart.getLayoutParams();
        startParams.width = startImageSize;
        startParams.height = startImageSize;
        mIvStart.setLayoutParams(startParams);


        ViewGroup.LayoutParams loadingParams = mIvLoading.getLayoutParams();
        loadingParams.width = startImageSize;
        loadingParams.height = startImageSize;
        mIvLoading.setLayoutParams(loadingParams);


        int startOrPauseImageSize = (int) (videoStandardSize * startOrPauseImageStandardSize / standardLine);
        ViewGroup.LayoutParams startOrPausParams = mIvStartOrPause.getLayoutParams();
        startOrPausParams.width = startOrPauseImageSize;
        startOrPausParams.height = startOrPauseImageSize;
        mIvStartOrPause.setLayoutParams(startOrPausParams);


        this.playerController = playerController;


        if (playerController.show_start_btn) {
            mIvStart.setVisibility(VISIBLE);
        } else {
            mIvStart.setVisibility(GONE);
        }
        if (playerController.show_playPause_btn) {
            mIvStartOrPause.setVisibility(VISIBLE);
        } else {
            mIvStartOrPause.setVisibility(GONE);
        }

    }

    private boolean firstReady = true;

    private String status;

    public void setVideoPlayerStatus(String status) {
        this.status = status;

        if (PlayerState.STATE_READY.equals(status)) {

            if (firstReady) {
                if (playerController.show_start_btn) {
                    mIvStart.setVisibility(View.VISIBLE);
                }
                mIvLoading.setVisibility(View.GONE);
                firstReady = false;
            }


            mIvStartOrPause.setImageResource(R.drawable.ic_play_icon);


        } else if (PlayerState.STATE_LOADED.equals(status)) {

            mIvStart.setVisibility(View.GONE);
            mIvLoading.setVisibility(View.VISIBLE);


        } else if (PlayerState.STATE_ONPLAY.equals(status)) {
            mIvStart.setVisibility(View.GONE);
            mIvLoading.setVisibility(View.GONE);

            mIvStartOrPause.setImageResource(R.drawable.ic_stop_icon);

        } else if (PlayerState.STATE_ONPAUSE.equals(status)) {
//            mIvStart.setVisibility(View.GONE);
            mIvLoading.setVisibility(View.GONE);

            mIvStartOrPause.setImageResource(R.drawable.ic_play_icon);

        } else if (PlayerState.STATE_END.equals(status)) {
            mIvStart.setVisibility(View.GONE);
            mIvLoading.setVisibility(View.GONE);

            mIvStartOrPause.setImageResource(R.drawable.ic_play_icon);
        }

    }

    private boolean showable = true;

    /**
     * 当被动暂停时不可显示底部控制条
     *
     * @param showable
     */
    public void setBottomViewShowable(boolean showable) {
        Log.d("ControllerView", "setBottomViewShowable---" + showable);

        this.showable = showable;
        if (!showable) {
//            mBottomView.setVisibility(GONE);
            showBottomView(false);
        }

//        mBottomView.setVisibility(showable ? VISIBLE : GONE);
    }

    private OnControllerListener listener;

    public void setOnControllerListener(OnControllerListener listener) {
        this.listener = listener;
    }

    public interface OnControllerListener {

        void onPlayOrPause(boolean play);

        void onRestart();
    }

    public void onClick() {

        if (mBottomView.getVisibility() == View.VISIBLE) {
            showBottomView(false);
        } else {
            if (showable) {
                showBottomView(true);
            }
        }

    }

    private long hideControllerViewDelay = 5000;

    public void showBottomView(boolean show) {

        if (show) {
            if (mBottomView != null && getHandler() != null) {
                mBottomView.setVisibility(VISIBLE);
                getHandler().removeCallbacks(runnable);
                getHandler().postDelayed(runnable, hideControllerViewDelay);
            }

        } else {
            if (mBottomView != null && getHandler() != null) {
                mBottomView.setVisibility(GONE);
                getHandler().removeCallbacks(runnable);
            }
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("ControllerView", "runnable-----");
            showBottomView(false);
        }
    };
}
