package com.erel.gym_calender10.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.CreatePlanActivity;
import com.erel.gym_calender10.R;
import com.erel.gym_calender10.TrackWorkoutActivity;
import com.erel.gym_calender10.module.Plan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<Plan> fullList;
    private List<Plan> displayedList;
    private SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

    /**
     * בנאי למתאם תוכניות אימון.
     * @param plansList רשימת התוכניות להצגה.
     */
    public PlanAdapter(List<Plan> plansList) {
        this.fullList = new ArrayList<>(plansList);
        this.displayedList = new ArrayList<>(plansList);
        sortListByDate();
    }

    /**
     * יוצר ViewHolder עבור פריט תוכנית ברשימה.
     */
    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    /**
     * מקשר את נתוני התוכנית לתצוגה ומגדיר מאזיני לחיצה למעקב אחרי אימון ועריכת תוכנית.
     */
    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = displayedList.get(position);

        holder.tvPlanName.setText(plan.getPlanName());
        holder.tvPlanDate.setText("תאריך: " + plan.getDate());

        int exerciseCount = (plan.getPlan() != null) ? plan.getPlan().size() : 0;
        holder.tvExerciseCount.setText(exerciseCount + " תרגילים");

        holder.itemView.setOnClickListener(v -> {
            if (plan.getPlan() != null && !plan.getPlan().isEmpty()) {
                Intent intent = new Intent(v.getContext(), TrackWorkoutActivity.class);
                intent.putExtra("PLAN_ID", plan.getPlanId());
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "אין תרגילים בתוכנית זו", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEditPlan.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CreatePlanActivity.class);
            intent.putExtra("EDIT_MODE", true);
            intent.putExtra("PLAN_ID", plan.getPlanId());
            v.getContext().startActivity(intent);
        });
    }

    /**
     * מחזיר את מספר התוכניות המוצגות.
     */
    @Override
    public int getItemCount() {
        return displayedList.size();
    }

    /**
     * מעדכן את רשימת התוכניות, ממיין אותן לפי תאריך ומרענן את התצוגה.
     * @param newList הרשימה החדשה.
     */
    public void updateList(List<Plan> newList) {
        this.fullList = new ArrayList<>(newList);
        sortListByDate();
        this.displayedList = new ArrayList<>(fullList);
        notifyDataSetChanged();
    }

    /**
     * ממיין את רשימת התוכניות לפי תאריך, מהחדש ביותר לישן ביותר.
     */
    private void sortListByDate() {
        Collections.sort(fullList, (p1, p2) -> {
            try {
                return sdf.parse(p2.getDate()).compareTo(sdf.parse(p1.getDate())); // Newest first
            } catch (Exception e) {
                return 0;
            }
        });
    }

    /**
     * מסננת את רשימת התוכניות לפי שם ויום בשבוע.
     * @param query מחרוזת לחיפוש בשם התוכנית.
     * @param dayOfWeek היום בשבוע לסינון (או -1 לכל הימים).
     */
    public void filter(String query, int dayOfWeek) {
        displayedList.clear();
        String lowerQuery = query.toLowerCase().trim();

        for (Plan plan : fullList) {
            boolean matchesName = plan.getPlanName().toLowerCase().contains(lowerQuery);
            boolean matchesDay = (dayOfWeek == -1); 

            if (dayOfWeek != -1) {
                try {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sdf.parse(plan.getDate()));
                    if (cal.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                        matchesDay = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (matchesName && matchesDay) {
                displayedList.add(plan);
            }
        }
        notifyDataSetChanged();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPlanDate, tvExerciseCount;
        View btnEditPlan;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvPlanDate = itemView.findViewById(R.id.tvPlanDate);
            tvExerciseCount = itemView.findViewById(R.id.tvExerciseCount);
            btnEditPlan = itemView.findViewById(R.id.btnEditPlan);
        }
    }
}