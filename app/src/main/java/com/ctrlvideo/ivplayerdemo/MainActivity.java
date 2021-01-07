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
    }

//    public void demo(View view) {
//
//        startActivity(new Intent(this, DemoActivity.class));
//    }

    public void fullscreen(View view) {

        Intent intent = new Intent(this, ExoplayerActivity.class);

        intent.putExtra("pid",pid);
        intent.putExtra("fullscreen",true);

        startActivity(intent);
    }

    public void halffullscreen(View view) {

        Intent intent = new Intent(this, ExoplayerActivity.class);
        intent.putExtra("pid",pid);
        intent.putExtra("fullscreen",false);
        startActivity(intent);
    }

    public void mediaplayer(View view) {

        startActivity(new Intent(this, PlayerActivity.class));
    }

//    public void list(View view) {
//
//        startActivity(new Intent(this, ListActivity.class));
//    }
}
