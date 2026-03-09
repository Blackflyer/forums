package com.erel.gym_calender10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;

public class AddAdmin extends AppCompatActivity {

    private EditText etAdminEmail, etAdminPassword;
    private Button btnCreateAdmin, btnBackFromAdmin;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        // חיבור ל-Database
        databaseService = DatabaseService.getInstance();

        // אתחול רכיבי התצוגה
        etAdminEmail = findViewById(R.id.etAdminEmail);
        etAdminPassword = findViewById(R.id.etAdminPassword);
        btnCreateAdmin = findViewById(R.id.btnCreateAdmin);
        btnBackFromAdmin = findViewById(R.id.btnBackFromAdmin);

        // כפתור חזור - מסיים את המסך הנוכחי וחוזר למסך הקודם
        btnBackFromAdmin.setOnClickListener(v -> finish());

        // כפתור יצירת מנהל
        btnCreateAdmin.setOnClickListener(v -> {
            String email = etAdminEmail.getText().toString().trim();
            String password = etAdminPassword.getText().toString().trim();

            // 1. וולידציה - בדיקה שהשדות לא ריקים
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(AddAdmin.this, "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. וולידציה - פיירבייס דורש סיסמה של 6 תווים לפחות
            if (password.length() < 6) {
                Toast.makeText(AddAdmin.this, "הסיסמה חייבת להכיל לפחות 6 תווים", Toast.LENGTH_SHORT).show();
                return;
            }

            // יצירת אובייקט User (תלוי במבנה המחלקה שלך, אפשר גם להוסיף שם אם נדרש)
            User newAdmin = new User();
            newAdmin.setEmail(email);
            newAdmin.setPassword(password);

            // קריאה לפונקציה החדשה שיצרנו
            databaseService.createNewAdmin(newAdmin, new DatabaseService.DatabaseCallback<String>() {
                @Override
                public void onCompleted(String uid) {
                    Toast.makeText(AddAdmin.this, "מנהל חדש נוצר בהצלחה!", Toast.LENGTH_LONG).show();
                    finish(); // חזרה למסך ניהול לאחר ההצלחה
                }

                @Override
                public void onFailed(Exception e) {
                    Log.e("AddAdmin", "שגיאה ביצירת מנהל", e);
                    Toast.makeText(AddAdmin.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
