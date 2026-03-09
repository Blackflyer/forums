package com.erel.gym_calender10;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    private EditText etSearchUser; // שורת החיפוש

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
        etSearchUser = findViewById(R.id.etSearchUser); // חיבור ה-EditText מה-XML

        // הוספת מאזין לשורת החיפוש
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // קריאה לפונקציית הסינון באדפטר בכל פעם שמוקלדת אות
                if (userAdapter != null) {
                    userAdapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        usersList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UsersAdapter(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Log.d(TAG, "User clicked: " + user.getFname());
                // כאן תוכל להוסיף מעבר לפרופיל המשתמש כדי לראות את האימונים שלו
                Toast.makeText(Users_list.this, "נבחר: " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongUserClick(User user) {
                // לחיצה ארוכה - פותחת חלון אישור מחיקה
                showDeleteConfirmationDialog(user);
            }
        });

        usersList.setAdapter(userAdapter);
    }

    // פונקציה להצגת דיאלוג "האם אתה בטוח?"
    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת מתאמן")
                .setMessage("האם אתה בטוח שברצונך למחוק את המתאמן " + user.getEmail() + "?\nפעולה זו אינה ניתנת לביטול.")
                .setPositiveButton("כן, מחק", (dialog, which) -> {
                    deleteUserFromDatabase(user);
                })
                .setNegativeButton("ביטול", null) // סוגר את הדיאלוג ללא פעולה
                .show();
    }

    // פונקציה למחיקת המשתמש מהדאטה-בייס
    private void deleteUserFromDatabase(User user) {
        databaseService.deleteUser(user.getId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Users_list.this, "משתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                loadUsers(); // טוען מחדש את הרשימה כדי להעלים את המשתמש שנמחק
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "שגיאה במחיקת משתמש", e);
                Toast.makeText(Users_list.this, "שגיאה במחיקת משתמש", Toast.LENGTH_SHORT).show();
            }
        });
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