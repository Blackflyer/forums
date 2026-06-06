package com.erel.gym_calender10;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.erel.gym_calender10.adapters.PlanAdapter;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * מחלקת User_page מציגה את לוח השנה האישי של המשתמש.
 * היא מאפשרת למשתמש לראות את ימי האימון המתוכננים שלו, לצפות ברשימת אימונים קרובים,
 * ולעבור לפירוט אימונים לפי בחירת יום בלוח השנה.
 */
public class User_page extends AppCompatActivity {

    private RecyclerView rvUpcomingPlans;
    private PlanAdapter planAdapter;
    private List<Plan> upcomingPlansList = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        initViews();
        setupCalendar();
        loadUpcomingPlans();
    }

    /**
     * מאתחלת את רכיבי הממשק, ה-RecyclerView וה-Adapter.
     */
    private void initViews() {
        rvUpcomingPlans = findViewById(R.id.rvUpcomingPlans);
        rvUpcomingPlans.setLayoutManager(new LinearLayoutManager(this));
        planAdapter = new PlanAdapter(upcomingPlansList, null);
        rvUpcomingPlans.setAdapter(planAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btn_go_dashboard).setOnClickListener(v -> navigateToDashboard());
    }

    /**
     * מבצעת ניווט חזרה למסך הלובי (Dashboard) המתאים לפי סוג המשתמש.
     */
    private void navigateToDashboard() {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean isAdmin = prefs.getBoolean("isAdmin", false);
        Intent intent;
        if (isAdmin) {
            intent = new Intent(this, AdminPage.class);
        } else {
            intent = new Intent(this, UserDashboardActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * מגדירה את לוח השנה ומאזין ללחיצה על יום מסוים.
     * בעת לחיצה, המשתמש מועבר למסך Plan_day המציג את האימונים לאותו יום.
     */
    private void setupCalendar() {
        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
            int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
            int year = clickedDayCalendar.get(Calendar.YEAR);

            String date = day + "/" + month + "/" + year;

            // מעבר למסך פירוט אימונים לתאריך הנבחר
            Intent intent = new Intent(User_page.this, Plan_day.class);
            intent.putExtra("SELECTED_DATE", date);
            startActivity(intent);
        });
    }

    /**
     * טוענת את כל תוכניות האימון של המשתמש המחובר ממסד הנתונים.
     * התוכניות משמשות לסימון בלוח השנה ולהצגת רשימת האימונים הקרובים.
     */
    private void loadUpcomingPlans() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseService.getInstance().getUser(currentUserId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null && user.getMaarachedPlans() != null) {
                    List<Plan> allPlans = user.getMaarachedPlans().getPlanArray();
                    if (allPlans != null) {
                        // סינון ומיון לאימונים קרובים
                        filterAndSortPlans(allPlans);
                        // עדכון לוח השנה בסימונים
                        updateCalendarWithPlans(allPlans);
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(User_page.this, "שגיאה בטעינת תוכניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * מעדכנת את לוח השנה עם אירועים (Events) עבור כל יום שבו מתוכנן אימון.
     * @param allPlans רשימת כל תוכניות האימון של המשתמש.
     */
    private void updateCalendarWithPlans(List<Plan> allPlans) {
        CalendarView calendarView = findViewById(R.id.calendarView);
        List<EventDay> events = new ArrayList<>();

        // הוספת היום הנוכחי כאירוע מיוחד
        events.add(new EventDay(Calendar.getInstance(), R.drawable.ic_launcher_background));

        for (Plan plan : allPlans) {
            try {
                Date date = sdf.parse(plan.getDate());
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    // הוספת אייקון לכל יום שבו יש אימון מתוכנן
                    events.add(new EventDay(calendar, R.drawable.ic_launcher_foreground));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendarView.setEvents(events);
    }

    /**
     * מסננת את רשימת האימונים כך שישארו רק אימונים מהיום והלאה, וממיינת אותם לפי תאריך (הקרוב ביותר ראשון).
     * @param allPlans רשימת כל התוכניות לסינון ומיון.
     */
    private void filterAndSortPlans(List<Plan> allPlans) {
        Date today = getStartOfDay(new Date());
        upcomingPlansList.clear();

        for (Plan plan : allPlans) {
            try {
                Date planDate = sdf.parse(plan.getDate());
                // בדיקה האם התאריך הוא היום או בעתיד
                if (planDate != null && (planDate.after(today) || planDate.equals(today))) {
                    upcomingPlansList.add(plan);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // מיון הרשימה לפי תאריך: מהקרוב לרחוק
        Collections.sort(upcomingPlansList, (p1, p2) -> {
            try {
                Date d1 = sdf.parse(p1.getDate());
                Date d2 = sdf.parse(p2.getDate());
                return d1.compareTo(d2);
            } catch (ParseException e) {
                return 0;
            }
        });

        // עדכון ה-Adapter עם הרשימה החדשה
        planAdapter.updateList(upcomingPlansList);
    }

    /**
     * מחזירה אובייקט Date המייצג את תחילת היום (שעה 00:00:00) עבור תאריך נתון.
     * משמש להשוואה מדויקת בין תאריכים ללא התחשבות בשעה.
     * @param date התאריך לחישוב.
     * @return תאריך המייצג את תחילת אותו יום.
     */
    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
