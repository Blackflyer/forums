package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.ExerciseSelectAdapter;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class CreatePlanActivity extends AppCompatActivity {
    private RecyclerView rvExercises;
    private EditText etSearch, etPlanName;
    private MaterialButton btnSavePlan; // שימוש ב-MaterialButton לעיצוב המודרני
    private ExerciseSelectAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        // קבלת התאריך מה-Intent
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");

        initViews();
        loadExercisesFromDB();

        // מאזין לחיפוש תרגילים
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSavePlan.setOnClickListener(v -> saveNewPlan());
    }

    private void loadExercisesFromDB() {
        DatabaseService.getInstance().getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            @Override
            public void onCompleted(List<Exercise> exercises) {
                // אתחול ה-Adapter עם רשימת התרגילים
                adapter = new ExerciseSelectAdapter(exercises);
                rvExercises.setAdapter(adapter);
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CreatePlanActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveNewPlan() {
        String name = etPlanName.getText().toString().trim();

        // בדיקת תקינות - שם ותפיסת תרגילים
        if (name.isEmpty()) {
            etPlanName.setError("חובה להזין שם לתוכנית");
            return;
        }

        if (adapter == null || adapter.getSelectedExercises().isEmpty()) {
            Toast.makeText(this, "אנא בחר לפחות תרגיל אחד מהרשימה", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Exercise> selected = adapter.getSelectedExercises();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String planId = DatabaseService.getInstance().generatePlanId();

        // יצירת אובייקט ה-Plan
        // שים לב: הסרתי את המשתנה 'type' שלא היה מוגדר בקוד שלך, ושלחתי מחרוזת ריקה או סוג ברירת מחדל
        Plan newPlan = new Plan(planId, userId, selectedDate, name, "General");
        newPlan.setPlan(new ArrayList<>(selected));

        // הצגת אינדיקציה שהשמירה בביצוע
        btnSavePlan.setEnabled(false);
        btnSavePlan.setText("שומר...");

        DatabaseService.getInstance().createNewPlan(newPlan, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(CreatePlanActivity.this, "התוכנית נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();

                // --- השינוי מתחיל כאן ---
                // מעבר למסך המציג את תוכניות האימון לאותו יום
                Intent intent = new Intent(CreatePlanActivity.this, Plan_day.class);
                intent.putExtra("SELECTED_DATE", selectedDate); // מעבירים את התאריך הלאה
                startActivity(intent);

                finish(); // סוגרים את מסך היצירה כדי שהמשתמש לא יחזור אליו אם ילחץ על 'חזור'
            }

            @Override
            public void onFailed(Exception e) {
                // ... (ללא שינוי)
            }
        });
    }

    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearchExercise);
        etPlanName = findViewById(R.id.etPlanName);
        btnSavePlan = findViewById(R.id.btnSavePlan); // מקושר ל-MaterialButton ב-XML המעוצב
    }
}