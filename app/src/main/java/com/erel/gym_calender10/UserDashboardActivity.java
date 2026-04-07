package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class UserDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        findViewById(R.id.cardTrackWorkout).setOnClickListener(v -> 
            startActivity(new Intent(this, TrackWorkoutActivity.class)));

        findViewById(R.id.cardMyPlans).setOnClickListener(v -> 
            startActivity(new Intent(this, item_plan.class)));

        findViewById(R.id.cardAnalytics).setOnClickListener(v -> 
            startActivity(new Intent(this, Progress_Graph.class)));

        findViewById(R.id.cardHeatmap).setOnClickListener(v -> 
            startActivity(new Intent(this, ActivityHeatmap.class)));

        findViewById(R.id.cardProfile).setOnClickListener(v -> 
            startActivity(new Intent(this, Users_Profile.class)));

        findViewById(R.id.cardCalendar).setOnClickListener(v -> 
            startActivity(new Intent(this, User_page.class)));
    }
}