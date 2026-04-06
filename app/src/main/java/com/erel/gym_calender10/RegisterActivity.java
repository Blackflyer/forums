package com.erel.gym_calender10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.button.MaterialButton;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    public static final String MyPREFERENCES = "myPrefs";

    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister; // שימוש ב-Material
    private TextView tvLogin;

    private DatabaseService databaseService;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // טיפול במרווחי מערכת (סטטוס בר)
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initViews();

        databaseService = DatabaseService.getInstance();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etFName = findViewById(R.id.fname);
        etLName = findViewById(R.id.et_lname);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.btnToLogin);

        // הגדרת המאזין
        btnRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRegister) {
            Log.d("RegisterActivity", "Register Button clicked!");
            validateAndRegister();
        } else if (id == R.id.btnToLogin) {
            // מעבר לדף התחברות אם כבר יש חשבון
            finish();
        }
    }


    private void validateAndRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fName = etFName.getText().toString().trim();
        String lName = etLName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // בדיקת שדות ריקים
        if (email.isEmpty() || password.isEmpty() || fName.isEmpty() || lName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "חובה למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // בדיקת פורמט אימייל
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("אימייל לא תקין");
            return;
        }

        // בדיקת אורך סיסמה (Firebase דורש לפחות 6)
        if (password.length() < 6) {
            etPassword.setError("הסיסמה חייבת להכיל לפחות 6 תווים");
            return;
        }

        // השבתת כפתור למניעת לחיצות כפולות
        btnRegister.setEnabled(false);
        btnRegister.setText("רושם משתמש...");

        // יצירת אובייקט זמני (ה-ID יתעדכן לאחר הרישום)
        User newUser = new User("", fName, lName, phone, email, password);
        createUserInDatabase(newUser);
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "User created successfully with UID: " + uid);

                user.setId(uid);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", user.getEmail());
                editor.putString("uid", uid);
                editor.apply();

                // 1. תיקון ה-Toast כדי שישרוד את מעבר המסך:
                Toast.makeText(getApplicationContext(), "נרשמת בהצלחה!", Toast.LENGTH_LONG).show();

                // 2. מעבר למסך האמיתי של האפליקציה (כמו עמוד המשתמש) ולא למסך הפתיחה:
                // הערה: החלף את User_page.class במסך שתרצה שהמשתמש יראה אחרי התחברות
                Intent mainIntent = new Intent(RegisterActivity.this, User_page.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to create user", e);
                btnRegister.setEnabled(true);
                btnRegister.setText("הרשמה");
                Toast.makeText(RegisterActivity.this, "שגיאה ברישום: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}