package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        CalendarView calendarView = findViewById(R.id.calendarView);

        // --- הוספת סימונים ליומן (לדוגמה: סימון האימון של היום) ---
        List<EventDay> events = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // הוספת נקודה כחולה (או אייקון משלך) מתחת לתאריך של היום
        events.add(new EventDay(calendar, R.drawable.ic_launcher_background));
        calendarView.setEvents(events);
        // --------------------------------------------------------

        Button btnProfile = findViewById(R.id.button);
        Button btnUserPlans = findViewById(R.id.btn_user_plans);

        // 1. מאזין לחיצה לכפתור "דף פרופיל" (צד שמאל)
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // תוקן ל- User_page.this
                Intent profileIntent = new Intent(User_page.this, item_plan.class);
                startActivity(profileIntent);
            }
        });

        // 2. מאזין לחיצה לכפתור "התוכניות שלי" (צד ימין)
        btnUserPlans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // תוקן ל- User_page.this
                Intent plansIntent = new Intent(User_page.this, Plan_day.class);
                startActivity(plansIntent);
            }
        }); // <--- כאן היו חסרים הסוגריים שסוגרים את הפעולה!

        // 3. מאזין ללחיצה על תאריך
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();

                int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                int month = clickedDayCalendar.get(Calendar.MONTH) + 1; // חודשים מתחילים מ-0
                int year = clickedDayCalendar.get(Calendar.YEAR);

                String date = day + "/" + month + "/" + year;

                Intent intent = new Intent(User_page.this, CreatePlanActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });
    }
}