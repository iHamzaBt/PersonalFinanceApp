package com.example.personalfinanceapp_0732;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;

public class NotificationHelper {

    private static final String GOAL_CHANNEL_ID = "goal_completion_channel";
    private static final String BUDGET_CHANNEL_ID = "budget_alerts";

    public static void showGoalCompletedNotifications(Context context, String goalTitle) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    GOAL_CHANNEL_ID,
                    "Goal Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder congratsNotification = new NotificationCompat.Builder(context, GOAL_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo_white_no_bg)
                .setContentTitle("🎉 Goal Achieved!")
                .setContentText("Congratulations! You have fully funded your goal: " + goalTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationCompat.Builder motivationNotification = new NotificationCompat.Builder(context, GOAL_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo_white_no_bg)
                .setContentTitle("🚀 What's Next?")
                .setContentText("A person without goals is like a ship without a Captain. Set your next financial goal now and keep growing!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, congratsNotification.build());

        new android.os.Handler().postDelayed(() -> {
            notificationManager.notify(2, motivationNotification.build());
        }, 5000);
    }

    public static void showBudgetExceededNotification(Context context, String category) {
        SharedPreferences prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        String prefKey = "notified_" + category + "_" + currentMonth;

        if (prefs.getBoolean(prefKey, false)) return;

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(BUDGET_CHANNEL_ID, "Budget Alerts", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, BUDGET_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_logo_white_no_bg)
                .setContentTitle("Budget Exceeded")
                .setContentText("Your " + category + " budget has been exceeded this month!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(category.hashCode(), builder.build());
        prefs.edit().putBoolean(prefKey, true).apply();
    }
}