package com.erel.gym_calender10;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.erel.gym_calender10.module.Exercise;
import com.erel.gym_calender10.module.ProgressRecord;
import com.erel.gym_calender10.services.DatabaseService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class Progress_Graph extends AppCompatActivity {

    private Spinner spinnerExercises;
    private LineChart lineChart;
    private List<Exercise> exerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_graph);

        spinnerExercises = findViewById(R.id.spinnerExercises);
        lineChart = findViewById(R.id.lineChartProgress);

        setupChartAppearance();
        loadExercisesIntoSpinner();
    }

    // עיצוב ראשוני של הגרף
    private void setupChartAppearance() {
        lineChart.setDrawGridBackground(false);
        Description description = new Description();
        description.setText("משקל מקסימלי (ק\"ג)");
        lineChart.setDescription(description);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // קפיצות של 1 בציר ה-X
    }

    // טעינת רשימת התרגילים לתוך ה-Spinner
    private void loadExercisesIntoSpinner() {
        DatabaseService.getInstance().getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            @Override
            public void onCompleted(List<Exercise> exercises) {
                if (exercises != null && !exercises.isEmpty()) {
                    exerciseList = exercises;

                    // יצירת רשימה של שמות תרגילים בלבד
                    List<String> exerciseNames = new ArrayList<>();
                    for (Exercise ex : exercises) {
                        exerciseNames.add(ex.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            Progress_Graph.this,
                            android.R.layout.simple_spinner_item,
                            exerciseNames
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerExercises.setAdapter(adapter);

                    // האזנה לבחירת תרגיל
                    spinnerExercises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Exercise selectedExercise = exerciseList.get(position);
                            loadGraphDataForExercise(selectedExercise.getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Progress_Graph.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // טעינת הנתונים מהפיירבייס לתרגיל הספציפי וציור הגרף
    // טעינת הנתונים האמיתיים מהפיירבייס לתרגיל הספציפי וציור הגרף
    private void loadGraphDataForExercise(String exerciseId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseService.getInstance().getExerciseProgress(user.getUid(), exerciseId, new DatabaseService.DatabaseCallback<List<ProgressRecord>>() {
            @Override
            public void onCompleted(List<ProgressRecord> records) {
                ArrayList<Entry> entries = new ArrayList<>();

                if (records != null && !records.isEmpty()) {
                    // יש נתונים! עוברים עליהם ומכניסים לגרף
                    for (int i = 0; i < records.size(); i++) {
                        // ציר ה-X יהיה מספר האימון (1, 2, 3...), וציר ה-Y יהיה המשקל
                        entries.add(new Entry(i + 1, records.get(i).getWeight()));
                    }
                } else {
                    Toast.makeText(Progress_Graph.this, "אין עדיין נתונים לתרגיל זה", Toast.LENGTH_SHORT).show();
                }

                // יצירת הקו של הגרף
                LineDataSet dataSet = new LineDataSet(entries, "התקדמות במשקלים (ק\"ג)");
                dataSet.setColor(Color.parseColor("#2196F3"));
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setValueTextSize(12f);
                dataSet.setLineWidth(3f);
                dataSet.setCircleColor(Color.parseColor("#2196F3"));
                dataSet.setCircleRadius(5f);

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // מצייר את הגרף מחדש
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Progress_Graph.this, "שגיאה בטעינת נתוני הגרף", Toast.LENGTH_SHORT).show();
            }
        });
    }
}