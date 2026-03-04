package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    public ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        myImageView = findViewById(R.id.imageView);

        Thread mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                }

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        };

        mSplashThread.start();
    }
}
