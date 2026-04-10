package com.example.personalfinanceapp_0732;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Transaction.class, Goal.class, Notification.class, Budget.class}, version = 4, exportSchema = false)
public abstract class TransactionDB extends RoomDatabase {

    public abstract TransactionDao transactionDao();
    public abstract GoalDao goalDao();
    public abstract NotificationDao notificationDao();
    public abstract BudgetDao budgetDao();

    private static volatile TransactionDB INSTANCE;

    public static TransactionDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TransactionDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TransactionDB.class, "transaction_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}