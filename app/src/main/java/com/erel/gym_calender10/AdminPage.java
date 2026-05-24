package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * מסך זה מהווה את דף הבית עבור משתמשים בעלי הרשאות מנהל.
 * הוא מכיל לוח שנה למעקב אחר אימונים וגישה מהירה לכל פונקציות הניהול (משתמשים, תרגילים וכו')
 * וכן לפונקציות המשתמש הרגילות.
 */
public class AdminPage extends AppCompatActivity {

    private ImageButton btnEditProfile;
    private CalendarView calendarView;
    private MaterialButton btnGoUsersList, btnGoAddExercise, btnGoAddAdmin;
    private MaterialCardView cardTrackWorkout, cardMyPlans, cardAnalytics, cardHeatmap, cardProfile;

    /**
     * פונקציה זו מאתחלת את האקטיביטי, טוענת את הממשק ומגדירה את הלוח שנה והכפתורים.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        initViews();
        setupCalendar();
        setupClickListeners();
    }

    /**
     * מאתחלת את כל רכיבי התצוגה מה-XML.
     */
    private void initViews() {
        btnEditProfile = findViewById(R.id.btnEditProfile);
        calendarView = findViewById(R.id.calendarView);
        btnGoUsersList = findViewById(R.id.btnGoUsersList);
        btnGoAddExercise = findViewById(R.id.btnGoAddExercise);
        btnGoAddAdmin = findViewById(R.id.btnGoAddAdmin);
        
        // כרטיסיות הדאשבורד
        cardTrackWorkout = findViewById(R.id.cardTrackWorkout);
        cardMyPlans = findViewById(R.id.cardMyPlans);
        cardAnalytics = findViewById(R.id.cardAnalytics);
        cardHeatmap = findViewById(R.id.cardHeatmap);
        cardProfile = findViewById(R.id.cardProfile);
    }

    /**
     * מגדירה את לוח השנה, כולל סימון היום הנוכחי וטיפול בלחיצה על תאריכים.
     */
    private void setupCalendar() {
        // הוספת סימון ליום הנוכחי בלוח השנה
        List<EventDay> events = new ArrayList<>();
        events.add(new EventDay(Calendar.getInstance(), R.drawable.ic_launcher_background));
        calendarView.setEvents(events);

        // הגדרת מאזין ללחיצה על יום בלוח השנה למעבר לתוכנית האימונים של אותו יום
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
            int month = clickedDayCalendar.get(Calendar.MONTH) + 1; // חודשים מתחילים מ-0
            int year = clickedDayCalendar.get(Calendar.YEAR);

            String date = day + "/" + month + "/" + year;

            // מעבר למסך תוכנית יומית עבור התאריך שנבחר
            Intent intent = new Intent(AdminPage.this, Plan_day.class);
            intent.putExtra("SELECTED_DATE", date);
            startActivity(intent);
        });
    }

    /**
     * מגדירה את כל מאזיני הלחיצות עבור הכפתורים והכרטיסיות במסך.
     */
    private void setupClickListeners() {
        // עריכת פרופיל
        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, EditProfileActivity.class));
        });

        // רשימת משתמשים
        btnGoUsersList.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, Users_list.class));
        });

        // ניהול תרגילים
        btnGoAddExercise.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, ExerciseListActivity.class));
        });

        // הוספת מנהל חדש
        btnGoAddAdmin.setOnClickListener(v -> {
            startActivity(new Intent(AdminPage.this, AddAdmin.class));
        });

        // פונקציות דאשבורד - מעקב אימון, תוכניות, גרפים ומפת חום
        cardTrackWorkout.setOnClickListener(v -> 
            startActivity(new Intent(AdminPage.this, TrackWorkoutActivity.class)));

        cardMyPlans.setOnClickListener(v -> 
            startActivity(new Intent(AdminPage.this, item_plan.class)));

        cardAnalytics.setOnClickListener(v -> 
            startActivity(new Intent(AdminPage.this, Progress_Graph.class)));

        cardHeatmap.setOnClickListener(v -> 
            startActivity(new Intent(AdminPage.this, ActivityHeatmap.class)));

        cardProfile.setOnClickListener(v -> 
            startActivity(new Intent(AdminPage.this, Users_Profile.class)));
    }
}