package com.example.nimeneze.cart;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class animationActivity extends AppCompatActivity {

    private AnimationDrawable anim;
    int count=0;
    ImageView img;
    private static int TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        img = (ImageView)findViewById(R.id.imageView1);
        Animation();
    }

    private void Animation() {

        anim = (AnimationDrawable) img.getDrawable();
        img.post(run);
    }

    Runnable run = new Runnable() {
        @Override
        public void run() {
            anim.start();

            final int intervalTime = 6000; // 10 sec
            Handler handler = new Handler();
            handler.postDelayed(new Runnable()  {
                @Override
                public void run() {
                    android.view.animation.Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                    img.startAnimation(animation1);
                    Intent i = new Intent(animationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, intervalTime);

        }

    };




}
