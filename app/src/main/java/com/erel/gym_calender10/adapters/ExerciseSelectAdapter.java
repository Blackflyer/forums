package com.erel.gym_calender10.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
import com.erel.gym_calender10.module.Exercise;

import java.util.ArrayList;
import java.util.List;

public class ExerciseSelectAdapter extends RecyclerView.Adapter<ExerciseSelectAdapter.ViewHolder> {

    private List<Exercise> fullList;        // הרשימה המקורית (לחיפוש)
    private List<Exercise> displayedList;   // הרשימה שמוצגת כרגע
    private List<Exercise> selectedExercises = new ArrayList<>(); // תרגילים שנבחרו

    public ExerciseSelectAdapter(List<Exercise> exercises) {
        this.fullList = new ArrayList<>(exercises);
        this.displayedList = new ArrayList<>(exercises);
    }

    // פונקציית הסינון (Search)
    public void filter(String query) {
        displayedList.clear();
        if (query.isEmpty()) {
            displayedList.addAll(fullList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (Exercise ex : fullList) {
                if (ex.getName().toLowerCase().contains(filterPattern) ||
                        ex.getMuscleGroup().toLowerCase().contains(filterPattern)) {
                    displayedList.add(ex);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Exercise> getSelectedExercises() {
        return selectedExercises;
    }

    public void setSelectedExercises(List<Exercise> selected) {
        this.selectedExercises.clear();
        if (selected == null) return;
        for (Exercise s : selected) {
            for (Exercise f : fullList) {
                if (f.getId() != null && f.getId().equals(s.getId())) {
                    selectedExercises.add(f);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // שימוש ב-XML החדש שיצרנו
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_exercise_item_selectable, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise ex = displayedList.get(position);

        // הגדרת הטקסטים
        holder.tvExName.setText(ex.getName());
        holder.tvMuscleGroup.setText(ex.getMuscleGroup());

        // עדכון מצב ה-CheckBox לפי האם התרגיל נבחר
        holder.cbSelect.setChecked(selectedExercises.contains(ex));

        // לחיצה על כל השורה (הכרטיס) תבצע בחירה/ביטול
        holder.itemView.setOnClickListener(v -> {
            if (selectedExercises.contains(ex)) {
                selectedExercises.remove(ex);
            } else {
                selectedExercises.add(ex);
            }
            notifyItemChanged(position); // עדכון התצוגה של הפריט הספציפי
        });
    }

    @Override
    public int getItemCount() {
        return displayedList.size();
    }

    // ViewHolder שמתאים ל-XML החדש
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExName, tvMuscleGroup;
        CheckBox cbSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExName = itemView.findViewById(R.id.tvExName);
            tvMuscleGroup = itemView.findViewById(R.id.tvMuscleGroup);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }
}