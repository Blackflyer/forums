package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AdminPage extends AppCompatActivity implements View.OnClickListener {

    // הגדרת כל הכפתורים
    Button btnAddExercise, btnAddAdmin, btnUsersList, btnGoCalendar, btnGoProfile, btnAdminAnalytics, btnAdminHeatmap, btnAdminMyPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);

        // קישור הכפתורים ל-XML
        btnAddExercise = findViewById(R.id.btnGoAddExercise);
        btnUsersList = findViewById(R.id.btnGoUsersList);
        btnAddAdmin = findViewById(R.id.btnGoAddAdmin);
        btnGoCalendar = findViewById(R.id.btnGoCalendar);
        btnGoProfile = findViewById(R.id.btnGoProfile);
        btnAdminAnalytics = findViewById(R.id.btnAdminAnalytics);
        btnAdminHeatmap = findViewById(R.id.btnAdminHeatmap);
        btnAdminMyPlans = findViewById(R.id.btnAdminMyPlans);

        // הגדרת מאזיני לחיצה
        btnAddExercise.setOnClickListener(this);
        btnUsersList.setOnClickListener(this);
        btnAddAdmin.setOnClickListener(this);
        btnGoCalendar.setOnClickListener(this);
        btnGoProfile.setOnClickListener(this);
        btnAdminAnalytics.setOnClickListener(this);
        btnAdminHeatmap.setOnClickListener(this);
        btnAdminMyPlans.setOnClickListener(this);
        // בתוך פונקציית onCreate:
        Button btnAdminTrackWorkout = findViewById(R.id.btnAdminTrackWorkout);
        btnAdminTrackWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, TrackWorkoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        // שימוש ב-if/else if כדי לנווט למסכים המתאימים
        if (v.getId() == btnAddExercise.getId()) {
            Intent intent = new Intent(AdminPage.this, AddExercise.class);
            startActivity(intent);
        }
        else if (v.getId() == btnAddAdmin.getId()) {
            Intent intent = new Intent(AdminPage.this, AddAdmin.class);
            startActivity(intent);
        }
        else if (v.getId() == btnUsersList.getId()) {
            Intent intent = new Intent(AdminPage.this, Users_list.class);
            startActivity(intent);
        }
        else if (v.getId() == btnGoCalendar.getId()) {
            Intent intent = new Intent(AdminPage.this, User_page.class);
            startActivity(intent);
        }
        else if (v.getId() == btnGoProfile.getId()) {
            Intent intent = new Intent(AdminPage.this,Users_Profile.class);
            startActivity(intent);
        }
        else if (v.getId() == btnAdminAnalytics.getId()) {
            Intent intent = new Intent(AdminPage.this, Progress_Graph.class);
            startActivity(intent);
        }
        else if (v.getId() == btnAdminHeatmap.getId()) {
            Intent intent = new Intent(AdminPage.this, ActivityHeatmap.class);
            startActivity(intent);
        }
        else if (v.getId() == btnAdminMyPlans.getId()) {
            Intent intent = new Intent(AdminPage.this, item_plan.class);
            startActivity(intent);
        }
    }
}