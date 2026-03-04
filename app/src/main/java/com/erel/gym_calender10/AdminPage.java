package com.erel.gym_calender10;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminPage extends AppCompatActivity implements View.OnClickListener {
    Button btnAddExercise, btnAddAdmin, btnUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        btnAddExercise = findViewById(R.id.btnGoAddExercise);
        btnUsersList= findViewById(R.id.btnGoUsersList);
        btnAddAdmin = findViewById(R.id.btnGoAddAdmin);
        btnAddExercise.setOnClickListener(this);
        btnUsersList.setOnClickListener(this);
        btnAddAdmin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnAddExercise.getId()) {
            Intent intent = new Intent(AdminPage.this, AddExercise.class);
            startActivity(intent);
        }
        if (v.getId() == btnAddAdmin.getId()) {
            Intent intent = new Intent(AdminPage.this, LoginActivity.class);
            startActivity(intent);
        }
        if (v.getId() == btnUsersList.getId()) {
            Intent intent = new Intent(AdminPage.this, Users_list.class);
            startActivity(intent);
        }
    }
}

