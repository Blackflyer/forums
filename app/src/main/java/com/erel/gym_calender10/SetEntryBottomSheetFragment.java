package com.erel.gym_calender10;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

/**
 * מחלקת SetEntryBottomSheetFragment היא דיאלוג מסוג BottomSheet המאפשר למשתמש להזין משקל וחזרות.
 * הדיאלוג כולל כפתורי פלוס ומינוס לשינוי מהיר של הערכים.
 */
public class SetEntryBottomSheetFragment extends BottomSheetDialogFragment {

    private float weight;
    private int reps;
    private String exerciseId;
    private OnEntryConfirmedListener listener;

    /**
     * ממשק (Interface) להאזנה לאירוע אישור הנתונים בדיאלוג.
     */
    public interface OnEntryConfirmedListener {
        /**
         * נקרא כאשר המשתמש מאשר את המשקל והחזרות.
         * @param exerciseId מזהה התרגיל.
         * @param weight המשקל שהוזן.
         * @param reps מספר החזרות שהוזנו.
         */
        void onEntryConfirmed(String exerciseId, float weight, int reps);
    }

    /**
     * פעולה סטטית ליצירת מופע חדש של הפרגמנט עם נתוני התרגיל והערכים ההתחלתיים.
     * @param exerciseId מזהה התרגיל.
     * @param currentWeight משקל התחלתי להצגה.
     * @param currentReps מספר חזרות התחלתי להצגה.
     * @return מופע חדש של SetEntryBottomSheetFragment.
     */
    public static SetEntryBottomSheetFragment newInstance(String exerciseId, float currentWeight, int currentReps) {
        SetEntryBottomSheetFragment fragment = new SetEntryBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("exerciseId", exerciseId);
        args.putFloat("weight", currentWeight);
        args.putInt("reps", currentReps);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * מגדירה את המאזין שיקבל את הנתונים לאחר אישור המשתמש.
     * @param listener המאזין למימוש.
     */
    public void setOnEntryConfirmedListener(OnEntryConfirmedListener listener) {
        this.listener = listener;
    }

    /**
     * יוצרת ומחזירה את תצוגת הדיאלוג מה-XML.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_set_entry_bottom_sheet, container, false);
    }

    /**
     * מאתחלת את רכיבי הממשק, טוענת את הנתונים מהארגומנטים ומגדירה מאזינים לכפתורים.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // קבלת נתונים שנשלחו לפרגמנט
        if (getArguments() != null) {
            exerciseId = getArguments().getString("exerciseId");
            weight = getArguments().getFloat("weight");
            reps = getArguments().getInt("reps");
        }

        TextView tvWeightValue = view.findViewById(R.id.tvWeightValue);
        TextView tvRepsValue = view.findViewById(R.id.tvRepsValue);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        // עדכון התצוגה הראשונית
        updateUI(tvWeightValue, tvRepsValue);

        // מאזינים לשינוי משקל
        view.findViewById(R.id.btnWeightMinusLarge).setOnClickListener(v -> { weight = Math.max(0, weight - 5); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnWeightMinusSmall).setOnClickListener(v -> { weight = Math.max(0, weight - 2.5f); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnWeightPlusSmall).setOnClickListener(v -> { weight += 2.5f; updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnWeightPlusLarge).setOnClickListener(v -> { weight += 5; updateUI(tvWeightValue, tvRepsValue); });

        // מאזינים לשינוי מספר חזרות
        view.findViewById(R.id.btnRepsMinusLarge).setOnClickListener(v -> { reps = Math.max(0, reps - 5); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnRepsMinusSmall).setOnClickListener(v -> { reps = Math.max(0, reps - 1); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnRepsPlusSmall).setOnClickListener(v -> { reps += 1; updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnRepsPlusLarge).setOnClickListener(v -> { reps += 5; updateUI(tvWeightValue, tvRepsValue); });

        // כפתור אישור ושליחת הנתונים למאזין
        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEntryConfirmed(exerciseId, weight, reps);
            }
            dismiss(); // סגירת הדיאלוג
        });
    }

    /**
     * מעדכנת את הטקסט המוצג בתיבות המשקל והחזרות לפי הערכים הנוכחיים.
     * @param tvWeight שדה הטקסט של המשקל.
     * @param tvReps שדה הטקסט של החזרות.
     */
    private void updateUI(TextView tvWeight, TextView tvReps) {
        tvWeight.setText(String.format(Locale.getDefault(), "%.1f", weight));
        tvReps.setText(String.valueOf(reps));
    }
}
