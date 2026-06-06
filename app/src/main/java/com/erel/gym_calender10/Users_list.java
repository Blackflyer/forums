package com.erel.gym_calender10;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etSearchUser;

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users_list);

        setupSystemBars();
        initViews();
        setupRecyclerView();
    }

    /**
     * מגדירה את שולי המערכת (סטטוס בר וניווט) כדי שהתוכן לא יוסתר.
     */
    private void setupSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * מאתחלת את רכיבי הממשק, שירות מסד הנתונים ומגדירה מאזין לחיפוש משתמשים.
     */
    private void initViews() {
        databaseService = DatabaseService.getInstance();
        usersList = findViewById(R.id.rcUsers);
        etSearchUser = findViewById(R.id.etSearchUser);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // הוספת מאזין לשורת החיפוש לסינון הרשימה בזמן אמת
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
    }

    /**
     * מגדירה את ה-RecyclerView להצגת רשימת המשתמשים עם מאזיני לחיצה.
     */
    private void setupRecyclerView() {
        usersList.setLayoutManager(new LinearLayoutManager(this));

        userAdapter = new UsersAdapter(new UsersAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                Log.d(TAG, "User clicked: " + user.getFname());
                Toast.makeText(Users_list.this, "נבחר: " + user.getEmail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongUserClick(User user) {
                showDeleteConfirmationDialog(user);
            }
        });

        usersList.setAdapter(userAdapter);
    }

    /**
     * מציגה דיאלוג אישור לפני מחיקת משתמש מהמערכת.
     * @param user המשתמש אותו רוצים למחוק.
     */
    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת מתאמן")
                .setMessage("האם אתה בטוח שברצונך למחוק את המתאמן " + user.getEmail() + "?\nפעולה זו אינה ניתנת לביטול.")
                .setPositiveButton("כן, מחק", (dialog, which) -> {
                    deleteUserFromDatabase(user);
                })
                .setNegativeButton("ביטול", null)
                .show();
    }

    /**
     * מוחקת את המשתמש ממסד הנתונים ומעדכנת את הרשימה.
     * @param user המשתמש למחיקה.
     */
    private void deleteUserFromDatabase(User user) {
        databaseService.deleteUser(user.getId(), new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Users_list.this, "משתמש נמחק בהצלחה", Toast.LENGTH_SHORT).show();
                loadUsers();
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "שגיאה במחיקת משתמש", e);
                Toast.makeText(Users_list.this, "שגיאה במחיקת משתמש", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * מופעלת כאשר האקטיביטי חוזרת לקדמת הבמה. טוענת את המשתמשים מחדש.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    /**
     * טוענת את רשימת המשתמשים ממסד הנתונים ומעדכנת את ה-Adapter.
     */
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