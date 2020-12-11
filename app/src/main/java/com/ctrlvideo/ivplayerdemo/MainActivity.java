package com.ctrlvideo.ivplayerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void demo(View view) {

        startActivity(new Intent(this, DemoActivity.class));
    }

    public void withPlayer(View view) {

        startActivity(new Intent(this, WithPlayerActivity.class));
    }

    public void Player(View view) {

        startActivity(new Intent(this, PlayerActivity.class));
    }
}
