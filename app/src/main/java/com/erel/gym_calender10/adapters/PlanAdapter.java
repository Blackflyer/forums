package com.erel.gym_calender10.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
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

        // --- הוספנו את הקוד הבא: מאזין ללחיצה על התוכנית ---
        holder.itemView.setOnClickListener(v -> {

            // אנחנו מוודאים שיש לפחות תרגיל אחד בתוכנית הזו כדי שלא נקבל שגיאה
            if (plan.getPlan() != null && !plan.getPlan().isEmpty()) {

                // לוקחים את התרגיל הראשון מהתוכנית
                // (בהמשך תוכל לעשות מסך שבוחרים איזה תרגיל בדיוק מתוך התוכנית רוצים לעשות)
                Exercise firstExercise = plan.getPlan().get(0);

                // יוצרים את המעבר למסך המעקב
                android.content.Intent intent = new android.content.Intent(v.getContext(), com.erel.gym_calender10.TrackWorkoutActivity.class);

                // שולחים למסך המעקב את הנתונים האמיתיים מתוך התרגיל!
                intent.putExtra("EXERCISE_ID", firstExercise.getId());
                intent.putExtra("EXERCISE_NAME", firstExercise.getName());

                // פותחים את המסך
                v.getContext().startActivity(intent);

            } else {
                android.widget.Toast.makeText(v.getContext(), "אין תרגילים בתוכנית זו", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
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
