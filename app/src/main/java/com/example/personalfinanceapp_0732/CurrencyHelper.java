package com.example.personalfinanceapp_0732;

import android.content.Context;
import android.content.SharedPreferences;

public class CurrencyHelper {

    public static String getSymbol(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        String currency = prefs.getString("currency", "USD $");
        String[] parts = currency.split(" ");
        return parts.length > 1 ? parts[1] : "$";
    }

    public static String format(Context context, double amount) {
        return String.format(java.util.Locale.US, "%s%.2f", getSymbol(context), amount);
    }

    public static String formatWithSign(Context context, double amount, boolean isIncome) {
        return (isIncome ? "+" : "-") + format(context, Math.abs(amount));
    }
}