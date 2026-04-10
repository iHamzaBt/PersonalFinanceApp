package com.example.personalfinanceapp_0732;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class BalanceWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_balance);

        SharedPreferences prefs = context.getSharedPreferences("widget_data", Context.MODE_PRIVATE);
        String balance = prefs.getString("balance", "$0.00");
        String income = prefs.getString("income", "$0.00");
        String expense = prefs.getString("expense", "$0.00");

        views.setTextViewText(R.id.widgetBalance, balance);
        views.setTextViewText(R.id.widgetIncome, income);
        views.setTextViewText(R.id.widgetExpense, expense);

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}