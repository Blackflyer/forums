package com.erel.gym_calender10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.WorkoutExerciseAdapter;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.module.ProgressRecord;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.AchievementService;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;



public class TrackWorkoutActivity extends AppCompatActivity implements WorkoutExerciseAdapter.OnExerciseClickListener, SetEntryBottomSheetFragment.OnEntryConfirmedListener {

    private AutoCompleteTextView autoCompletePlan;
    private RecyclerView rvWorkoutExercises;
    private Button btnSaveWorkout;
    private TextView tvTimer;
    private View cvTimer;


    private List<Plan> allPlans = new ArrayList<>();
    private Plan selectedPlan = null;
    private WorkoutExerciseAdapter workoutAdapter;
    private DatabaseService databaseService;
    private CountDownTimer countDownTimer;
    private Map<String, Float> personalBests = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_workout);

        databaseService = DatabaseService.getInstance();

        autoCompletePlan = findViewById(R.id.autoCompletePlan);
        rvWorkoutExercises = findViewById(R.id.rvWorkoutExercises);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        tvTimer = findViewById(R.id.tvTimer);
        cvTimer = findViewById(R.id.cvTimer);
        findViewById(R.id.btnBackToDashboard).setOnClickListener(v -> navigateToDashboard());


        rvWorkoutExercises.setLayoutManager(new LinearLayoutManager(this));

        loadAllPlans();

        autoCompletePlan.setOnClickListener(v -> autoCompletePlan.showDropDown());
        autoCompletePlan.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) autoCompletePlan.showDropDown();
        });

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
                if (user != null && user.getMaarachedPlans() != null && user.getMaarachedPlans().getPlanArray() != null && !user.getMaarachedPlans().getPlanArray().isEmpty()) {
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
                    autoCompletePlan.setThreshold(0); // Show results from the first character or focus

                    // Auto-select plan if PLAN_ID was passed
                    String targetPlanId = getIntent().getStringExtra("PLAN_ID");
                    if (targetPlanId != null) {
                        for (int i = 0; i < allPlans.size(); i++) {
                            if (targetPlanId.equals(allPlans.get(i).getPlanId())) {
                                selectedPlan = allPlans.get(i);
                                autoCompletePlan.setText(planNames.get(i), false);
                                displayPlanExercises(selectedPlan);
                                btnSaveWorkout.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(TrackWorkoutActivity.this, "לא נמצאו תוכניות אימון. אנא צור תוכנית חדשה.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("TrackWorkout", "Failed to load plans", e);
                Toast.makeText(TrackWorkoutActivity.this, "שגיאה בחיבור לשרת", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayPlanExercises(Plan plan) {
        if (plan.getPlan() != null) {
            workoutAdapter = new WorkoutExerciseAdapter(plan.getPlan(), this);
            rvWorkoutExercises.setAdapter(workoutAdapter);
            loadPreviousData(plan);
        }
    }

    private void loadPreviousData(Plan plan) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null || plan.getPlan() == null) return;

        Map<String, Float> lastWeights = new HashMap<>();
        Map<String, Integer> lastReps = new HashMap<>();
        final int[] fetchedCount = {0};
        int totalExercises = plan.getPlan().size();

        for (Exercise ex : plan.getPlan()) {
            databaseService.getExerciseProgress(userId, ex.getId(), new DatabaseService.DatabaseCallback<List<ProgressRecord>>() {
                @Override
                public void onCompleted(List<ProgressRecord> records) {
                    float maxWeight = 0;
                    if (records != null && !records.isEmpty()) {
                        ProgressRecord last = records.get(records.size() - 1);
                        lastWeights.put(ex.getId(), last.getWeight());
                        lastReps.put(ex.getId(), last.getReps());

                        for (ProgressRecord r : records) {
                            if (r.getWeight() > maxWeight) maxWeight = r.getWeight();
                        }
                    }
                    personalBests.put(ex.getId(), maxWeight);
                    
                    fetchedCount[0]++;
                    if (fetchedCount[0] == totalExercises) {
                        workoutAdapter.setInitialData(lastWeights, lastReps);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    fetchedCount[0]++;
                    if (fetchedCount[0] == totalExercises) {
                        workoutAdapter.setInitialData(lastWeights, lastReps);
                    }
                }
            });
        }
    }

    @Override
    public void onExerciseClick(Exercise exercise, float currentWeight, int currentReps) {
        SetEntryBottomSheetFragment bottomSheet = SetEntryBottomSheetFragment.newInstance(exercise.getId(), currentWeight, currentReps);
        bottomSheet.setOnEntryConfirmedListener(this);
        bottomSheet.show(getSupportFragmentManager(), "SetEntryBottomSheet");
    }

    @Override
    public void onEntryConfirmed(String exerciseId, float weight, int reps) {
        workoutAdapter.updateExerciseData(exerciseId, weight, reps);
        
        // PR Celebration
        Float best = personalBests.get(exerciseId);
        if (weight > 0 && (best == null || weight > best)) {

            personalBests.put(exerciseId, weight); // Update local PB
        }

        // Auto Rest Timer
        startRestTimer(60); // 60 seconds default
    }



    private void startRestTimer(int seconds) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        cvTimer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", sec / 60, sec % 60));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(TrackWorkoutActivity.this, "המנוחה נגמרה! חזרה לעבודה", Toast.LENGTH_SHORT).show();
                // Optionally hide after some time
            }
        }.start();
    }

    private void saveWorkoutProgress() {
        if (selectedPlan == null || workoutAdapter == null) return;

        String userId = FirebaseAuth.getInstance().getUid();
        Map<String, Float> weights = workoutAdapter.getWeights();
        Map<String, Integer> reps = workoutAdapter.getReps();
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        if (weights.isEmpty()) {
            Toast.makeText(this, "נא להזין לפחות משקל אחד", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalToSave = weights.size();
        final int[] savedCount = {0};
        final boolean[] failed = {false};
        final float[] maxWeightEntered = {0};

        for (Map.Entry<String, Float> entry : weights.entrySet()) {
            String exerciseId = entry.getKey();
            float weight = entry.getValue();
            if (weight > maxWeightEntered[0]) maxWeightEntered[0] = weight;
            int repCount = reps.containsKey(exerciseId) ? reps.get(exerciseId) : 0;

            ProgressRecord record = new ProgressRecord(todayDate, weight, repCount);

            databaseService.saveExerciseProgress(userId, exerciseId, record, new DatabaseService.DatabaseCallback<Void>() {
                @Override
                public void onCompleted(Void v) {
                    savedCount[0]++;
                    if (savedCount[0] == totalToSave && !failed[0]) {
                        checkForAchievements(userId, maxWeightEntered[0]);
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

    private void checkForAchievements(String userId, float maxWeight) {
        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    List<String> newOnes = AchievementService.checkAchievements(user, maxWeight);
                    if (!newOnes.isEmpty()) {
                        for (String id : newOnes) {
                            user.addAchievement(id);
                            Toast.makeText(TrackWorkoutActivity.this, "הישג חדש: " + AchievementService.getAchievementName(id), Toast.LENGTH_LONG).show();
                        }
                        databaseService.updateUserAchievements(userId, user.getAchievements(), null);
                    }
                }
            }
            @Override
            public void onFailed(Exception e) {}
        });
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
}