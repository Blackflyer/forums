package com.erel.gym_calender10;

import android.app.AlertDialog;
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

/**
 * מסך זה מציג את רשימת כל התרגילים המנוהלים במערכת.
 * המנהל יכול לצפות ברשימה, לנווט להוספת תרגיל חדש או לבחור תרגיל קיים לעדכון.
 */
public class ExerciseListActivity extends AppCompatActivity implements ManageExerciseAdapter.OnExerciseDeleteListener {

    private RecyclerView rvExercises;
    private ManageExerciseAdapter adapter;
    private ImageButton btnBack, btnAddExercise;
    private DatabaseService databaseService;

    /**
     * פונקציה זו מאתחלת את המסך וטוענת את רשימת התרגילים הראשונית.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        databaseService = DatabaseService.getInstance();
        initViews();
        loadExercises();
    }

    /**
     * נקראת כאשר המשתמש חוזר למסך. היא מבטיחה שרשימת התרגילים תהיה מעודכנת
     * במידה ובוצעו שינויים (הוספה/עריכה/מחיקה) במסכים קודמים.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadExercises(); 
    }

    /**
     * מאתחלת את רכיבי הממשק, מגדירה את האדפטר ל-RecyclerView ומקשרת מאזינים לכפתורים.
     */
    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        btnBack = findViewById(R.id.btnBack);
        btnAddExercise = findViewById(R.id.btnAddExercise);

        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageExerciseAdapter(this, new ArrayList<>(), this);
        rvExercises.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        
        // מעבר למסך הוספת תרגיל חדש
        btnAddExercise.setOnClickListener(v -> {
            startActivity(new Intent(ExerciseListActivity.this, AddExercise.class));
        });
    }

    /**
     * פונה לשירות מסד הנתונים כדי לקבל את רשימת התרגילים המעודכנת ומציגה אותה.
     */
    private void loadExercises() {
        databaseService.getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            /**
             * מבוצע לאחר קבלת רשימת התרגילים בהצלחה.
             * @param exercises רשימת התרגילים שהתקבלה.
             */
            @Override
            public void onCompleted(List<Exercise> exercises) {
                if (exercises != null) {
                    adapter.updateList(exercises);
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשליפת רשימת התרגילים.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ExerciseListActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * נקרא כאשר נבחר תרגיל למחיקה מהאדפטר.
     */
    @Override
    public void onExerciseDelete(Exercise exercise) {
        showDeleteConfirmationDialog(exercise);
    }

    /**
     * מציגה דיאלוג אישור לפני מחיקת תרגיל.
     * @param exercise התרגיל למחיקה.
     */
    private void showDeleteConfirmationDialog(Exercise exercise) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת תרגיל")
                .setMessage("האם אתה בטוח שברצונך למחוק את התרגיל \"" + exercise.getName() + "\"?\nפעולה זו תמחק אותו לצמיתות מהמאגר.")
                .setPositiveButton("מחק", (dialog, which) -> {
                    deleteExerciseFromDatabase(exercise);
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    /**
     * מוחקת את התרגיל ממסד הנתונים ומעדכנת את הרשימה.
     * @param exercise התרגיל למחיקה.
     */
    private void deleteExerciseFromDatabase(Exercise exercise) {
        databaseService.deleteExercise(exercise.getId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(ExerciseListActivity.this, "התרגיל נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                loadExercises();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(ExerciseListActivity.this, "שגיאה במחיקת התרגיל", Toast.LENGTH_SHORT).show();
            }
        });
    }
}