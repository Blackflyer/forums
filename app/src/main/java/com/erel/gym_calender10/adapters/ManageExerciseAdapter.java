package com.erel.gym_calender10.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.AddExercise;
import com.erel.gym_calender10.R;
import com.erel.gym_calender10.module.Exercise;

import java.util.List;

public class ManageExerciseAdapter extends RecyclerView.Adapter<ManageExerciseAdapter.ViewHolder> {

    private List<Exercise> exerciseList;
    private Context context;
    private OnExerciseDeleteListener deleteListener;

    public interface OnExerciseDeleteListener {
        void onExerciseDelete(Exercise exercise);
    }

    /**
     * בנאי למתאם ניהול תרגילים.
     * @param context הקשר האקטיביטי.
     * @param exerciseList רשימת התרגילים לניהול.
     */
    public ManageExerciseAdapter(Context context, List<Exercise> exerciseList, OnExerciseDeleteListener deleteListener) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.deleteListener = deleteListener;
    }

    /**
     * יוצר ViewHolder עבור שורת ניהול תרגיל (עריכה).
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_manage, parent, false);
        return new ViewHolder(v);
    }

    /**
     * מקשר את נתוני התרגיל לתצוגה ומגדירה מאזין לכפתור עריכה ולחיצה ארוכה למחיקה.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise ex = exerciseList.get(position);

        holder.tvExName.setText(ex.getName());
        holder.tvMuscleGroup.setText(ex.getMuscleGroup());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddExercise.class);
            intent.putExtra("EXERCISE_ID", ex.getId());
            intent.putExtra("EXERCISE_NAME", ex.getName());
            intent.putExtra("EXERCISE_DESCRIPTION", ex.getDescription());
            intent.putExtra("EXERCISE_EQUIPMENT", ex.getEquipment());
            intent.putExtra("EXERCISE_MUSCLE_GROUP", ex.getMuscleGroup());
            intent.putExtra("EXERCISE_SETS", ex.getSets());
            intent.putExtra("EXERCISE_TIMES", ex.getTimes());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onExerciseDelete(ex);
            }
            return true;
        });
    }

    /**
     * מחזיר את מספר התרגילים ברשימה.
     */
    @Override
    public int getItemCount() {
        return exerciseList.size();
    }

    /**
     * מעדכן את רשימת התרגילים ומרענן את התצוגה.
     * @param newList הרשימה החדשה.
     */
    public void updateList(List<Exercise> newList) {
        this.exerciseList = newList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExName, tvMuscleGroup;
        ImageButton btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExName = itemView.findViewById(R.id.tvExName);
            tvMuscleGroup = itemView.findViewById(R.id.tvMuscleGroup);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}