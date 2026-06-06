package com.erel.gym_calender10.services;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.erel.gym_calender10.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationHelper {

    public static final String CHANNEL_ID = "gym_calender_channel";
    private static final String CHANNEL_NAME = "Workout Reminders";
    private static final String CHANNEL_DESC = "Notifications for upcoming workouts";

    /**
     * יוצרת ערוץ התראות (Notification Channel) עבור האפליקציה (נדרש עבור אנדרואיד 8.0 ומעלה).
     * @param context ההקשר של האפליקציה.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * קובעת התראות עבור אימון מתוכנן: התראה בזמן האימון ותזכורת 24 שעות לפני.
     * @param context ההקשר של האפליקציה.
     * @param planName שם תוכנית האימון.
     * @param dateStr תאריך האימון (בפורמט d/M/yyyy).
     * @param timeStr שעת האימון (בפורמט HH:mm).
     */
    public static void scheduleWorkoutNotifications(Context context, String planName, String dateStr, String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy HH:mm", Locale.getDefault());
        try {
            Date workoutDate = sdf.parse(dateStr + " " + timeStr);
            if (workoutDate == null) return;

            long workoutTimeMillis = workoutDate.getTime();
            long currentTimeMillis = System.currentTimeMillis();

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            // 1. Schedule exact time notification
            if (workoutTimeMillis > currentTimeMillis) {
                Intent exactIntent = new Intent(context, NotificationReceiver.class);
                exactIntent.putExtra("title", "זמן אימון!");
                exactIntent.putExtra("message", "הגיע הזמן להתחיל את תוכנית: " + planName);
                
                int exactRequestCode = (planName + dateStr + timeStr + "exact").hashCode();
                PendingIntent exactPendingIntent = PendingIntent.getBroadcast(
                        context,
                        exactRequestCode,
                        exactIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, workoutTimeMillis, exactPendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, workoutTimeMillis, exactPendingIntent);
                }
            }

            // 2. Schedule 24h before notification
            long reminderTimeMillis = workoutTimeMillis - (24 * 60 * 60 * 1000L); // 24 hours before
            if (reminderTimeMillis > currentTimeMillis) {
                Intent reminderIntent = new Intent(context, NotificationReceiver.class);
                reminderIntent.putExtra("title", "תזכורת לאימון מחר");
                reminderIntent.putExtra("message", "יש לך אימון מתוכנן מחר: " + planName + " בשעה " + timeStr);

                int reminderRequestCode = (planName + dateStr + timeStr + "reminder").hashCode();
                PendingIntent reminderPendingIntent = PendingIntent.getBroadcast(
                        context,
                        reminderRequestCode,
                        reminderIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTimeMillis, reminderPendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTimeMillis, reminderPendingIntent);
                }
            }

        } catch (ParseException e) {
            Log.e("NotificationHelper", "Error parsing date/time: " + e.getMessage());
        }
    }
}
