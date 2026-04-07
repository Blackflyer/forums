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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    public static final String MyPREFERENCES = "myPrefs";

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private DatabaseService databaseService;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        // מניעת חיתוך על ידי הסטטוס בר
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

        // מילוי אוטומטי של פרטים שמורים (אם קיימים)
        etEmail.setText(sharedpreferences.getString("email", ""));
        etPassword.setText(sharedpreferences.getString("password", ""));
    }

    private void initViews() {
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnLogin) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "נא להזין אימייל וסיסמה", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false); // מניעת לחיצות כפולות
            loginUser(email, password);
        } else if (id == R.id.btnRegister) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void loginUser(String email, String password) {
        databaseService.LoginUser(email, password, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "Login success, UID: " + uid);

                // שליפת אובייקט המשתמש המלא מה-Database
                databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User user) {
                        if (user != null) {
                            // שמירה רק לאחר הצלחה מלאה
                            saveToPrefs(email, password, uid);

                            Intent intent;
                            // בדיקה אם המשתמש הוא אדמין (וודא שאין NullPointerException)
                            if (Boolean.TRUE.equals(user.getAdmin())) {
                                intent = new Intent(LoginActivity.this, AdminPage.class);
                            } else {
                                intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
                            }

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            onFailed(new Exception("User data not found in database"));
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        btnLogin.setEnabled(true);
                        Toast.makeText(LoginActivity.this, "טעינת נתוני משתמש נכשלה", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                btnLogin.setEnabled(true);
                Log.e(TAG, "Login failed", e);
                etPassword.setError("אימייל או סיסמה שגויים");
                Toast.makeText(LoginActivity.this, "התחברות נכשלה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToPrefs(String email, String password, String uid) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("uid", uid);
        editor.apply();
    }
}