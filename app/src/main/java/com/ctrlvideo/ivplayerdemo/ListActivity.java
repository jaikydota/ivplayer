package com.ctrlvideo.ivplayerdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5925315322305659");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028427201742");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028436931666");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5853226980321223");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5925315322305659");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028427201742");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028436931666");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5853226980321223");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5925315322305659");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028427201742");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5159028436931666");
        urlList.add("https://apiivetest.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5853226980321223");



    }

    private LinearLayoutManager layoutManager;

    private void initRecyclerView() {

        layoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        listView.addOnScrollListener(scrollListener);

        adapter = new ListPlayerAdapter(this, urlList);
        listView.setAdapter(adapter);

    }


    private IVPlayer lastPlayer;

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                int firstCompletelyVisibleItemPosition =
                        layoutManager.findFirstCompletelyVisibleItemPosition();

                if (currentPosition != firstCompletelyVisibleItemPosition) {


                    if (lastPlayer != null) {
                        lastPlayer.release();
                    }


                    View currentView = layoutManager.findViewByPosition(firstCompletelyVisibleItemPosition);
                    if (null != currentView) {
                        ListPlayerAdapter.ListPlayerHoldet viewHolder =
                                (ListPlayerAdapter.ListPlayerHoldet) recyclerView.getChildViewHolder(currentView);
                        if (viewHolder != null) {
                            IVPlayer ivPlayer = viewHolder.videoPlayerView;
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

                                    Toast.makeText(ListActivity.this,errorType,Toast.LENGTH_SHORT).show();
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

                            lastPlayer = ivPlayer;
                        }

                    }


                }


                currentPosition = firstCompletelyVisibleItemPosition;


            }
        }
    };

    private int currentPosition = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (lastPlayer != null) {
            lastPlayer.release();
        }
    }
}
