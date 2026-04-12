package com.erel.gym_calender10;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.view.View;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.erel.gym_calender10.R;
import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.services.DatabaseService;
import com.erel.gym_calender10.ImageUtil.ImageUtil;

public class AddExercise extends AppCompatActivity {


    private EditText etExerciseName, etDescription, etEquipment, etMuscleGroup,etSets,etTimes;

    private Button  btnAddExercise, btnBackpage;

    private String editingExerciseId = null;

    private DatabaseService databaseService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);



        InitViews();

        /// request permission for the camera and storage
        ImageUtil.requestPermission(this);

        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();


        // Check if we are in edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("EXERCISE_ID")) {
            editingExerciseId = intent.getStringExtra("EXERCISE_ID");
            etExerciseName.setText(intent.getStringExtra("EXERCISE_NAME"));
            etDescription.setText(intent.getStringExtra("EXERCISE_DESCRIPTION"));
            etEquipment.setText(intent.getStringExtra("EXERCISE_EQUIPMENT"));
            etMuscleGroup.setText(intent.getStringExtra("EXERCISE_MUSCLE_GROUP"));
            etSets.setText(intent.getStringExtra("EXERCISE_SETS"));
            etTimes.setText(intent.getStringExtra("EXERCISE_TIMES"));

            btnAddExercise.setText("עדכן תרגיל");
        }


        btnBackpage = findViewById(R.id.btnBack);

        btnBackpage.setOnClickListener(v -> {
            finish();
        });





        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseName = etExerciseName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String equipment = etEquipment.getText().toString().trim();
                String muscleGroup = etMuscleGroup.getText().toString().trim();
                String setsStr = etSets.getText().toString().trim();
                String timesStr = etTimes.getText().toString().trim();

                // 1. בדיקה שכל השדות מלאים
                if (exerciseName.isEmpty() || description.isEmpty() || equipment.isEmpty() ||
                        muscleGroup.isEmpty() || setsStr.isEmpty() || timesStr.isEmpty()) {
                    Toast.makeText(AddExercise.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                    return; // <<-- חובה להוסיף return כדי לעצור את ההמשך ולא לשמור תרגיל ריק
                }

                // 2. בדיקת תקינות: סטים וחזרות חייבים להיות מספרים חיוביים
                try {
                    int sets = Integer.parseInt(setsStr);
                    int times = Integer.parseInt(timesStr);
                    if (sets <= 0 || times <= 0) {
                        Toast.makeText(AddExercise.this, "סטים וחזרות חייבים להיות מספרים גדולים מאפס", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(AddExercise.this, "אנא הזן מספרים תקינים בשדות הסטים והחזרות", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3. יצירת מזהה ושמירה ל-Firebase
                String id = (editingExerciseId != null) ? editingExerciseId : databaseService.generateExerciseId();
                Exercise newExercise = new Exercise(id, exerciseName, equipment, muscleGroup, description, setsStr, timesStr);

                databaseService.createNewExercise(newExercise, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        String msg = (editingExerciseId != null) ? "התרגיל עודכן בהצלחה!" : "התרגיל נוסף בהצלחה!";
                        Toast.makeText(AddExercise.this, msg, Toast.LENGTH_SHORT).show();

                        // מעבר חזרה למסך הקודם לאחר הצלחה
                        finish();
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("TAG", "Failed to add/update item", e);
                        Toast.makeText(AddExercise.this, "שגיאה בשמירת התרגיל", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void InitViews() {
        etDescription = findViewById(R.id.etDescription);
        etEquipment = findViewById(R.id.etEquipment);
        etExerciseName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etTimes = findViewById(R.id.etTimes);
        etMuscleGroup = findViewById(R.id.etMuscleGroup);

        btnAddExercise = findViewById(R.id.AddExercise);

    }








}
