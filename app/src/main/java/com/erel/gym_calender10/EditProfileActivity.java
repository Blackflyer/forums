package com.erel.gym_calender10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

/**
 * מסך זה מאפשר למשתמש לערוך את פרטי הפרופיל האישיים שלו, כגון שם פרטי, שם משפחה ומספר טלפון.
 */
public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFirstName, etLastName, etPhone, etEmail;
    private Button btnSaveProfile;
    private ProgressBar progressBar;
    
    private DatabaseService databaseService;
    private User currentUser;

    /**
     * פונקציה זו מאתחלת את המסך, מגדירה את המאזינים לכפתורי השמירה והחזרה, וטוענת את נתוני המשתמש.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        databaseService = DatabaseService.getInstance();

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        progressBar = findViewById(R.id.progressBar);

        loadUserData();

        btnSaveProfile.setOnClickListener(v -> saveProfileChanges());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    /**
     * טוענת את נתוני המשתמש המחובר מהמסד נתונים ומציגה אותם בשדות הטקסט המתאימים.
     */
    private void loadUserData() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        databaseService.getUser(uid, new DatabaseService.DatabaseCallback<User>() {
            /**
             * מבוצע לאחר קבלת נתוני המשתמש מהשרת.
             * @param user אובייקט המשתמש שהתקבל.
             */
            @Override
            public void onCompleted(User user) {
                progressBar.setVisibility(View.GONE);
                if (user != null) {
                    currentUser = user;
                    etFirstName.setText(user.getFname());
                    etLastName.setText(user.getLname());
                    etPhone.setText(user.getPhone());
                    etEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(EditProfileActivity.this, "לא נמצאו נתוני משתמש", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            /**
             * מבוצע במקרה של שגיאה בשליפת נתוני המשתמש.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "שגיאה בטעינת נתונים: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * אוספת את הנתונים החדשים מהשדות, מבצעת בדיקת תקינות ושומרת את השינויים במסד הנתונים.
     */
    private void saveProfileChanges() {
        if (currentUser == null) return;

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // בדיקה שכל השדות הנדרשים מולאו
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // עדכון אובייקט המשתמש בנתונים החדשים
        currentUser.setFname(firstName);
        currentUser.setLname(lastName);
        currentUser.setPhone(phone);
        // הערה: כתובת האימייל לא מעודכנת כאן כדי למנוע בעיות סנכרון עם מנגנון האימות של Firebase

        progressBar.setVisibility(View.VISIBLE);
        btnSaveProfile.setEnabled(false);

        // עדכון הנתונים במסד הנתונים
        databaseService.updateUser(currentUser, new DatabaseService.DatabaseCallback<Void>() {
            /**
             * מבוצע לאחר עדכון הפרופיל בהצלחה.
             * @param object פרמטר ריק.
             */
            @Override
            public void onCompleted(Void object) {
                progressBar.setVisibility(View.GONE);
                btnSaveProfile.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "הפרופיל עודכן בהצלחה", Toast.LENGTH_SHORT).show();
                finish();
            }

            /**
             * מבוצע במקרה של שגיאה בעדכון הפרופיל.
             * @param e השגיאה שהתרחשה.
             */
            @Override
            public void onFailed(Exception e) {
                progressBar.setVisibility(View.GONE);
                btnSaveProfile.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "שגיאה בעדכון הפרופיל: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}