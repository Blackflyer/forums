package com.erel.gym_calender10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.adapters.UsersAdapter;
import com.erel.gym_calender10.module.User;
import com.erel.gym_calender10.services.DatabaseService;

import java.util.List;

public class Users_list extends AppCompatActivity {

    private static final String TAG = "UsersListActivity";
    private UsersAdapter userAdapter;
    private DatabaseService databaseService;
    private RecyclerView usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_list);

        setupSystemBars();
        initViews();
        setupRecyclerView();
    }

    private void setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        databaseService = DatabaseService.getInstance();
        usersList = findViewById(R.id.rcUsers);
    }

    private void setupRecyclerView() {
        usersList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UsersAdapter(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Log.d(TAG, "User clicked: " + user.getFname());
                // כאן תוכל להוסיף מעבר לפרופיל המשתמש
            }

            @Override
            public void onLongUserClick(User user) {
                // לדוגמה: אפשרות למחוק משתמש בלחיצה ארוכה
                Toast.makeText(Users_list.this, "עריכת משתמש: " + user.getFname(), Toast.LENGTH_SHORT).show();
            }
        });

        usersList.setAdapter(userAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        databaseService.getUserList(new DatabaseService.DatabaseCallback<List<User>>() {
            @Override
            public void onCompleted(List<User> users) {
                if (users != null && !users.isEmpty()) {
                    userAdapter.setUserList(users);
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(Users_list.this, "לא נמצאו משתמשים", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
                Toast.makeText(Users_list.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}