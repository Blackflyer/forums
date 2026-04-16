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
import android.widget.TextView;
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
    private TextView tvTitle;
    private ExerciseSelectAdapter adapter;
    private String selectedDate;
    private String selectedTime = "12:00"; // default
    private boolean isEditMode = false;
    private String planIdToEdit = null;
    private Plan existingPlan = null;

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        // Get intent data
        isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        planIdToEdit = getIntent().getStringExtra("PLAN_ID");
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null) {
            selectedDate = getIntent().getStringExtra("date");
        }

        initViews();
        
        if (isEditMode && planIdToEdit != null) {
            tvTitle.setText("עריכת תוכנית");
            btnSavePlan.setText("עדכן תוכנית");
            loadExistingPlan();
        } else {
            // New Plan fallback date
            if (selectedDate == null || selectedDate.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                selectedDate = day + "/" + month + "/" + year;
            }
            loadExercisesFromDB();
        }

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
        btnSavePlan.setOnClickListener(v -> saveOrUpdatePlan());
        findViewById(R.id.btnBackToDashboard).setOnClickListener(v -> navigateToDashboard());
    }

    private void loadExistingPlan() {
        DatabaseService.getInstance().getPlanById(planIdToEdit, new DatabaseService.DatabaseCallback<Plan>() {
            @Override
            public void onCompleted(Plan plan) {
                if (plan != null) {
                    existingPlan = plan;
                    etPlanName.setText(plan.getPlanName());
                    selectedDate = plan.getDate();
                    selectedTime = plan.getTime();
                    btnSelectTime.setText("בחר שעת אימון: " + selectedTime);
                    loadExercisesFromDB(); // Then pre-select
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CreatePlanActivity.this, "שגיאה בטעינת התוכנית", Toast.LENGTH_SHORT).show();
                loadExercisesFromDB();
            }
        });
    }

    private void navigateToDashboard() {
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
        int hour = Integer.parseInt(selectedTime.split(":")[0]);
        int minute = Integer.parseInt(selectedTime.split(":")[1]);

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
                
                if (isEditMode && existingPlan != null && existingPlan.getPlan() != null) {
                    adapter.setSelectedExercises(existingPlan.getPlan());
                }
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CreatePlanActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOrUpdatePlan() {
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
        String planId = isEditMode ? planIdToEdit : DatabaseService.getInstance().generatePlanId();

        // Create Plan object
        Plan plan = new Plan(planId, userId, selectedDate, name, "General", selectedTime);
        plan.setPlan(new ArrayList<>(selected));

        btnSavePlan.setEnabled(false);
        btnSavePlan.setText("שומר...");

        DatabaseService.getInstance().createNewPlan(plan, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(CreatePlanActivity.this, isEditMode ? "התוכנית עודכנה בהצלחה!" : "התוכנית נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();

                // Schedule push notifications
                NotificationHelper.scheduleWorkoutNotifications(CreatePlanActivity.this, name, selectedDate, selectedTime);

                finish();
            }

            @Override
            public void onFailed(Exception e) {
                btnSavePlan.setEnabled(true);
                btnSavePlan.setText(isEditMode ? "עדכן תוכנית" : "שמור תוכנית");
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
        tvTitle = findViewById(R.id.tvTitle); // Assuming this ID exists or I should add it
    }
}