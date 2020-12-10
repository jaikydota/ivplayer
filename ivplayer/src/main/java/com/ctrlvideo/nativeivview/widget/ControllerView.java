package com.ctrlvideo.nativeivview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ctrlvideo.ivplayer.PlayerState;
import com.ctrlvideo.ivplayer.R;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;

public class ControllerView extends RelativeLayout {

    private ImageView mIvStart;
    private ImageView mIvStartOrPause;

    private LinearLayout mBottomView;

    private ImageView mIvLoading;

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

                    if (status.equals(PlayerState.STATE_ONPLAY)) {
                        listener.onPlayOrPause(false);
                    } else if (status.equals(PlayerState.STATE_ONPAUSE)) {
                        listener.onPlayOrPause(true);
                    } else if (status.equals(PlayerState.STATE_END)) {
                        listener.onRestart();
                    }
                }
            }
        });

    }

    private VideoProtocolInfo.PlayerController playerController;


    /**
     * 初始化播控配置
     *
     * @param playerController
     */
    public void initController(VideoProtocolInfo.PlayerController playerController) {

        this.playerController = playerController;


        if (playerController.show_start_btn) {
            mIvStart.setVisibility(VISIBLE);
        } else {
            mIvStart.setVisibility(GONE);
        }

    }

    private String status;

    public void setVideoPlayerStatus(String status) {
        this.status = status;

        if (status.equals(PlayerState.STATE_LOADED)) {

            mIvStart.setVisibility(GONE);
            mIvLoading.setVisibility(VISIBLE);

        } else if (status.equals(PlayerState.STATE_ONPLAY)) {

            mIvLoading.setVisibility(GONE);
            mIvStart.setVisibility(GONE);
            if (playerController.show_playPause_btn) {
                mIvStartOrPause.setVisibility(VISIBLE);
                mIvStartOrPause.setImageResource(R.drawable.ic_stop_icon);
            }

        } else if (status.equals(PlayerState.STATE_ONPAUSE)) {
            mIvLoading.setVisibility(GONE);
            mIvStart.setVisibility(GONE);
            if (playerController.show_playPause_btn) {
                mIvStartOrPause.setVisibility(VISIBLE);
                mIvStartOrPause.setImageResource(R.drawable.ic_play_icon);
            }

        } else if (status.equals(PlayerState.STATE_END)) {
            mIvLoading.setVisibility(GONE);
            mIvStart.setVisibility(GONE);
            if (playerController.show_playPause_btn) {
                mIvStartOrPause.setVisibility(VISIBLE);
                mIvStartOrPause.setImageResource(R.drawable.ic_play_icon);

            }

        }


//        if (playing) {
//            mIvStart.setVisibility(View.GONE);
//
//            mIvStartOrPause.setImageResource(R.drawable.ic_stop_icon);
//
//        } else {
//            mIvStartOrPause.setImageResource(R.drawable.ic_play_icon);
//        }
    }

    /**
     * 当被动暂停时不可显示底部控制条
     * @param showable
     */
    public void setBottomViewShowable(boolean showable) {
        Log.d("ControllerView", "setBottomViewShowable---" + showable);

        mBottomView.setVisibility(showable ? VISIBLE : GONE);
    }

    private OnControllerListener listener;

    public void setOnControllerListener(OnControllerListener listener) {
        this.listener = listener;
    }

    public interface OnControllerListener {

        void onPlayOrPause(boolean play);

        void onRestart();
    }
}
