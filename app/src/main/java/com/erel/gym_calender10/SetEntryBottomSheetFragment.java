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

public class SetEntryBottomSheetFragment extends BottomSheetDialogFragment {

    private float weight;
    private int reps;
    private String exerciseId;
    private OnEntryConfirmedListener listener;

    public interface OnEntryConfirmedListener {
        void onEntryConfirmed(String exerciseId, float weight, int reps);
    }

    public static SetEntryBottomSheetFragment newInstance(String exerciseId, float currentWeight, int currentReps) {
        SetEntryBottomSheetFragment fragment = new SetEntryBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString("exerciseId", exerciseId);
        args.putFloat("weight", currentWeight);
        args.putInt("reps", currentReps);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnEntryConfirmedListener(OnEntryConfirmedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_set_entry_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            exerciseId = getArguments().getString("exerciseId");
            weight = getArguments().getFloat("weight");
            reps = getArguments().getInt("reps");
        }

        TextView tvWeightValue = view.findViewById(R.id.tvWeightValue);
        TextView tvRepsValue = view.findViewById(R.id.tvRepsValue);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        updateUI(tvWeightValue, tvRepsValue);

        view.findViewById(R.id.btnWeightMinusLarge).setOnClickListener(v -> { weight = Math.max(0, weight - 5); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnWeightMinusSmall).setOnClickListener(v -> { weight = Math.max(0, weight - 2.5f); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnWeightPlusSmall).setOnClickListener(v -> { weight += 2.5f; updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnWeightPlusLarge).setOnClickListener(v -> { weight += 5; updateUI(tvWeightValue, tvRepsValue); });

        view.findViewById(R.id.btnRepsMinusLarge).setOnClickListener(v -> { reps = Math.max(0, reps - 5); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnRepsMinusSmall).setOnClickListener(v -> { reps = Math.max(0, reps - 1); updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnRepsPlusSmall).setOnClickListener(v -> { reps += 1; updateUI(tvWeightValue, tvRepsValue); });
        view.findViewById(R.id.btnRepsPlusLarge).setOnClickListener(v -> { reps += 5; updateUI(tvWeightValue, tvRepsValue); });

        btnConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEntryConfirmed(exerciseId, weight, reps);
            }
            dismiss();
        });
    }

    private void updateUI(TextView tvWeight, TextView tvReps) {
        tvWeight.setText(String.format(Locale.getDefault(), "%.1f", weight));
        tvReps.setText(String.valueOf(reps));
    }
}