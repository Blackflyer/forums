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





    private DatabaseService databaseService;


    /// Activity result launcher for capturing image from camera




    // constant to compare
    // the activity result code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);



        InitViews();

        /// request permission for the camera and storage
        ImageUtil.requestPermission(this);

        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();






        btnBackpage = findViewById(R.id.btnBack);

        btnBackpage.setOnClickListener(v -> {
            Intent intent = new Intent(AddExercise.this, AdminPage.class);
            startActivity(intent);
            finish();
        });





        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ExerciseName = etExerciseName.getText().toString();
                String Description = etDescription.getText().toString();
                String Equipment = etEquipment.getText().toString();
                String MuscleGroup = etMuscleGroup.getText().toString();
                String sets = etSets.getText().toString();
                String times = etTimes.getText().toString();



                if (ExerciseName.isEmpty() || Description.isEmpty() || Equipment.isEmpty() ||
                        MuscleGroup.isEmpty() || sets.isEmpty() || times.isEmpty()) {
                    Toast.makeText(AddExercise.this, "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddExercise.this, "התרגיל נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                }

                /// generate a new id for the item
                String id = databaseService.generateExerciseId();


                Exercise newExercise = new Exercise(id, ExerciseName , Equipment, MuscleGroup, Description, sets,times);

                /// save the item to the database and get the result in the callback
                databaseService.createNewExercise(newExercise, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        Log.d("TAG", "Item added successfully");
                        Toast.makeText(AddExercise.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                        /// clear the input fields after adding the item for the next item
                        Log.d("TAG", "Clearing input fields");

                        Intent intent = new Intent(AddExercise.this, AdminPage.class);
                        startActivity(intent);


                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.e("TAG", "Failed to add item", e);
                        Toast.makeText(AddExercise.this, "Failed to add Exercise", Toast.LENGTH_SHORT).show();
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
