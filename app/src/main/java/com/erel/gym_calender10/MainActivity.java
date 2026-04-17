package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnGoRegister) {
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        } else if (id == R.id.btnGoLogin) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}
