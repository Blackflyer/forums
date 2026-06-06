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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * מסך זה אחראי על הצגת ההישגים של המשתמש.
 * הוא טוען את ההישגים מהמסד נתונים ומציג אותם ברשימה.
 */
public class AchievementsActivity extends AppCompatActivity {

    private RecyclerView rvAchievements;
    private AchievementAdapter adapter;
    private List<Achievement> achievementList = new ArrayList<>();

    /**
     * פונקציה זו נקראת בעת יצירת האקטיביטי.
     * היא מאתחלת את ממשק המשתמש, מגדירה את ה-Toolbar ואת ה-RecyclerView.
     * @param savedInstanceState מידע שנשמר במקרה שהאקטיביטי נוצר מחדש.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        // הגדרת כפתור החזרה
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // אתחול רשימת ההישגים
        rvAchievements = findViewById(R.id.rvAchievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AchievementAdapter(achievementList);
        rvAchievements.setAdapter(adapter);

        // טעינת ההישגים מהשרת
        loadAchievements();
    }

    /**
     * טוענת את נתוני המשתמש מהמסד נתונים ובודקת אילו הישגים הוא צבר.
     * אם נמצאו הישגים חדשים, הם מתווספים לפרופיל המשתמש ומעודכנים במסד הנתונים.
     */
    private void loadAchievements() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            /**
             * פונקציה זו מבוצעת לאחר קבלת נתוני המשתמש בהצלחה.
             * @param user אובייקט המשתמש שהתקבל.
             */
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    // בדיקה אם יש הישגים חדשים שהמשתמש זכאי להם
                    List<String> newAchievements = AchievementService.checkAchievements(user, 0);
                    if (!newAchievements.isEmpty()) {
                        for (String id : newAchievements) {
                            user.addAchievement(id);
                        }
                        // עדכון מסד הנתונים עם ההישגים החדשים
                        DatabaseService.getInstance().updateUserAchievements(uid, user.getAchievements(), null);
                    }
                    
                    // ניקוי הרשימה וטעינה מחדש של כל ההישגים המעודכנים
                    achievementList.clear();
                    achievementList.addAll(AchievementService.getAllAchievements(user.getAchievements()));
                    adapter.notifyDataSetChanged();
                }
            }

            /**
             * פונקציה זו מבוצעת במקרה של כישלון בשליפת נתוני המשתמש.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                // הצגת הודעת שגיאה במקרה של כישלון בטעינה
                Toast.makeText(AchievementsActivity.this, "שגיאה בטעינת הישגים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}