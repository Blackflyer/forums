package com.erel.gym_calender10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * מחלקת SplashActivity מציגה מסך פתיחה (Splash Screen) עם לוגו ואנימציה בעת הפעלת האפליקציה.
 * היא אחראית לבדוק האם המשתמש כבר מחובר ולנתב אותו למסך המתאים באופן אוטומטי.
 */
public class SplashActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvAppName;
    public static final String MyPREFERENCES = "myPrefs";

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי. 
     * היא מציגה את הממשק, מפעילה אנימציה ומגדירה השהיה למעבר למסך הבא.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ivLogo = findViewById(R.id.ivLogo);
        tvAppName = findViewById(R.id.tvAppName);

        // הוספת אנימציית הופעה פשוטה (Fade-in) למשך 1.5 שניות
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        ivLogo.startAnimation(fadeIn);
        tvAppName.startAnimation(fadeIn);

        // השהיה של 3 שניות לפני מעבר למסך הבא
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) return;

                try {
                    // בדיקה האם קיים משתמש מחובר במערכת ה-Firebase
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    
                    if (currentUser != null) {
                        // המשתמש מחובר, נבדוק ב-SharedPreferences האם הוא מנהל
                        SharedPreferences sharedpreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                        boolean isAdmin = sharedpreferences.getBoolean("isAdmin", false);
                        
                        Intent intent;
                        // ניתוב למסך המתאים לפי סוג המשתמש
                        if (isAdmin) {
                            intent = new Intent(SplashActivity.this, AdminPage.class);
                        } else {
                            intent = new Intent(SplashActivity.this, UserDashboardActivity.class);
                        }
                        startActivity(intent);
                    } else {
                        // לא נמצא משתמש מחובר, עוברים למסך הראשי (MainActivity) לבחירה בין התחברות להרשמה
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                } catch (Exception e) {
                    // במקרה של שגיאה בלתי צפויה באתחול, עוברים למסך הראשי כברירת מחדל
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish(); // סגירת מסך הפתיחה כך שלא ניתן יהיה לחזור אליו בלחיצת 'חזור'
            }
        }, 3000);
    }
}
