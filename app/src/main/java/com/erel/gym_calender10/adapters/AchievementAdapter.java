package com.erel.gym_calender10.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
import com.erel.gym_calender10.module.Achievement;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> achievementList;

    /**
     * בנאי למתאם ההישגים.
     * @param achievementList רשימת ההישגים להצגה.
     */
    public AchievementAdapter(List<Achievement> achievementList) {
        this.achievementList = achievementList;
    }

    /**
     * יוצר ViewHolder חדש עבור פריט ברשימה.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    /**
     * מקשר את הנתונים של הישג ספציפי ל-ViewHolder (לתצוגה).
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievementList.get(position);

        holder.tvName.setText(achievement.getName());
        holder.tvDesc.setText(achievement.getDescription());
        holder.tvIcon.setText(achievement.getIcon());

        if (achievement.isUnlocked()) {
            holder.ivLockStatus.setImageResource(android.R.drawable.btn_star_big_on);
            holder.ivLockStatus.setColorFilter(null);
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.ivLockStatus.setImageResource(android.R.drawable.ic_partial_secure);
            holder.itemView.setAlpha(0.6f);
        }
    }

    /**
     * מחזיר את מספר הפריטים ברשימה.
     */
    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvIcon;
        ImageView ivLockStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAchievementName);
            tvDesc = itemView.findViewById(R.id.tvAchievementDesc);
            tvIcon = itemView.findViewById(R.id.tvAchievementIcon);
            ivLockStatus = itemView.findViewById(R.id.ivLockStatus);
        }
    }
}