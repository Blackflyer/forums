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

    private List<Exercise> exercises;
    private Map<String, Float> weights = new HashMap<>();

    public WorkoutExerciseAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    public Map<String, Float> getWeights() {
        return weights;
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

        // Remove previous listener to avoid infinite loop or wrong updates
        if (holder.textWatcher != null) {
            holder.etWeight.removeTextChangedListener(holder.textWatcher);
        }

        Float currentWeight = weights.get(ex.getId());
        holder.etWeight.setText(currentWeight != null ? String.valueOf(currentWeight) : "");

        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0) {
                        weights.put(ex.getId(), Float.parseFloat(s.toString()));
                    } else {
                        weights.remove(ex.getId());
                    }
                } catch (NumberFormatException e) {
                    weights.remove(ex.getId());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.etWeight.addTextChangedListener(holder.textWatcher);
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName;
        TextInputEditText etWeight;
        TextWatcher textWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            etWeight = itemView.findViewById(R.id.etWeight);
        }
    }
}