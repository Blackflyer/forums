package com.erel.gym_calender10.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = plansList.get(position);

        holder.tvPlanName.setText(plan.getPlanName());
        holder.tvPlanType.setText("סוג: " + plan.getType());

        // מציג כמה תרגילים יש בתוכנית (אם הרשימה לא ריקה)
        int exerciseCount = (plan.getPlan() != null) ? plan.getPlan().size() : 0;
        holder.tvExercisesCount.setText(exerciseCount + " תרגילים");
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanName, tvPlanType, tvExercisesCount;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanName = itemView.findViewById(R.id.tvPlanName);
            tvPlanType = itemView.findViewById(R.id.tvPlanType);
            tvExercisesCount = itemView.findViewById(R.id.tvExercisesCount);
        }
    }
}
