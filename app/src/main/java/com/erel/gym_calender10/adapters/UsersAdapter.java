package com.erel.gym_calender10.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.erel.gym_calender10.R;
import com.erel.gym_calender10.module.User;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public interface OnUserClickListener {
        void onUserClick(User user);
        void onLongUserClick(User user);
    }

    private final List<User> userList;
    private List<User> userListFull;
    private final OnUserClickListener onUserClickListener;

    /**
     * בנאי למתאם המשתמשים.
     * @param onUserClickListener מאזין ללחיצות על משתמשים.
     */
    public UsersAdapter(@Nullable final OnUserClickListener onUserClickListener) {
        this.userList = new ArrayList<>();
        this.userListFull = new ArrayList<>();
        this.onUserClickListener = onUserClickListener;
    }

    /**
     * יוצר ViewHolder עבור פריט משתמש ברשימה.
     */
    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    /**
     * מקשר את נתוני המשתמש לתצוגה ומגדיר מאזיני לחיצה קצרה וארוכה.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null) return;

        holder.tvName.setText(user.getFname() + " " + user.getLname());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());

        // Set initials
        String initials = "";
        if (user.getFname() != null && !user.getFname().isEmpty()) {
            initials += user.getFname().charAt(0);
        }
        if (user.getLname() != null && !user.getLname().isEmpty()) {
            initials += user.getLname().charAt(0);
        }
        holder.tvInitials.setText(initials.toUpperCase());

        // Show admin chip if user is admin
        if (user.getAdmin() != null && user.getAdmin()) {
            holder.chipRole.setVisibility(View.VISIBLE);
            holder.chipRole.setText("Admin");
        } else {
            holder.chipRole.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onUserClickListener != null) {
                onUserClickListener.onLongUserClick(user);
            }
            return true;
        });
    }

    /**
     * מחזיר את מספר המשתמשים המוצגים ברשימה.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * מעדכן את רשימת המשתמשים ומרענן את התצוגה.
     * @param users רשימת המשתמשים החדשה.
     */
    public void setUserList(List<User> users) {
        userList.clear();
        userList.addAll(users);
        userListFull = new ArrayList<>(users); // <--- הוספנו: שומר עותק מלא של הנתונים בכל טעינה
        notifyDataSetChanged();
    }

    /**
     * מוסיף משתמש בודד לרשימה.
     * @param user המשתמש להוספה.
     */
    public void addUser(User user) {
        userList.add(user);
        userListFull.add(user); // גיבוי
        notifyItemInserted(userList.size() - 1);
    }

    /**
     * מעדכן נתונים של משתמש קיים ברשימה.
     * @param user המשתמש עם הנתונים המעודכנים.
     */
    public void updateUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.set(index, user);

        // עדכון גם ברשימה המלאה
        int fullIndex = userListFull.indexOf(user);
        if (fullIndex != -1) {
            userListFull.set(fullIndex, user);
        }
        notifyItemChanged(index);
    }

    /**
     * מסיר משתמש מהרשימה.
     * @param user המשתמש להסרה.
     */
    public void removeUser(User user) {
        int index = userList.indexOf(user);
        if (index == -1) return;
        userList.remove(index);
        userListFull.remove(user); // מחיקה מהגיבוי
        notifyItemRemoved(index);
    }

    /**
     * מסננת את רשימת המשתמשים לפי מחרוזת חיפוש (בשם או באימייל).
     * @param text מחרוזת החיפוש.
     */
    public void filter(String text) {
        userList.clear();
        if (text == null || text.isEmpty()) {
            // אם שורת החיפוש ריקה, מציגים את כולם
            userList.addAll(userListFull);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (User item : userListFull) {
                // בדיקה אם הטקסט נמצא באימייל או בשם הפרטי/משפחה
                boolean matchesEmail = item.getEmail() != null && item.getEmail().toLowerCase().contains(filterPattern);
                boolean matchesFName = item.getFname() != null && item.getFname().toLowerCase().contains(filterPattern);
                boolean matchesLName = item.getLname() != null && item.getLname().toLowerCase().contains(filterPattern);

                if (matchesEmail || matchesFName || matchesLName) {
                    userList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
    // -----------------------------------

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvPhone, tvInitials;
        Chip chipRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_user_name);
            tvEmail = itemView.findViewById(R.id.tv_item_user_email);
            tvPhone = itemView.findViewById(R.id.tv_item_user_phone);
            tvInitials = itemView.findViewById(R.id.tv_user_initials);
            chipRole = itemView.findViewById(R.id.chip_user_role);
        }
    }
}