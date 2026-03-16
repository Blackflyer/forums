package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Users_Profile extends AppCompatActivity {

    private TextView tvProfileName, tvProfileEmail;
    private Button btnLogout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        initViews();
        loadUserData();

        // כפתור חזרה (הנחתי שהוא חוזר למסך הראשי של היומן)
        btnBack.setOnClickListener(v -> {
            finish(); // סוגר את הפעילות הנוכחית וחוזר לקודמת
        });

        // כפתור התנתקות מ-Firebase
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Users_Profile.this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

            // מעבר למסך ההתחברות (נקה את היסטוריית המסכים כדי שלא יוכל לחזור אחורה בלי להתחבר)
            Intent intent = new Intent(Users_Profile.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initViews() {
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBackFromProfile);
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // שימוש בפונקציה שכבר קיימת אצלך ב-DatabaseService!
            DatabaseService.getInstance().getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                @Override
                public void onCompleted(User user) {
                    if (user != null) {
                        // מעדכן את ממשק המשתמש עם הנתונים שחזרו מהדאטה-בייס
                        tvProfileName.setText(user.getFname() != null ? user.getFname() : "משתמש ללא שם");
                        tvProfileEmail.setText(user.getEmail());
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("User_page", "Failed to load user data: " + e.getMessage());
                    Toast.makeText(Users_Profile.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
                    // במקרה של שגיאה לפחות נציג את האימייל מ-Auth
                    tvProfileEmail.setText(currentUser.getEmail());
                }
            });
        } else {
            // אם משום מה המשתמש לא מחובר, נזרוק אותו למסך הלוגין
            startActivity(new Intent(Users_Profile.this, LoginActivity.class));
            finish();
        }
    }
}