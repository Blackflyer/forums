package com.erel.gym_calender10;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.ProgressRecord;
import com.erel.gym_calender10.services.DatabaseService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TrackWorkoutActivity extends AppCompatActivity {

    private String exerciseId;
    private String exerciseName;
    private EditText etActualWeight, etActualReps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_workout);

        // מקבלים את פרטי התרגיל מהמסך הקודם (היכן שהמשתמש לחץ עליו)
        exerciseId = getIntent().getStringExtra("EXERCISE_ID");
        exerciseName = getIntent().getStringExtra("EXERCISE_NAME");

        TextView tvExerciseName = findViewById(R.id.tvExerciseNameTrack);
        tvExerciseName.setText(exerciseName != null ? exerciseName : "תרגיל");

        etActualWeight = findViewById(R.id.etActualWeight);
        etActualReps = findViewById(R.id.etActualReps);
        Button btnSaveProgress = findViewById(R.id.btnSaveProgress);

        btnSaveProgress.setOnClickListener(v -> saveProgress());
    }

    private void saveProgress() {
        String weightStr = etActualWeight.getText().toString();
        String repsStr = etActualReps.getText().toString();

        if (weightStr.isEmpty() || repsStr.isEmpty()) {
            Toast.makeText(this, "אנא הזן משקל וחזרות", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || exerciseId == null) return;

        float weight = Float.parseFloat(weightStr);
        int reps = Integer.parseInt(repsStr);

        // יצירת תאריך נוכחי
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        ProgressRecord record = new ProgressRecord(currentDate, weight, reps);

        DatabaseService.getInstance().saveExerciseProgress(user.getUid(), exerciseId, record, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(TrackWorkoutActivity.this, "הביצוע נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                finish(); // סוגר את המסך וחוזר לאימון
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(TrackWorkoutActivity.this, "שגיאה בשמירת הביצוע", Toast.LENGTH_SHORT).show();
            }
        });
    }
}