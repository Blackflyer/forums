package com.erel.gym_calender10;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * מסך זה מייצג פריט תרגיל שניתן לבחירה מתוך רשימה.
 * משמש בעיקר להצגת המבנה הוויזואלי של תרגיל בודד בתוך רשימת בחירה.
 */
public class Exercise_item_selectable extends AppCompatActivity {

    /**
     * פונקציה זו נקראת בעת יצירת המסך. היא מגדירה את פריסת המסך (Layout)
     * ומטפלת בשוליים של סרגלי המערכת (סטטוס וניווט) לתמיכה בתצוגת מסך מלא.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_item_selectable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}