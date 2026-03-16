package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Plan_day extends AppCompatActivity {

    private RecyclerView rvPlans;
    private TextView tvDateTitle, tvEmptyState;
    private FloatingActionButton fabAddPlan;
    private PlanAdapter adapter;
    private String selectedDate;
    Button btnBackToCalendar = findViewById(R.id.btnBackTocalender);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_day);

        // 1. קבלת התאריך מהמסך הקודם - בודק גם "SELECTED_DATE" וגם "date" למקרה ששונה באחד המסכים
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null) {
            selectedDate = getIntent().getStringExtra("date");
        }

        // 2. אם המשתמש נכנס בלי תאריך ספציפי (למשל מכפתור "התוכניות שלי"), ניקח את התאריך של היום
        if (selectedDate == null || selectedDate.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1; // בחודשים מתחילים מ-0, לכן נוסיף 1
            int year = calendar.get(Calendar.YEAR);
            selectedDate = day + "/" + month + "/" + year;
        }

        initViews();
        loadPlansForDate();
    }

    private void initViews() {
        tvDateTitle = findViewById(R.id.tvDateTitle);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        rvPlans = findViewById(R.id.rvPlans);
        fabAddPlan = findViewById(R.id.fabAddPlan);

        // הוספת הכפתור השמאלי שחוזר ללוח השנה
        Button btnBackToCalendar = findViewById(R.id.btnBackTocalender);

        // הגדרת הכותרת
        tvDateTitle.setText("אימונים ל-" + selectedDate);

        // הגדרת ה-RecyclerView
        rvPlans.setLayoutManager(new LinearLayoutManager(this));

        // כפתור חזרה ליצירת אימון לאותו יום
        fabAddPlan.setOnClickListener(v -> {
            Intent intent = new Intent(Plan_day.this, CreatePlanActivity.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });

        // פעולת הלחיצה על כפתור החזרה ללוח השנה
        btnBackToCalendar.setOnClickListener(v -> {
            // אם המסך הקודם הוא לוח השנה, אפשר פשוט לסגור את המסך הנוכחי
            finish();
        });
    }

    private void loadPlansForDate() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // קריאה ל-Firebase להבאת התוכניות לפי התאריך המדויק
        DatabaseService.getInstance().getPlansByDate(userId, selectedDate, new DatabaseService.DatabaseCallback<List<Plan>>() {
            @Override
            public void onCompleted(List<Plan> plans) {
                if (plans == null || plans.isEmpty()) {
                    // אם אין אימונים
                    rvPlans.setVisibility(View.GONE);
                    tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    // אם יש אימונים - מציגים ברשימה
                    tvEmptyState.setVisibility(View.GONE);
                    rvPlans.setVisibility(View.VISIBLE);

                    // 3. תיקון האדפטר - הוספנו את 'Plan_day.this' כ-Context, שהיה חסר בקוד הקודם!
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