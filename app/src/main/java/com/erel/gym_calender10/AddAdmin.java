package com.erel.gym_calender10;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.UsersAdapter;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;

import java.util.List;

public class AddAdmin extends AppCompatActivity {

    private EditText etSearchUser;
    private RecyclerView rcUsers;
    private Button btnPromoteAdmin, btnBackFromAdmin;
    private DatabaseService databaseService;
    private UsersAdapter userAdapter;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);

        databaseService = DatabaseService.getInstance();

        etSearchUser = findViewById(R.id.etSearchUser);
        rcUsers = findViewById(R.id.rcUsers);
        btnPromoteAdmin = findViewById(R.id.btnPromoteAdmin);
        btnBackFromAdmin = findViewById(R.id.btnBackFromAdmin);

        btnBackFromAdmin.setOnClickListener(v -> finish());

        setupRecyclerView();
        loadUsers();

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (userAdapter != null) {
                    userAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnPromoteAdmin.setOnClickListener(v -> {
            if (selectedUser != null) {
                promoteUserToAdmin(selectedUser);
            }
        });
    }

    private void setupRecyclerView() {
        rcUsers.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UsersAdapter(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                selectedUser = user;
                btnPromoteAdmin.setVisibility(View.VISIBLE);
                btnPromoteAdmin.setText("הפוך את " + user.getFname() + " למנהל");
            }

            @Override
            public void onLongUserClick(User user) {
                // לא רלוונטי כאן
            }
        });
        rcUsers.setAdapter(userAdapter);
    }

    private void loadUsers() {
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    userAdapter.setUserList(users);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("AddAdmin", "Failed to load users", e);
                Toast.makeText(AddAdmin.this, "שגיאה בטעינת משתמשים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promoteUserToAdmin(User user) {
        databaseService.updateUserAdminStatus(user.getId(), true, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void v) {
                Toast.makeText(AddAdmin.this, user.getFname() + " קודם למנהל בהצלחה!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e("AddAdmin", "Failed to promote user", e);
                Toast.makeText(AddAdmin.this, "שגיאה בקידום המשתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }
}