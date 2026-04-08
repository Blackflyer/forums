package com.erel.gym_calender10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.ExerciseSelectAdapter;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.DatabaseService;
import com.erel.gym_calender10.services.NotificationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreatePlanActivity extends AppCompatActivity {
    private RecyclerView rvExercises;
    private EditText etSearch, etPlanName;
    private MaterialButton btnSavePlan, btnSelectTime;
    private ExerciseSelectAdapter adapter;
    private String selectedDate;
    private String selectedTime = "12:00"; // default

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        // 1. Get the date
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null) {
            selectedDate = getIntent().getStringExtra("date");
        }

        // 2. Fallback
        if (selectedDate == null || selectedDate.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            selectedDate = day + "/" + month + "/" + year;
        }

        initViews();
        loadExercisesFromDB();
        checkNotificationPermission();

        // Search listener
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSavePlan.setOnClickListener(v -> saveNewPlan());
        findViewById(R.id.btnBackToDashboard).setOnClickListener(v -> navigateToDashboard());
    }

    private void navigateToDashboard() {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean("isAdmin", false);
        Intent intent;
        if (isAdmin) {
            intent = new Intent(this, AdminPage.class);
        } else {
            intent = new Intent(this, UserDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    btnSelectTime.setText("בחר שעת אימון: " + selectedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadExercisesFromDB() {
        DatabaseService.getInstance().getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            @Override
            public void onCompleted(List<Exercise> exercises) {
                adapter = new ExerciseSelectAdapter(exercises);
                rvExercises.setAdapter(adapter);
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CreatePlanActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewPlan() {
        String name = etPlanName.getText().toString().trim();

        if (name.isEmpty()) {
            etPlanName.setError("חובה להזין שם לתוכנית");
            return;
        }

        if (adapter == null || adapter.getSelectedExercises().isEmpty()) {
            Toast.makeText(this, "אנא בחר לפחות תרגיל אחד מהרשימה", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Exercise> selected = adapter.getSelectedExercises();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String planId = DatabaseService.getInstance().generatePlanId();

        // Create Plan object with time
        Plan newPlan = new Plan(planId, userId, selectedDate, name, "General", selectedTime);
        newPlan.setPlan(new ArrayList<>(selected));

        btnSavePlan.setEnabled(false);
        btnSavePlan.setText("שומר...");

        DatabaseService.getInstance().createNewPlan(newPlan, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(CreatePlanActivity.this, "התוכנית נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();

                // Schedule push notifications for the upcoming workout
                NotificationHelper.scheduleWorkoutNotifications(CreatePlanActivity.this, name, selectedDate, selectedTime);

                // Return to Plan_day
                Intent intent = new Intent(CreatePlanActivity.this, Plan_day.class);
                intent.putExtra("SELECTED_DATE", selectedDate);
                startActivity(intent);

                finish();
            }

            @Override
            public void onFailed(Exception e) {
                btnSavePlan.setEnabled(true);
                btnSavePlan.setText("שמור תוכנית");
                Toast.makeText(CreatePlanActivity.this, "שגיאה בשמירה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearchExercise);
        etPlanName = findViewById(R.id.etPlanName);
        btnSavePlan = findViewById(R.id.btnSavePlan);
        btnSelectTime = findViewById(R.id.btnSelectTime);
    }
}
