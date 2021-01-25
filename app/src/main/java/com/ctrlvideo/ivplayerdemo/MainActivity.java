package com.ctrlvideo.ivplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pid = getIntent().getStringExtra("pid");


//        List<String> urls = new ArrayList<>();
//        urls.add("https://apiive.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5166003853478754");
//        urls.add("https://apiive.ctrlvideo.com/player/ajax/get_ivideo_info/?project_id=5166459489024370");
//        NativeIVSDK.getInstance(this).preloadMediaResource(urls);


    }

//    public void demo(View view) {
//
//        startActivity(new Intent(this, DemoActivity.class));
//    }

    public void fullscreen(View view) {

        Intent intent = new Intent(this, ExoplayerActivity.class);

        intent.putExtra("pid", pid);
        intent.putExtra("fullscreen", true);

        startActivity(intent);
    }

    public void halffullscreen(View view) {

        Intent intent = new Intent(this, ExoplayerActivity.class);
        intent.putExtra("pid", pid);
        intent.putExtra("fullscreen", false);
        startActivity(intent);
    }

    public void mediaplayer(View view) {


        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("pid", pid);
        startActivity(intent);
    }

//    public void list(View view) {
//
//        startActivity(new Intent(this, ListActivity.class));
//    }
}
