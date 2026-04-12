package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.ManageExerciseAdapter;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.services.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {

    private RecyclerView rvExercises;
    private ManageExerciseAdapter adapter;
    private ImageButton btnBack, btnAddExercise;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        databaseService = DatabaseService.getInstance();
        initViews();
        loadExercises();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExercises(); // Reload in case an exercise was updated
    }

    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        btnBack = findViewById(R.id.btnBack);
        btnAddExercise = findViewById(R.id.btnAddExercise);

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageExerciseAdapter(this, new ArrayList<>());
        rvExercises.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        
        btnAddExercise.setOnClickListener(v -> {
            startActivity(new Intent(ExerciseListActivity.this, AddExercise.class));
        });
    }

    private void loadExercises() {
        databaseService.getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            @Override
            public void onCompleted(List<Exercise> exercises) {
                if (exercises != null) {
                    adapter.updateList(exercises);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ExerciseListActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}