package com.erel.gym_calender10;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.PlanAdapter;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * מסך זה מציג את כל תוכניות האימונים של המשתמש ברשימה.
 * המשתמש יכול לחפש תוכנית ספציפית לפי שם או לסנן את התוכניות לפי ימי השבוע.
 */
public class item_plan extends AppCompatActivity implements PlanAdapter.OnPlanDeleteListener {

    private RecyclerView rvAllPlans;
    private PlanAdapter planAdapter;
    private List<Plan> plansList = new ArrayList<>();
    private EditText etSearchPlan;
    private ChipGroup cgDays;
    private int selectedDayOfWeek = -1; // -1 מייצג הצגת כל הימים
    private String currentSearchQuery = "";

    /**
     * פונקציה זו מאתחלת את המסך, מגדירה את רכיבי הממשק והפילטרים וטוענת את הנתונים.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_plan);

        initViews();
        setupFilters();
        loadAllPlans();
    }

    /**
     * מאתחלת את ה-Views מה-XML, מגדירה את ה-RecyclerView ואת סרגל הכלים.
     */
    private void initViews() {
        rvAllPlans = findViewById(R.id.rvAllPlans);
        rvAllPlans.setLayoutManager(new LinearLayoutManager(this));
        planAdapter = new PlanAdapter(plansList, this);
        rvAllPlans.setAdapter(planAdapter);

        etSearchPlan = findViewById(R.id.etSearchPlan);
        cgDays = findViewById(R.id.cgDays);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /**
     * מגדירה את המאזינים לשינויים בשדה החיפוש ובבחירת ימי השבוע (Chips).
     */
    private void setupFilters() {
        // מאזין לחיפוש טקסטואלי
        etSearchPlan.addTextChangedListener(new TextWatcher() {
            /**
             * מבוצע לפני שינוי הטקסט.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            /**
             * מבוצע בעת שינוי הטקסט - מעדכן את הפילטר.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                applyFilters();
            }

            /**
             * מבוצע לאחר שינוי הטקסט.
             */
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // מאזין לבחירת יום בשבוע לסנון
        cgDays.setOnCheckedChangeListener((group, checkedId) -> {
            /**
             * מבוצע בעת שינוי הבחירה ב-ChipGroup של הימים.
             */
            if (checkedId == R.id.chipSun) selectedDayOfWeek = Calendar.SUNDAY;
            else if (checkedId == R.id.chipMon) selectedDayOfWeek = Calendar.MONDAY;
            else if (checkedId == R.id.chipTue) selectedDayOfWeek = Calendar.TUESDAY;
            else if (checkedId == R.id.chipWed) selectedDayOfWeek = Calendar.WEDNESDAY;
            else if (checkedId == R.id.chipThu) selectedDayOfWeek = Calendar.THURSDAY;
            else if (checkedId == R.id.chipFri) selectedDayOfWeek = Calendar.FRIDAY;
            else if (checkedId == R.id.chipSat) selectedDayOfWeek = Calendar.SATURDAY;
            else selectedDayOfWeek = -1; 

            applyFilters();
        });
    }

    /**
     * מפעילה את הסינונים שנבחרו (חיפוש ויום בשבוע) על האדפטר של הרשימה.
     */
    private void applyFilters() {
        if (planAdapter != null) {
            planAdapter.filter(currentSearchQuery, selectedDayOfWeek);
        }
    }

    /**
     * טוענת את כל תוכניות האימונים המשויכות למשתמש המחובר מהמסד נתונים.
     */
    private void loadAllPlans() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "אנא התחבר מחדש", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseService.getInstance().getAllPlans(user.getUid(), new DatabaseService.DatabaseCallback<List<Plan>>() {
            /**
             * מבוצע לאחר קבלת רשימת כל התוכניות של המשתמש.
             * @param plans רשימת התוכניות שהתקבלה.
             */
            @Override
            public void onCompleted(List<Plan> plans) {
                if (plans != null) {
                    plansList.clear();
                    plansList.addAll(plans);
                    planAdapter.updateList(plansList);
                    applyFilters(); // החלת סינונים קיימים על הרשימה החדשה
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשליפת התוכניות.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(item_plan.this, "שגיאה בטעינת תוכניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * נקראת בכל חזרה למסך. מבצעת טעינה מחדש של התוכניות כדי להציג עדכונים.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadAllPlans();
    }

    /**
     * נקרא כאשר נבחרה תוכנית למחיקה מהאדפטר.
     */
    @Override
    public void onPlanDelete(Plan plan) {
        showDeleteConfirmationDialog(plan);
    }

    /**
     * מציגה דיאלוג אישור לפני מחיקת תוכנית אימון.
     * @param plan התוכנית למחיקה.
     */
    private void showDeleteConfirmationDialog(Plan plan) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת תוכנית")
                .setMessage("האם אתה בטוח שברצונך למחוק את התוכנית \"" + plan.getPlanName() + "\"?\nפעולה זו אינה ניתנת לביטול.")
                .setPositiveButton("מחק", (dialog, which) -> {
                    deletePlanFromDatabase(plan);
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    /**
     * מוחקת את התוכנית ממסד הנתונים ומעדכנת את הרשימה.
     * @param plan התוכנית למחיקה.
     */
    private void deletePlanFromDatabase(Plan plan) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseService.getInstance().deletePlan(user.getUid(), plan.getPlanId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(item_plan.this, "התוכנית נמחקה בהצלחה", Toast.LENGTH_SHORT).show();
                loadAllPlans();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(item_plan.this, "שגיאה במחיקת התוכנית", Toast.LENGTH_SHORT).show();
            }
        });
        }
        }