package com.erel.gym_calender10.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
import com.erel.gym_calender10.TrackWorkoutActivity;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<Plan> plansList;

    public PlanAdapter(List<Plan> plansList) {
        this.plansList = plansList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Using the new item_plan layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = plansList.get(position);

        holder.tvPlanName.setText(plan.getPlanName());
        holder.tvPlanDate.setText("תאריך: " + plan.getDate());

        int exerciseCount = (plan.getPlan() != null) ? plan.getPlan().size() : 0;
        holder.tvExerciseCount.setText(exerciseCount + " תרגילים");

        holder.itemView.setOnClickListener(v -> {
            if (plan.getPlan() != null && !plan.getPlan().isEmpty()) {
                Exercise firstExercise = plan.getPlan().get(0);
                Intent intent = new Intent(v.getContext(), TrackWorkoutActivity.class);
                intent.putExtra("EXERCISE_ID", firstExercise.getId());
                intent.putExtra("EXERCISE_NAME", firstExercise.getName());
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "אין תרגילים בתוכנית זו", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    public void updateList(List<Plan> newList) {
        this.plansList = newList;
        notifyDataSetChanged();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPlanDate, tvExerciseCount;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvPlanDate = itemView.findViewById(R.id.tvPlanDate);
            tvExerciseCount = itemView.findViewById(R.id.tvExerciseCount);
        }
    }
}
