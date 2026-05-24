package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * מחלקת MainActivity היא המסך הראשי של האפליקציה (מסך פתיחה/בחירה).
 * היא משמשת כנקודת כניסה המאפשרת למשתמש לבחור בין התחברות להרשמה.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton btnLogin, btnRegister;

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // אתחול רכיבי הממשק
        initViews();
    }

    /**
     * פעולה המקשרת בין משתני הקוד לרכיבי הממשק ב-XML ומגדירה מאזינים ללחיצות.
     */
    private void initViews() {
        btnLogin = findViewById(R.id.btnGoLogin);
        btnRegister = findViewById(R.id.btnGoRegister);

        if (btnLogin != null) {
            btnLogin.setText("התחברות (V2)");
            btnLogin.setOnClickListener(this);
        }
        if (btnRegister != null) {
            btnRegister.setOnClickListener(this);
        }
    }

    /**
     * טיפול באירועי לחיצה על כפתורי הממשק.
     * @param v הרכיב שעליו נלחץ.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGoRegister) {
            // מעבר למסך ההרשמה
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        } else if (id == R.id.btnGoLogin) {
            // מעבר למסך ההתחברות
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}
