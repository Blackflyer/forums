package com.erel.gym_calender10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvAppName;
    public static final String MyPREFERENCES = "myPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);

        // Add a simple fade-in animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        ivLogo.startAnimation(fadeIn);
        tvAppName.startAnimation(fadeIn);

        // Wait for 3 seconds and then decide where to go
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) return;

                try {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    
                    if (currentUser != null) {
                        // User is logged in, check if admin from SharedPreferences
                        SharedPreferences sharedpreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                        boolean isAdmin = sharedpreferences.getBoolean("isAdmin", false);
                        
                        Intent intent;
                        if (isAdmin) {
                            intent = new Intent(SplashActivity.this, AdminPage.class);
                        } else {
                            intent = new Intent(SplashActivity.this, UserDashboardActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        // No user logged in, go to MainActivity (Welcome screen)
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                } catch (Exception e) {
                    // In case of any Firebase initialization error, fallback to MainActivity
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish(); // Close the splash activity
            }
        }, 3000);
    }
}
