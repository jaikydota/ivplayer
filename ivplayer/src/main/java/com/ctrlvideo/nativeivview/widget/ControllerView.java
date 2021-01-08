package com.ctrlvideo.nativeivview.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlvideo.ivplayer.PlayerState;
import com.ctrlvideo.ivplayer.R;
import com.ctrlvideo.nativeivview.model.VideoProtocolInfo;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.Adapter;
import static androidx.recyclerview.widget.RecyclerView.ViewHolder;


public class ControllerView extends RelativeLayout {

    private ImageView mIvStart;
    private ImageView mIvStartOrPause;

    private LinearLayout mBottomView;

    private ProgressBar mIvLoading;

    private TextView mTvRatio;
    private RecyclerView mRatioList;

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
        mTvRatio = findViewById(R.id.tv_ratio);
        mRatioList = findViewById(R.id.ratiolist);

        initRatioList();

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

        mTvRatio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                mRatioList.setVisibility((mRatioList.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE));
            }
        });

    }

    List<Float> ratioValues;

    private void initRatioList() {


        ratioValues = new ArrayList<>();

        ratioValues.add(2.0f);
        ratioValues.add(1.5f);
        ratioValues.add(1.25f);
        ratioValues.add(1.0f);
        ratioValues.add(0.5f);

        mTvRatio.setText(ratioValues.get(ratioIndex) + "X");

        mRatioList.setLayoutManager(new LinearLayoutManager(getContext()));


        ratioAdapter = new RatioAdapter();

        mRatioList.setAdapter(ratioAdapter);
    }

    RatioAdapter ratioAdapter;

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class RatioAdapter extends Adapter {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0, dip2px(6), 0, dip2px(6));
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);

            return new RatioHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TextView textView = (TextView) holder.itemView;

            float value = ratioValues.get(position);
            textView.setText(value + "X");
            if (position == ratioIndex) {
                textView.setTextColor(Color.BLUE);
            } else {
                textView.setTextColor(Color.WHITE);
            }


            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ratioIndex = position;
                    ratioAdapter.notifyDataSetChanged();
                    mTvRatio.setText(value + "X");
                    mRatioList.setVisibility(GONE);
                    if (listener != null) {
                        listener.onRatioChange(value);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return ratioValues.size();
        }
    }

    int ratioIndex = 3;

    private class RatioHolder extends ViewHolder {

        public RatioHolder(@NonNull View itemView) {
            super(itemView);
        }
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

        void onRatioChange(float ratio);
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
                mRatioList.setVisibility(GONE);
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
