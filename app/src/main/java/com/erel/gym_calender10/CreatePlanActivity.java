package com.erel.gym_calender10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.ExerciseSelectAdapter;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.AchievementService;
import com.erel.gym_calender10.services.DatabaseService;
import com.erel.gym_calender10.services.NotificationHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * מסך זה מאפשר למשתמש ליצור תוכנית אימונים חדשה או לערוך תוכנית קיימת.
 * המשתמש בוחר תרגילים, מגדיר שם לתוכנית, תאריך ושעת אימון.
 */
public class CreatePlanActivity extends AppCompatActivity {
    private RecyclerView rvExercises;
    private EditText etSearch, etPlanName;
    private MaterialButton btnSavePlan, btnSelectTime;
    private TextView tvTitle;
    private ExerciseSelectAdapter adapter;
    private String selectedDate;
    private String selectedTime = "12:00"; // ברירת מחדל
    private boolean isEditMode = false;
    private String planIdToEdit = null;
    private Plan existingPlan = null;

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    /**
     * פונקציה זו מאתחלת את המסך, בודקת האם מדובר בעריכה או ביצירה חדשה,
     * ומגדירה את המאזינים לחיפוש ושמירה.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        // קבלת נתונים מה-Intent (האם מדובר בעריכה ומהו התאריך שנבחר)
        isEditMode = getIntent().getBooleanExtra("EDIT_MODE", false);
        planIdToEdit = getIntent().getStringExtra("PLAN_ID");
        selectedDate = getIntent().getStringExtra("SELECTED_DATE");
        if (selectedDate == null) {
            selectedDate = getIntent().getStringExtra("date");
        }

        initViews();
        
        if (isEditMode && planIdToEdit != null) {
            tvTitle.setText("עריכת תוכנית");
            btnSavePlan.setText("עדכן תוכנית");
            loadExistingPlan();
        } else {
            // הגדרת תאריך ברירת מחדל (היום) במידה ולא נבחר תאריך
            if (selectedDate == null || selectedDate.isEmpty()) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH) + 1;
                int year = calendar.get(Calendar.YEAR);
                selectedDate = day + "/" + month + "/" + year;
            }
            loadExercisesFromDB();
        }

        // בדיקת הרשאות להתראות עבור אנדרואיד 13 ומעלה
        checkNotificationPermission();

        // מאזין לשינויים בשדה החיפוש לסינון תרגילים
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnSavePlan.setOnClickListener(v -> saveOrUpdatePlan());
        findViewById(R.id.btnBack).setOnClickListener(v -> navigateToDashboard());
    }

    /**
     * טוענת תוכנית קיימת מהמסד נתונים לצורך עריכה.
     */
    private void loadExistingPlan() {
        DatabaseService.getInstance().getPlanById(planIdToEdit, new DatabaseService.DatabaseCallback<Plan>() {
            /**
             * מבוצע לאחר שליפת התוכנית בהצלחה.
             * @param plan אובייקט התוכנית שהתקבל.
             */
            @Override
            public void onCompleted(Plan plan) {
                if (plan != null) {
                    existingPlan = plan;
                    etPlanName.setText(plan.getPlanName());
                    selectedDate = plan.getDate();
                    selectedTime = plan.getTime();
                    btnSelectTime.setText("בחר שעת אימון: " + selectedTime);
                    loadExercisesFromDB(); // טעינת התרגילים ולאחר מכן סימון הנבחרים
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשליפת התוכנית.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CreatePlanActivity.this, "שגיאה בטעינת התוכנית", Toast.LENGTH_SHORT).show();
                loadExercisesFromDB();
            }
        });
    }

    /**
     * סוגר את המסך הנוכחי וחוזר למסך הקודם (הדאשבורד).
     */
    private void navigateToDashboard() {
        finish();
    }

