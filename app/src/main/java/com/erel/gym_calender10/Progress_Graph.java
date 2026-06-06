package com.erel.gym_calender10;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * מחלקת Progress_Graph מציגה את התקדמות המשתמש באמצעות גרפים ויזואליים.
 * המחלקה כוללת גרף קווי המציג שינוי ב-1RM (משקל מקסימלי לחזרה אחת) לאורך זמן,
 * וגרף עוגה המציג את התפלגות נפח האימונים לפי קבוצות שריר.
 */
public class Progress_Graph extends AppCompatActivity {

    private Spinner spinnerExercises;
    private LineChart lineChart;
    private PieChart pieChart;
    private List<Exercise> exerciseList;

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_graph);

        spinnerExercises = findViewById(R.id.spinnerExercises);
        lineChart = findViewById(R.id.lineChartProgress);
        pieChart = findViewById(R.id.pieChartProgress);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // הגדרת מראה הגרפים
        setupChartAppearance();
        // טעינת התרגילים לבחירה
        loadExercisesIntoSpinner();
    }

    /**
     * הגדרת המראה והמאפיינים הטכניים של הגרפים (LineChart ו-PieChart).
     */
    private void setupChartAppearance() {
        // הגדרות גרף קווי
        lineChart.setDrawGridBackground(false);
        Description lineDescription = new Description();
        lineDescription.setText("התקדמות 1RM (ק\"ג)");
        lineChart.setDescription(lineDescription);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        // הגדרות גרף עוגה
        Description pieDescription = new Description();
        pieDescription.setText("נפח אימונים לפי קבוצת שריר");
        pieChart.setDescription(pieDescription);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        
        loadMuscleVolumeData();
    }

    /**
     * טוענת את נתוני נפח האימונים (משקל * חזרות) עבור כל קבוצת שריר.
     * הנתונים נאספים מכל התרגילים שהמשתמש ביצע.
     */
    private void loadMuscleVolumeData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || exerciseList == null) return;

        Map<String, Float> volumeByMuscle = new HashMap<>();
        final int[] fetchedCount = {0};

        for (Exercise ex : exerciseList) {
            DatabaseService.getInstance().getExerciseProgress(user.getUid(), ex.getId(), new DatabaseService.DatabaseCallback<List<ProgressRecord>>() {
                @Override
                public void onCompleted(List<ProgressRecord> records) {
                    if (records != null) {
                        float totalVolume = 0;
                        for (ProgressRecord r : records) {
                            // חישוב נפח: משקל כפול חזרות
                            totalVolume += (r.getWeight() * (r.getReps() > 0 ? r.getReps() : 1));
                        }
                        String muscle = ex.getMuscleGroup();
                        if (muscle == null || muscle.isEmpty()) muscle = "אחר";
                        volumeByMuscle.put(muscle, volumeByMuscle.getOrDefault(muscle, 0f) + totalVolume);
                    }
                    
                    fetchedCount[0]++;
                    // עדכון הגרף רק לאחר שכל נתוני התרגילים נטענו
                    if (fetchedCount[0] == exerciseList.size()) {
                        updatePieChart(volumeByMuscle);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    fetchedCount[0]++;
                    if (fetchedCount[0] == exerciseList.size()) {
                        updatePieChart(volumeByMuscle);
                    }
                }
            });
        }
    }

    /**
     * מעדכנת את גרף העוגה עם נתוני הנפח שחושבו.
     * @param data מפה המקשרת בין שם קבוצת שריר לערך הנפח הכולל.
     */
    private void updatePieChart(Map<String, Float> data) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Float> entry : data.entrySet()) {
            if (entry.getValue() > 0) {
                pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "נפח לפי שריר");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setSliceSpace(3f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); // רענון הגרף
    }

    /**
     * טוענת את רשימת התרגילים הקיימת במסד הנתונים לתוך ה-Spinner.
     * מאפשרת למשתמש לבחור תרגיל לצפייה בגרף ההתקדמות שלו.
     */
    private void loadExercisesIntoSpinner() {
        DatabaseService.getInstance().getExerciseList(new DatabaseService.DatabaseCallback<List<Exercise>>() {
            @Override
            public void onCompleted(List<Exercise> exercises) {
                if (exercises != null && !exercises.isEmpty()) {
                    exerciseList = exercises;
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

                    spinnerExercises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Exercise selectedExercise = exerciseList.get(position);
                            loadGraphDataForExercise(selectedExercise.getId());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                    // טעינת נתוני הנפח לאחר קבלת רשימת התרגילים
                    loadMuscleVolumeData();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Progress_Graph.this, "שגיאה בטעינת תרגילים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * טוענת את נתוני ההתקדמות עבור תרגיל ספציפי ומעדכנת את הגרף הקווי.
     * @param exerciseId מזהה התרגיל שנבחר.
     */
    private void loadGraphDataForExercise(String exerciseId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseService.getInstance().getExerciseProgress(user.getUid(), exerciseId, new DatabaseService.DatabaseCallback<List<ProgressRecord>>() {
            @Override
            public void onCompleted(List<ProgressRecord> records) {
                ArrayList<Entry> lineEntries = new ArrayList<>();

                if (records != null && !records.isEmpty()) {
                    for (int i = 0; i < records.size(); i++) {
                        float weight = records.get(i).getWeight();
                        int reps = records.get(i).getReps();
                        
                        // חישוב 1RM משוער לפי נוסחת Brzycki: weight * (1 + 0.0333 * reps)
                        float estimated1RM = weight * (1 + 0.0333f * reps);

                        lineEntries.add(new Entry(i + 1, estimated1RM));
                    }
                } else {
                    Toast.makeText(Progress_Graph.this, "אין עדיין נתונים לתרגיל זה", Toast.LENGTH_SHORT).show();
                }

                // עדכון והצגת הנתונים בגרף הקווי
                LineDataSet lineDataSet = new LineDataSet(lineEntries, "1RM משוער (ק\"ג)");
                lineDataSet.setColor(Color.parseColor("#2196F3"));
                lineDataSet.setLineWidth(3f);
                lineDataSet.setCircleColor(Color.parseColor("#2196F3"));
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setValueTextSize(10f);

                LineData lineData = new LineData(lineDataSet);
                lineChart.setData(lineData);
                lineChart.invalidate();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Progress_Graph.this, "שגיאה בטעינת נתוני הגרף", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
