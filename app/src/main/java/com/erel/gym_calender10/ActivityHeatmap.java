package com.erel.gym_calender10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityHeatmap extends AppCompatActivity {

    private RecyclerView rvHeatmap;
    private TextView tvTotalWorkouts, tvCurrentStreak, tvBestStreak;
    private DatabaseService databaseService;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);

        rvHeatmap = findViewById(R.id.rvHeatmap);
        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvCurrentStreak = findViewById(R.id.tvCurrentStreak);
        tvBestStreak = findViewById(R.id.tvBestStreak);

        databaseService = DatabaseService.getInstance();

        MaterialButton btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
        btnBackToDashboard.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
            boolean isAdmin = prefs.getBoolean("isAdmin", false);
            Intent intent;
            if (isAdmin) {
                intent = new Intent(ActivityHeatmap.this, AdminPage.class);
            } else {
                intent = new Intent(ActivityHeatmap.this, UserDashboardActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        loadWorkoutData();
    }

    private void loadWorkoutData() {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) return;

        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null && user.getMaarachedPlans() != null && user.getMaarachedPlans().getPlanArray() != null) {
                    processWorkouts(user.getMaarachedPlans().getPlanArray());
                }
            }

            @Override
            public void onFailed(Exception e) {
                // Handle error
            }
        });
    }

    private void processWorkouts(List<Plan> plans) {
        Map<String, Integer> workoutCounts = new HashMap<>();
        List<Date> workoutDates = new ArrayList<>();

        for (Plan plan : plans) {
            String dateStr = plan.getDate();
            if (dateStr == null) continue;
            workoutCounts.put(dateStr, workoutCounts.getOrDefault(dateStr, 0) + 1);
            try {
                workoutDates.add(sdf.parse(dateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        tvTotalWorkouts.setText(String.valueOf(plans.size()));
        calculateStreaks(workoutDates);
        setupHeatmap(workoutCounts);
    }

    private void calculateStreaks(List<Date> dates) {
        if (dates.isEmpty()) return;
        Collections.sort(dates);

        int currentStreak = 0;
        int bestStreak = 0;
        int tempStreak = 0;

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();

        Date lastDate = null;
        for (Date d : dates) {
            if (lastDate != null) {
                long diff = d.getTime() - lastDate.getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                if (days == 1) {
                    tempStreak++;
                } else if (days > 1) {
                    if (tempStreak > bestStreak) bestStreak = tempStreak;
                    tempStreak = 1;
                }
            } else {
                tempStreak = 1;
            }
            lastDate = d;
        }
        if (tempStreak > bestStreak) bestStreak = tempStreak;

        // Calculate current streak from today backwards
        currentStreak = 0;
        cal.setTime(today);
        while (true) {
            boolean found = false;
            for (Date d : dates) {
                if (sdf.format(d).equals(sdf.format(cal.getTime()))) {
                    currentStreak++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Check if it was yesterday (allow 1 day gap if still today)
                if (currentStreak == 0) {
                     cal.add(Calendar.DATE, -1);
                     for (Date d : dates) {
                        if (sdf.format(d).equals(sdf.format(cal.getTime()))) {
                            currentStreak++;
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) break;
            }
            cal.add(Calendar.DATE, -1);
        }

        tvCurrentStreak.setText(String.valueOf(currentStreak));
        tvBestStreak.setText(String.valueOf(bestStreak));
    }

    private void setupHeatmap(Map<String, Integer> workoutCounts) {
        List<Integer> intensities = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // Start from a year ago
        
        // Adjust to start of week (Sunday)
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DATE, -1);
        }

        for (int i = 0; i < 364; i++) {
            String dateStr = sdf.format(cal.getTime());
            intensities.add(workoutCounts.getOrDefault(dateStr, 0));
            cal.add(Calendar.DATE, 1);
        }

        HeatmapAdapter adapter = new HeatmapAdapter(intensities);
        rvHeatmap.setLayoutManager(new GridLayoutManager(this, 7, GridLayoutManager.HORIZONTAL, false));
        rvHeatmap.setAdapter(adapter);
    }

    static class HeatmapAdapter extends RecyclerView.Adapter<HeatmapAdapter.ViewHolder> {
        private List<Integer> intensities;

        HeatmapAdapter(List<Integer> intensities) {
            this.intensities = intensities;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = new View(parent.getContext());
            int size = (int) (12 * parent.getContext().getResources().getDisplayMetrics().density);
            int margin = (int) (2 * parent.getContext().getResources().getDisplayMetrics().density);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
            params.setMargins(margin, margin, margin, margin);
            v.setLayoutParams(params);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int intensity = intensities.get(position);
            if (intensity == 0) holder.itemView.setBackgroundColor(Color.parseColor("#EBEDF0"));
            else if (intensity == 1) holder.itemView.setBackgroundColor(Color.parseColor("#9BE9A8"));
            else if (intensity == 2) holder.itemView.setBackgroundColor(Color.parseColor("#40C463"));
            else if (intensity == 3) holder.itemView.setBackgroundColor(Color.parseColor("#30A14E"));
            else holder.itemView.setBackgroundColor(Color.parseColor("#216E39"));
        }

        @Override
        public int getItemCount() {
            return intensities.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) { super(itemView); }
        }
    }
}