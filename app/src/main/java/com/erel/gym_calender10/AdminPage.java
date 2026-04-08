package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminPage extends AppCompatActivity {

    private ImageButton btnEditProfile;
    private CalendarView calendarView;
    private MaterialButton btnGoUsersList, btnGoAddExercise, btnGoAddAdmin, btnGoDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        initViews();
        setupCalendar();
        setupClickListeners();
    }

    private void initViews() {
        btnEditProfile = findViewById(R.id.btnEditProfile);
        calendarView = findViewById(R.id.calendarView);
        btnGoUsersList = findViewById(R.id.btnGoUsersList);
        btnGoAddExercise = findViewById(R.id.btnGoAddExercise);
        btnGoAddAdmin = findViewById(R.id.btnGoAddAdmin);
        btnGoDashboard = findViewById(R.id.btn_go_dashboard);
    }

    private void setupCalendar() {
        // Add today as an event
        List<EventDay> events = new ArrayList<>();
        events.add(new EventDay(Calendar.getInstance(), R.drawable.ic_launcher_background));
        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
            int month = clickedDayCalendar.get(Calendar.MONTH) + 1; // Month is 0-indexed
            int year = clickedDayCalendar.get(Calendar.YEAR);

            String date = day + "/" + month + "/" + year;

            // Navigate to CreatePlanActivity for the selected day
            Intent intent = new Intent(AdminPage.this, CreatePlanActivity.class);
            intent.putExtra("date", date);
            startActivity(intent);
        });
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, EditProfileActivity.class));
        });

        btnGoUsersList.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, Users_list.class));
        });

        btnGoAddExercise.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, AddExercise.class));
        });

        btnGoAddAdmin.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, AddAdmin.class));
        });

        btnGoDashboard.setOnClickListener(v -> {
            // Reusing the UserDashboardActivity for the admin as well, just like User_page
            startActivity(new Intent(AdminPage.this, UserDashboardActivity.class));
        });
    }
}
