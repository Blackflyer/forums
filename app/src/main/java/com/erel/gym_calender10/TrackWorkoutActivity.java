package com.erel.gym_calender10;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackWorkoutActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompletePlan;
    private AutoCompleteTextView autoCompleteExercise;

    private List<Plan> todayPlans = new ArrayList<>();
    private List<Exercise> allExercises = new ArrayList<>();

    private Plan selectedPlan = null;
    private Exercise selectedExercise = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_workout);

        autoCompletePlan = findViewById(R.id.autoCompletePlan);
        autoCompleteExercise = findViewById(R.id.autoCompleteExercise);

        loadTodayPlans();
        loadExercises();
    }

    // 1. טעינת התוכניות של המשתמש להיום
    private void loadTodayPlans() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // יצירת התאריך של היום בפורמט ששמרת במסד הנתונים (לדוגמה: dd/MM/yyyy)
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        DatabaseService.getInstance().getPlansByDate(userId, todayDate, new DatabaseService.DatabaseCallback<List<Plan>>() {
            @Override
            public void onCompleted(List<Plan> plans) {
                todayPlans = plans;
                List<String> planNames = new ArrayList<>();
                for (Plan plan : plans) {
                    // נניח שיש מתודה getPlanName() במודל Plan, אם אין, יש להשתמש בשדה מתאים
                    planNames.add(plan.getPlanId()); // זמני - החלף לשם התוכנית האמיתי
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        TrackWorkoutActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        planNames
                );
                autoCompletePlan.setAdapter(adapter);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(TrackWorkoutActivity.this, "שגיאה בטעינת אימונים", Toast.LENGTH_SHORT).show();
            }
        });

        // שמירת הבחירה
        autoCompletePlan.setOnItemClickListener((parent, view, position, id) -> {
            selectedPlan = todayPlans.get(position);
        });
    }

    // 2. טעינת רשימת התרגילים
    private void loadExercises() {
        DatabaseService.getInstance().getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            @Override
            public void onCompleted(List<Exercise> exercises) {
                allExercises = exercises;
                List<String> exerciseNames = new ArrayList<>();
                for (Exercise ex : exercises) {
                    exerciseNames.add(ex.getName()); // שליפת שם התרגיל לפי המודל
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        TrackWorkoutActivity.this,
                        android.R.layout.simple_dropdown_item_1line,
                        exerciseNames
                );
                autoCompleteExercise.setAdapter(adapter);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(TrackWorkoutActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });

        // שמירת התרגיל הנבחר על ידי המשתמש
        autoCompleteExercise.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (Exercise ex : allExercises) {
                if (ex.getName().equals(selectedName)) {
                    selectedExercise = ex;
                    break;
                }
            }
        });
    }
}