package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.PlanAdapter;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Users_Profile extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail, tvProfilePhone;
    private Button btnLogout;
    private ImageButton btnBack;
    private RecyclerView rvProfilePlans;
    private PlanAdapter planAdapter;
    private DatabaseService databaseService;

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי.
     * מאתחלת את שירות מסד הנתונים, רכיבי התצוגה, וטוענת את נתוני המשתמש.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        databaseService = DatabaseService.getInstance();

        initViews();
        loadUserData();

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Users_Profile.this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Users_Profile.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * מאתחלת את רכיבי הממשק (TextViews, Buttons, RecyclerView) ומגדירה מאזיני לחיצה.
     */
    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        rvProfilePlans = findViewById(R.id.rvProfilePlans);
        Button btnViewProgress = findViewById(R.id.btnViewProgress);
        ImageButton btnEditProfile = findViewById(R.id.btnEditProfile);

        rvProfilePlans.setLayoutManager(new LinearLayoutManager(this));
        
        btnViewProgress.setOnClickListener(v -> {
            Intent intent = new Intent(Users_Profile.this, Progress_Graph.class);
            startActivity(intent);
        });

        findViewById(R.id.btnViewAchievements).setOnClickListener(v -> {
            Intent intent = new Intent(Users_Profile.this, AchievementsActivity.class);
            startActivity(intent);
        });

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(Users_Profile.this, EditProfileActivity.class));
        });
    }

    /**
     * טוענת את נתוני המשתמש המחובר מ-Firebase ומהמסד הנתונים ומציגה אותם בממשק.
     */
    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        databaseService.getUser(currentUser.getUid(), new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    tvProfileName.setText(user.getFname() + " " + (user.getLname() != null ? user.getLname() : ""));
                    tvProfileEmail.setText(user.getEmail());
                    tvProfilePhone.setText("טלפון: " + (user.getPhone() != null ? user.getPhone() : "לא הוזן"));

                    if (user.getMaarachedPlans() != null && user.getMaarachedPlans().getPlanArray() != null) {
                        planAdapter = new PlanAdapter(user.getMaarachedPlans().getPlanArray());
                        rvProfilePlans.setAdapter(planAdapter);
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("Users_Profile", "Failed to load user data", e);
                Toast.makeText(Users_Profile.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}