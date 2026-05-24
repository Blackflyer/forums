package com.erel.gym_calender10;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * מחלקת STP (ייתכן כקיצור ל-Setup או מסך בדיקה) היא אקטיביטי המדגימה שימוש בתצוגת Edge-to-Edge.
 * המחלקה דואגת להתאמת התוכן של האפליקציה כך שלא יוסתר על ידי סרגלי המערכת.
 */
public class STP extends AppCompatActivity {

    /**
     * פעולה המופעלת בעת יצירת האקטיביטי.
     * @param savedInstanceState מצב המערכת השמור.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // הפעלת מצב תצוגה מקצה לקצה
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stp);
        
        // הגדרת מאזין לשינויים ב-WindowInsets כדי להתאים את הריפוד של התצוגה לסרגלי המערכת
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // התאמת הריפוד של האלמנט הראשי כך שלא ייכנס מתחת לסרגלי הניווט והסטטוס
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
