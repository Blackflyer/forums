package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin, btnRegister, btnGoToTrackWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        btnLogin = findViewById(R.id.btnGoLogin);
        btnRegister = findViewById(R.id.btnGoRegister);
        btnGoToTrackWorkout = findViewById(R.id.btnGoToTrackWorkout);

        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnGoToTrackWorkout.setOnClickListener(this);

        View mainView = findViewById(android.R.id.content);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGoRegister) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        } else if (id == R.id.btnGoLogin) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else if (id == R.id.btnGoToTrackWorkout) {
            startActivity(new Intent(MainActivity.this, TrackWorkoutActivity.class));
        }
    }
}
