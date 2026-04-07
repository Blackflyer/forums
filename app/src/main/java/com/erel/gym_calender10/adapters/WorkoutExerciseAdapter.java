package com.erel.gym_calender10.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
import com.erel.gym_calender10.module.Exercise;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.ViewHolder> {

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise, float currentWeight, int currentReps);
    }

    private List<Exercise> exercises;
    private Map<String, Float> weights = new HashMap<>();
    private Map<String, Integer> reps = new HashMap<>();
    private OnExerciseClickListener clickListener;

    public WorkoutExerciseAdapter(List<Exercise> exercises, OnExerciseClickListener clickListener) {
        this.exercises = exercises;
        this.clickListener = clickListener;
    }

    public Map<String, Float> getWeights() {
        return weights;
    }

    public Map<String, Integer> getReps() {
        return reps;
    }

    public void updateExerciseData(String exerciseId, float weight, int repCount) {
        weights.put(exerciseId, weight);
        reps.put(exerciseId, repCount);
        notifyDataSetChanged();
    }

    public void setInitialData(Map<String, Float> initialWeights, Map<String, Integer> initialReps) {
        this.weights.putAll(initialWeights);
        this.reps.putAll(initialReps);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise ex = exercises.get(position);
        holder.tvExerciseName.setText(ex.getName());

        Float currentWeight = weights.get(ex.getId());
        holder.tvWeightDisplay.setText(currentWeight != null ? String.valueOf(currentWeight) : "--");

        Integer currentReps = reps.get(ex.getId());
        holder.tvRepsDisplay.setText(currentReps != null ? String.valueOf(currentReps) : "--");

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExerciseClick(ex, currentWeight != null ? currentWeight : 0, currentReps != null ? currentReps : 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvWeightDisplay, tvRepsDisplay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvWeightDisplay = itemView.findViewById(R.id.tvWeightDisplay);
            tvRepsDisplay = itemView.findViewById(R.id.tvRepsDisplay);
        }
    }
}