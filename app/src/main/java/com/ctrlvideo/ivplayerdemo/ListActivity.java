package com.ctrlvideo.ivplayerdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctrlvideo.ivplayer.IVPlayer;
import com.ctrlvideo.ivplayer.IVPlayerListener;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends FragmentActivity {

    private String TAG = "PlayerActivity";

//    private VideoView mVideoView;
//    private NativeIVView ivView;

    private RecyclerView listView;
    private List<String> urlList;
    private ListPlayerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_player);
        getData();
        listView = findViewById(R.id.recyclerview);

        initRecyclerView();

    }

    private void getData() {

        urlList = new ArrayList<>();
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5165902802815866");
    }

    private LinearLayoutManager layoutManager;

    private void initRecyclerView() {

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        listView.addOnScrollListener(scrollListener);

        adapter = new ListPlayerAdapter(this, urlList);
        listView.setAdapter(adapter);

    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                int firstCompletelyVisibleItemPosition =
                        layoutManager.findFirstCompletelyVisibleItemPosition();

                if (currentPosition != firstCompletelyVisibleItemPosition) {
                    Log.d("LRM", "onScrollStateChanged-----firstCompletelyVisibleItemPosition=" + firstCompletelyVisibleItemPosition);


                    View lastView = layoutManager.findViewByPosition(currentPosition);
                    if (lastView != null) {
                        ListPlayerAdapter.ListPlayerHoldet lastViewHolder =
                                (ListPlayerAdapter.ListPlayerHoldet) recyclerView.getChildViewHolder(lastView);
                        if (lastViewHolder != null) {
                            IVPlayer lastIvPlayer = lastViewHolder.videoPlayerView;
                            Log.d("LRM", "release-----currentPosition=--" + currentPosition);
//                            lastIvPlayer.setBackgroundColor(Color.RED);
                            lastIvPlayer.release();
                        }
                    }


                    View currentView = layoutManager.findViewByPosition(firstCompletelyVisibleItemPosition);
                    if (null != currentView) {


                        ListPlayerAdapter.ListPlayerHoldet viewHolder =
                                (ListPlayerAdapter.ListPlayerHoldet) recyclerView.getChildViewHolder(currentView);
                        if (viewHolder != null) {
                            IVPlayer ivPlayer = viewHolder.videoPlayerView;
                            Log.d("LRM", "onScrollStateChanged-----loadIVideo=--" + firstCompletelyVisibleItemPosition);
                            ivPlayer.loadIVideo(urlList.get(firstCompletelyVisibleItemPosition), new IVPlayerListener() {
                                @Override
                                public void onStateChanged(String state) {

                                }

                                @Override
                                public void onViewClick(String info) {

                                }

                                @Override
                                public void onEventCallback(String result) {

                                }

                                @Override
                                public void onError(String errorType) {

                                }

                                @Override
                                public void onCustomNotify(String msg) {

                                }

                                @Override
                                public void onHrefUrl(String url) {

                                }

                                @Override
                                public void onCallPhone(String phone) {

                                }
                            });
                        }

                    }


                }


//                Log.d("LRM", "onScrollStateChanged----currentPosition=" + currentPosition + "-----firstCompletelyVisibleItemPosition=" + firstCompletelyVisibleItemPosition);

//                View currentView = layoutManager.findViewByPosition(firstCompletelyVisibleItemPosition);
//
//                if (null != currentView) {
//                    if (currentPosition != firstCompletelyVisibleItemPosition) {
//                        //如果当前position 和 上一次固定后的position 相同, 说明是同一个, 只不过滑动了一点点, 然后又释放了
////                        JzvdStd.releaseAllVideos();
//
//                        View lastView = layoutManager.findViewByPosition(currentPosition);
//                        if (lastView != null) {
//                            ListPlayerAdapter.ListPlayerHoldet lastViewHolder =
//                                    (ListPlayerAdapter.ListPlayerHoldet) recyclerView.getChildViewHolder(lastView);
//                            if (lastViewHolder != null) {
//                                IVPlayer lastIvPlayer = lastViewHolder.ivPlayer;
//                                lastIvPlayer.release();
//                            }
//                        }
//
//
//                        ListPlayerAdapter.ListPlayerHoldet viewHolder =
//                                (ListPlayerAdapter.ListPlayerHoldet) recyclerView.getChildViewHolder(currentView);
//                        IVPlayer ivPlayer = viewHolder.ivPlayer;
//                        ivPlayer.loadIVideo(urlList.get(firstCompletelyVisibleItemPosition), new IVPlayerListener() {
//                            @Override
//                            public void onStateChanged(String state) {
//
//                            }
//
//                            @Override
//                            public void onViewClick(String info) {
//
//                            }
//
//                            @Override
//                            public void onEventCallback(String result) {
//
//                            }
//
//                            @Override
//                            public void onError(String errorType) {
//
//                            }
//
//                            @Override
//                            public void onCustomNotify(String msg) {
//
//                            }
//
//                            @Override
//                            public void onHrefUrl(String url) {
//
//                            }
//
//                            @Override
//                            public void onCallPhone(String phone) {
//
//                            }
//                        });
//
//                    }
//
//                }
                currentPosition = firstCompletelyVisibleItemPosition;


            }
        }
    };

    private int currentPosition = 0;

    private boolean isLifeToPause = false;

    @Override
    protected void onResume() {
        super.onResume();


//        if (isLifeToPause) {
//            isLifeToPause = false;
//            //继续播放
//            mVideoView.start();
//            ivView.onPlayerStateChanged(PlayerState.STATE_ONPLAY);
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();

//        if (mVideoView.isPlaying()) {
//            //暂停视频
//            mVideoView.pause();
//            ivView.onPlayerStateChanged(PlayerState.STATE_ONPAUSE);
//            isLifeToPause = true;
//        }
    }
}
