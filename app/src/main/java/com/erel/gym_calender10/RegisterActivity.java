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

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    public static final String MyPREFERENCES = "myPrefs";

    private EditText etEmail, etPassword, etFName, etLName, etPhone;
    private Button btnRegister; 
    private TextView tvLogin;

    private DatabaseService databaseService;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        if (btnRegister != null) btnRegister.setOnClickListener(this);
        if (tvLogin != null) tvLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnRegister) {
            validateAndRegister();
        } else if (id == R.id.btnToLogin) {
            finish();
        }
    }


    private void validateAndRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fName = etFName.getText().toString().trim();
        String lName = etLName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || fName.isEmpty() || lName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "חובה למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (etEmail != null) etEmail.setError("אימייל לא תקין");
            return;
        }

        if (password.length() < 6) {
            if (etPassword != null) etPassword.setError("הסיסמה חייבת להכיל לפחות 6 תווים");
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("רושם משתמש...");

        User newUser = new User("", fName, lName, phone, email, password);
        createUserInDatabase(newUser);
    }

    private void createUserInDatabase(User user) {
        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                user.setId(uid);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("email", user.getEmail());
                editor.putString("uid", uid);
                editor.putBoolean("isAdmin", false); 
                editor.apply();

                Toast.makeText(getApplicationContext(), "נרשמת בהצלחה!", Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(RegisterActivity.this, UserDashboardActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                if (btnRegister != null) {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("הרשמה");
                }
                Toast.makeText(RegisterActivity.this, "שגיאה ברישום: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
