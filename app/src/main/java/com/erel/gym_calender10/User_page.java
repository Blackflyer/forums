package com.erel.gym_calender10;

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

public class User_page extends AppCompatActivity {

    private RecyclerView rvUpcomingPlans;
    private PlanAdapter planAdapter;
    private List<Plan> upcomingPlansList = new ArrayList<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        initViews();
        setupCalendar();
        loadUpcomingPlans();
    }

    private void initViews() {
        rvUpcomingPlans = findViewById(R.id.rvUpcomingPlans);
        rvUpcomingPlans.setLayoutManager(new LinearLayoutManager(this));
        planAdapter = new PlanAdapter(upcomingPlansList);
        rvUpcomingPlans.setAdapter(planAdapter);

        findViewById(R.id.btn_go_dashboard).setOnClickListener(v -> {
            startActivity(new Intent(User_page.this, UserDashboardActivity.class));
        });

        findViewById(R.id.btnEditProfile).setOnClickListener(v -> {
            startActivity(new Intent(User_page.this, EditProfileActivity.class));
        });
    }

    private void setupCalendar() {
        CalendarView calendarView = findViewById(R.id.calendarView);

        // Add today as an event
        List<EventDay> events = new ArrayList<>();
        events.add(new EventDay(Calendar.getInstance(), R.drawable.ic_launcher_background));
        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            int day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
            int month = clickedDayCalendar.get(Calendar.MONTH) + 1;
            int year = clickedDayCalendar.get(Calendar.YEAR);

            String date = day + "/" + month + "/" + year;

            Intent intent = new Intent(User_page.this, CreatePlanActivity.class);
            intent.putExtra("date", date);
            startActivity(intent);
        });
    }

    private void loadUpcomingPlans() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseService.getInstance().getUser(currentUserId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null && user.getMaarachedPlans() != null) {
                    List<Plan> allPlans = user.getMaarachedPlans().getPlanArray();
                    if (allPlans != null) {
                        filterAndSortPlans(allPlans);
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(User_page.this, "שגיאה בטעינת תוכניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAndSortPlans(List<Plan> allPlans) {
        Date today = getStartOfDay(new Date());
        upcomingPlansList.clear();

        for (Plan plan : allPlans) {
            try {
                Date planDate = sdf.parse(plan.getDate());
                if (planDate != null && (planDate.after(today) || planDate.equals(today))) {
                    upcomingPlansList.add(plan);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Sort by date: closest first
        Collections.sort(upcomingPlansList, (p1, p2) -> {
            try {
                Date d1 = sdf.parse(p1.getDate());
                Date d2 = sdf.parse(p2.getDate());
                return d1.compareTo(d2);
            } catch (ParseException e) {
                return 0;
            }
        });

        planAdapter.updateList(upcomingPlansList);
    }

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
