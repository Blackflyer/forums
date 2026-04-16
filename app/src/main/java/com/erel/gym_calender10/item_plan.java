package com.erel.gym_calender10;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.PlanAdapter;
import com.erel.gym_calender10.module.Plan;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class item_plan extends AppCompatActivity {

    private RecyclerView rvAllPlans;
    private PlanAdapter planAdapter;
    private List<Plan> plansList = new ArrayList<>();
    private MaterialToolbar toolbar;
    private EditText etSearchPlan;
    private ChipGroup cgDays;
    private int selectedDayOfWeek = -1; // -1 = הכל
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_plan);

        initViews();
        setupFilters();
        loadAllPlans();
    }

    private void initViews() {
        rvAllPlans = findViewById(R.id.rvAllPlans);
        rvAllPlans.setLayoutManager(new LinearLayoutManager(this));
        planAdapter = new PlanAdapter(plansList);
        rvAllPlans.setAdapter(planAdapter);

        etSearchPlan = findViewById(R.id.etSearchPlan);
        cgDays = findViewById(R.id.cgDays);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupFilters() {
        // האזנה לשינויים בטקסט החיפוש
        etSearchPlan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // האזנה לשינויים בבחירת יום
        cgDays.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipSun) selectedDayOfWeek = Calendar.SUNDAY;
            else if (checkedId == R.id.chipMon) selectedDayOfWeek = Calendar.MONDAY;
            else if (checkedId == R.id.chipTue) selectedDayOfWeek = Calendar.TUESDAY;
            else if (checkedId == R.id.chipWed) selectedDayOfWeek = Calendar.WEDNESDAY;
            else if (checkedId == R.id.chipThu) selectedDayOfWeek = Calendar.THURSDAY;
            else if (checkedId == R.id.chipFri) selectedDayOfWeek = Calendar.FRIDAY;
            else if (checkedId == R.id.chipSat) selectedDayOfWeek = Calendar.SATURDAY;
            else selectedDayOfWeek = -1; // chipAll or none

            applyFilters();
        });
    }

    private void applyFilters() {
        if (planAdapter != null) {
            planAdapter.filter(currentSearchQuery, selectedDayOfWeek);
        }
    }

    private void loadAllPlans() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "אנא התחבר מחדש", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseService.getInstance().getAllPlans(user.getUid(), new DatabaseService.DatabaseCallback<List<Plan>>() {
            @Override
            public void onCompleted(List<Plan> plans) {
                if (plans != null) {
                    plansList.clear();
                    plansList.addAll(plans);
                    planAdapter.updateList(plansList);
                    applyFilters(); // שמירה על הפילטרים הקיימים אם יש
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(item_plan.this, "שגיאה בטעינת תוכניות", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllPlans();
    }
}