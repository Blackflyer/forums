package com.erel.gym_calender10;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.PlanAdapter;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;

/**
 * מחלקת Plan_day מציגה את תוכניות האימון המתוכננות לתאריך ספציפי.
 * המשתמש יכול לצפות באימונים הקיימים, להוסיף אימון חדש או לבחור תאריך אחר.
 */
public class Plan_day extends AppCompatActivity {

    private RecyclerView rvPlans;
    private TextView tvDateTitle, tvEmptyState;
    private FloatingActionButton fabAddPlan;
    private PlanAdapter adapter;
    private String selectedDate;
    private Button btnBackToCalendar;

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי. 
     * היא מחלצת את התאריך הנבחר מה-Intent או משתמשת בתאריך הנוכחי כברירת מחדל.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_day);

        // קבלת התאריך שנשלח מהמסך הקודם
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null) {
            selectedDate = getIntent().getStringExtra("date");
        }

        // הגדרת תאריך נוכחי אם לא התקבל תאריך
        if (selectedDate == null || selectedDate.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            selectedDate = day + "/" + month + "/" + year;
        }

        initViews();
        loadPlansForDate();
    }

    /**
     * פעולה המאתחלת את רכיבי הממשק, מגדירה את כותרת התאריך ומאזינים ללחיצות.
     */
    private void initViews() {
        tvDateTitle = findViewById(R.id.tvDateTitle);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvPlans = findViewById(R.id.rvPlans);
        fabAddPlan = findViewById(R.id.fabAddPlan);
        btnBackToCalendar = findViewById(R.id.btnBackTocalender);
        ImageButton btnSearchDate = findViewById(R.id.btnSearchDate);

        tvDateTitle.setText("אימונים ל: " + selectedDate);
        rvPlans.setLayoutManager(new LinearLayoutManager(this));

        // מעבר למסך יצירת תוכנית חדשה
        fabAddPlan.setOnClickListener(v -> {
            Intent intent = new Intent(Plan_day.this, CreatePlanActivity.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });

        if (btnBackToCalendar != null) {
            btnBackToCalendar.setOnClickListener(v -> finish());
        }

        // פתיחת דיאלוג לבחירת תאריך חדש
        if (btnSearchDate != null) {
            btnSearchDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        Plan_day.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            tvDateTitle.setText("אימונים ל: " + selectedDate);
                            loadPlansForDate(); // טעינה מחדש של התוכניות לתאריך שנבחר
                        },
                        year, month, day);
                datePickerDialog.show();
            });
        }
    }

    /**
     * טוענת את תוכניות האימון ממסד הנתונים עבור המשתמש המחובר והתאריך שנבחר.
     */
    private void loadPlansForDate() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // קריאה לשירות מסד הנתונים לקבלת רשימת תוכניות
        DatabaseService.getInstance().getPlansByDate(userId, selectedDate, new DatabaseService.DatabaseCallback<List<Plan>>() {
            @Override
            public void onCompleted(List<Plan> plans) {
                // עדכון הממשק לפי תוצאות החיפוש
                if (plans == null || plans.isEmpty()) {
                    rvPlans.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyState.setVisibility(View.GONE);
                    rvPlans.setVisibility(View.VISIBLE);
                    adapter = new PlanAdapter(plans);
                    rvPlans.setAdapter(adapter);
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Plan_day.this, "שגיאה בטעינת אימונים: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
