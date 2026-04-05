package com.erel.gym_calender10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.WorkoutExerciseAdapter;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.module.ProgressRecord;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TrackWorkoutActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompletePlan;
    private RecyclerView rvWorkoutExercises;
    private Button btnSaveWorkout;

    private List<Plan> allPlans = new ArrayList<>();
    private Plan selectedPlan = null;
    private WorkoutExerciseAdapter workoutAdapter;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_workout);

        databaseService = DatabaseService.getInstance();

        autoCompletePlan = findViewById(R.id.autoCompletePlan);
        rvWorkoutExercises = findViewById(R.id.rvWorkoutExercises);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);

        rvWorkoutExercises.setLayoutManager(new LinearLayoutManager(this));

        loadAllPlans();

        autoCompletePlan.setOnItemClickListener((parent, view, position, id) -> {
            selectedPlan = allPlans.get(position);
            displayPlanExercises(selectedPlan);
            btnSaveWorkout.setVisibility(View.VISIBLE);
        });

        btnSaveWorkout.setOnClickListener(v -> saveWorkoutProgress());
    }

    private void loadAllPlans() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null && user.getMaarachedPlans() != null && user.getMaarachedPlans().getPlanArray() != null) {
                    allPlans = user.getMaarachedPlans().getPlanArray();
                    List<String> planNames = new ArrayList<>();
                    for (Plan plan : allPlans) {
                        String name = plan.getPlanName();
                        if (name == null || name.isEmpty()) {
                            name = "תוכנית ללא שם (" + plan.getDate() + ")";
                        }
                        planNames.add(name);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            TrackWorkoutActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            planNames
                    );
                    autoCompletePlan.setAdapter(adapter);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("TrackWorkout", "Failed to load plans", e);
                Toast.makeText(TrackWorkoutActivity.this, "שגיאה בטעינת תוכניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPlanExercises(Plan plan) {
        if (plan.getPlan() != null) {
            workoutAdapter = new WorkoutExerciseAdapter(plan.getPlan());
            rvWorkoutExercises.setAdapter(workoutAdapter);
        }
    }

    private void saveWorkoutProgress() {
        if (selectedPlan == null || workoutAdapter == null) return;

        String userId = FirebaseAuth.getInstance().getUid();
        Map<String, Float> weights = workoutAdapter.getWeights();
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (weights.isEmpty()) {
            Toast.makeText(this, "נא להזין לפחות משקל אחד", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalToSave = weights.size();
        final int[] savedCount = {0};
        final boolean[] failed = {false};

        for (Map.Entry<String, Float> entry : weights.entrySet()) {
            String exerciseId = entry.getKey();
            float weight = entry.getValue();

            ProgressRecord record = new ProgressRecord(todayDate, weight, 0); // Reps default to 0 for now

            databaseService.saveExerciseProgress(userId, exerciseId, record, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void v) {
                    savedCount[0]++;
                    if (savedCount[0] == totalToSave && !failed[0]) {
                        Toast.makeText(TrackWorkoutActivity.this, "האימון נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    if (!failed[0]) {
                        failed[0] = true;
                        Toast.makeText(TrackWorkoutActivity.this, "שגיאה בשמירת חלק מהנתונים", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}