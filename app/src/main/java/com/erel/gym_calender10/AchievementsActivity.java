package com.erel.gym_calender10;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.AchievementAdapter;
import com.erel.gym_calender10.module.Achievement;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.AchievementService;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView rvAchievements;
    private AchievementAdapter adapter;
    private List<Achievement> achievementList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAchievements = findViewById(R.id.rvAchievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AchievementAdapter(achievementList);
        rvAchievements.setAdapter(adapter);

        loadAchievements();
    }

    private void loadAchievements() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    // Check for any new achievements that might have been missed
                    List<String> newAchievements = AchievementService.checkAchievements(user, 0);
                    if (!newAchievements.isEmpty()) {
                        for (String id : newAchievements) {
                            user.addAchievement(id);
                        }
                        // Update DB with new achievements
                        DatabaseService.getInstance().updateUserAchievements(uid, user.getAchievements(), null);
                    }
                    
                    achievementList.clear();
                    achievementList.addAll(AchievementService.getAllAchievements(user.getAchievements()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(AchievementsActivity.this, "שגיאה בטעינת הישגים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}