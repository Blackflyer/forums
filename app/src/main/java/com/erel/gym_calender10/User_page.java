package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User_page extends AppCompatActivity {

    private LinearLayout llUpcomingPlansContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // חיבור ה-Container מה-XML
        llUpcomingPlansContainer = findViewById(R.id.llUpcomingPlansContainer);

        CalendarView calendarView = findViewById(R.id.calendarView);

        // --- הוספת סימונים ליומן ---
        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, R.drawable.ic_launcher_background));
        calendarView.setEvents(events);
        // --------------------------------------------------------

        // מאזין ללחיצה על תאריך
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();

                int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
                int year = clickedDayCalendar.get(Calendar.YEAR);

                String date = day + "/" + month + "/" + year;

                Intent intent = new Intent(User_page.this, CreatePlanActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

        // קריאה לפונקציה שטוענת את התוכניות של המשתמש
        loadUpcomingPlans();
    }

    private void loadUpcomingPlans() {
        // קבלת ה-ID של המשתמש המחובר כרגע
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // שליפת אובייקט ה-User מהדאטה-בייס
        DatabaseService.getInstance().getUser(currentUserId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                // ניקוי תצוגות קודמות במקרה של רענון
                llUpcomingPlansContainer.removeAllViews();

                if (user != null && user.getMaarachedPlans() != null) {

                    // הערה: יש לוודא שקיימת מתודה שמחזירה את רשימת התוכניות מתוך ListOfPlans
                    // לפי DatabaseService.java ייתכן והרשימה נקראת planArray
                    // אם למתודה שלך קוראים אחרת ב-ListOfPlans.java (למשל getPlans()), שנה את הקוד בהתאם.
                    List<Plan> userPlans = user.getMaarachedPlans().getPlanArray();

                    if (userPlans != null && !userPlans.isEmpty()) {
                        for (Plan plan : userPlans) {
                            // יצירת הכרטיסייה על ידי ניפוח ה-XML שיצרנו
                            View planView = getLayoutInflater().inflate(R.layout.activity_stp, llUpcomingPlansContainer, false);

                            // קישור השדות בתוך הכרטיסייה
                            TextView tvPlanName = planView.findViewById(R.id.tvPlanName);
                            TextView tvPlanDate = planView.findViewById(R.id.tvPlanDate);

                            // השמת הנתונים מתוך אובייקט ה-Plan
                            tvPlanName.setText(plan.getPlanName());
                            tvPlanDate.setText("תאריך: " + plan.getDate());

                            // הוספת הכרטיסייה לתוך ה-ScrollView
                            llUpcomingPlansContainer.addView(planView);
                        }
                    } else {
                        showNoPlansMessage();
                    }
                } else {
                    showNoPlansMessage();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(User_page.this, "שגיאה בטעינת תוכניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoPlansMessage() {
        TextView noPlansText = new TextView(User_page.this);
        noPlansText.setText("אין לך תוכניות אימון שמורות.");
        noPlansText.setTextSize(16);
        noPlansText.setPadding(16, 16, 16, 16);
        llUpcomingPlansContainer.addView(noPlansText);
    }
}