    /**
     * בודקת ומבקשת הרשאות להצגת התראות (עבור תזכורות אימון).
     */
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    /**
     * מציגה דיאלוג לבחירת שעה עבור האימון המתוכנן.
     */
    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = Integer.parseInt(selectedTime.split(":")[0]);
        int minute = Integer.parseInt(selectedTime.split(":")[1]);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedTime = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    btnSelectTime.setText("בחר שעת אימון: " + selectedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    /**
     * טוענת את כל רשימת התרגילים הזמינה מהמסד נתונים.
     */
    private void loadExercisesFromDB() {
        DatabaseService.getInstance().getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            /**
             * מבוצע לאחר שליפת רשימת התרגילים בהצלחה.
             * @param exercises רשימת התרגילים שהתקבלה.
             */
            @Override
            public void onCompleted(List<Exercise> exercises) {
                adapter = new ExerciseSelectAdapter(exercises);
                rvExercises.setAdapter(adapter);
                
                // במידה ומדובר בעריכה, נסמן את התרגילים שכבר קיימים בתוכנית
                if (isEditMode && existingPlan != null && existingPlan.getPlan() != null) {
                    adapter.setSelectedExercises(existingPlan.getPlan());
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשליפת רשימת התרגילים.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(CreatePlanActivity.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * שומרת את התוכנית החדשה או מעדכנת את הקיימת במסד הנתונים.
     * לאחר השמירה, מתזמנת התראה ובודקת זכאות להישגים.
     */
    private void saveOrUpdatePlan() {
        String name = etPlanName.getText().toString().trim();

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
        String planId = isEditMode ? planIdToEdit : DatabaseService.getInstance().generatePlanId();

        // יצירת אובייקט תוכנית
        Plan plan = new Plan(planId, userId, selectedDate, name, "General", selectedTime);
        plan.setPlan(new ArrayList<>(selected));

        btnSavePlan.setEnabled(false);
        btnSavePlan.setText("שומר...");

        DatabaseService.getInstance().createNewPlan(plan, new DatabaseService.DatabaseCallback<Void>() {
            /**
             * מבוצע לאחר שמירת התוכנית בהצלחה.
             * @param object פרמטר ריק.
             */
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(CreatePlanActivity.this, isEditMode ? "התוכנית עודכנה בהצלחה!" : "התוכנית נשמרה בהצלחה!", Toast.LENGTH_SHORT).show();

                // תזמון התראות דחיפה לתזכורת על האימון
                NotificationHelper.scheduleWorkoutNotifications(CreatePlanActivity.this, name, selectedDate, selectedTime);

                // בדיקת הישגים רק במידה וזו תוכנית חדשה
                if (!isEditMode) {
                    checkForAchievements(userId);
                } else {
                    finish();
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשמירת התוכנית.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                btnSavePlan.setEnabled(true);
                btnSavePlan.setText(isEditMode ? "עדכן תוכנית" : "שמור תוכנית");
                Toast.makeText(CreatePlanActivity.this, "שגיאה בשמירה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * בודקת האם המשתמש זכאי להישגים חדשים בעקבות יצירת התוכנית ומעדכנת את המסד נתונים.
     * @param userId מזהה המשתמש.
     */
    private void checkForAchievements(String userId) {
        DatabaseService.getInstance().getUser(userId, new DatabaseService.DatabaseCallback<com.erel.gym_calender10.module.User>() {
            /**
             * מבוצע לאחר קבלת נתוני המשתמש בהצלחה לצורך בדיקת הישגים.
             * @param user אובייקט המשתמש שהתקבל.
             */
            @Override
            public void onCompleted(com.erel.gym_calender10.module.User user) {
                if (user != null) {
                    List<String> newAchievements = AchievementService.checkAchievements(user, 0);
                    if (!newAchievements.isEmpty()) {
                        for (String achievementId : newAchievements) {
                            user.addAchievement(achievementId);
                            Toast.makeText(CreatePlanActivity.this, "🏆 הישג חדש: " + AchievementService.getAchievementName(achievementId), Toast.LENGTH_LONG).show();
                        }
                        DatabaseService.getInstance().updateUserAchievements(userId, user.getAchievements(), new DatabaseService.DatabaseCallback<Void>() {
                            /**
                             * מבוצע לאחר עדכון רשימת ההישגים של המשתמש.
                             * @param object פרמטר ריק.
                             */
                            @Override
                            public void onCompleted(Void object) {
                                finish();
                            }

                            /**
                             * מבוצע במקרה של שגיאה בעדכון ההישגים.
                             * @param e השגיאה שהתרחשה.
                             */
                            @Override
                            public void onFailed(Exception e) {
                                finish();
                            }
                        });
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשליפת נתוני המשתמש לבדיקת הישגים.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                finish();
            }
        });
    }

    /**
     * מאתחלת את רכיבי הממשק מה-XML.
     */
    private void initViews() {
        rvExercises = findViewById(R.id.rvExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));

        etSearch = findViewById(R.id.etSearchExercise);
        etPlanName = findViewById(R.id.etPlanName);
        btnSavePlan = findViewById(R.id.btnSavePlan);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        tvTitle = findViewById(R.id.tvTitle); 
    }
}