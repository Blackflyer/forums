package com.erel.gym_calender10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;

/**
 * מחלקת LoginActivity אחראית על ניהול מסך ההתחברות של האפליקציה.
 * היא מאפשרת למשתמשים קיימים להתחבר ולמשתמשים חדשים לעבור למסך ההרשמה.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    public static final String MyPREFERENCES = "myPrefs";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseService databaseService;
    private SharedPreferences sharedpreferences;

    /**
     * פעולה זו נקראת בעת יצירת האקטיביטי. 
     * היא מאתחלת את התצוגה, השירותים וטוענת נתונים שמורים מה-SharedPreferences.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // אתחול רכיבי הממשק
        initViews();

        databaseService = DatabaseService.getInstance();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // טעינת אימייל וסיסמה שמורים אם קיימים
        etEmail.setText(sharedpreferences.getString("email", ""));
        etPassword.setText(sharedpreferences.getString("password", ""));
    }

    /**
     * פעולה המאתחלת את רכיבי הממשק (כפתורים ושדות טקסט) ומגדירה מאזינים ללחיצות.
     */
    private void initViews() {
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        if (btnLogin != null) btnLogin.setOnClickListener(this);
        if (btnRegister != null) btnRegister.setOnClickListener(this);
    }

    /**
     * טיפול בלחיצות על כפתורי הממשק.
     * @param v הרכיב שעליו נלחץ.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLogin) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // בדיקת תקינות קלט בסיסית
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא להזין אימייל וסיסמה", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false); 
            loginUser(email, password);
        } else if (id == R.id.btnRegister) {
            // מעבר למסך ההרשמה
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    /**
     * מבצעת התחברות של משתמש מול מסד הנתונים וטוענת את פרטיו.
     * @param email כתובת האימייל של המשתמש.
     * @param password סיסמת המשתמש.
     */
    private void loginUser(String email, String password) {
        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                // לאחר התחברות מוצלחת, שליפת נתוני המשתמש
                databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        if (user != null) {
                            boolean isAdmin = Boolean.TRUE.equals(user.getAdmin());
                            saveToPrefs(email, password, uid, isAdmin);

                            Intent intent;
                            // ניתוב המשתמש למסך המתאים לפי סוג המשתמש (מנהל או משתמש רגיל)
                            if (isAdmin) {
                                intent = new Intent(LoginActivity.this, AdminPage.class);
                            } else {
                                intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            onFailed(new Exception("User data not found"));
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        if (btnLogin != null) btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "טעינת נתוני משתמש נכשלה", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                if (btnLogin != null) btnLogin.setEnabled(true);
                if (etPassword != null) etPassword.setError("אימייל או סיסמה שגויים");
                Toast.makeText(LoginActivity.this, "התחברות נכשלה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * שומרת את פרטי המשתמש בהעדפות המשותפות (SharedPreferences) לגישה מהירה בעתיד.
     * @param email אימייל המשתמש.
     * @param password סיסמת המשתמש.
     * @param uid מזהה ייחודי של המשתמש.
     * @param isAdmin האם המשתמש הוא מנהל.
     */
    private void saveToPrefs(String email, String password, String uid, boolean isAdmin) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("uid", uid);
        editor.putBoolean("isAdmin", isAdmin);
        editor.apply();
    }
}
