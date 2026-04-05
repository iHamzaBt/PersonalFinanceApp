package com.example.personalfinanceapp_0732;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {

    private final TransactionDao transactionDao;
    private final GoalDao goalDao;
    private final NotificationDao notificationDao;

    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<List<Goal>> allGoals;
    private final LiveData<List<Notification>> allNotifications;

    private final ExecutorService executorService;

    public TransactionRepository(Application application) {
        TransactionDB database = TransactionDB.getDatabase(application);
        transactionDao = database.transactionDao();
        goalDao = database.goalDao();
        notificationDao = database.notificationDao();

        allTransactions = transactionDao.getAllTransactions();
        allGoals = goalDao.getAllGoals();
        allNotifications = notificationDao.getAllNotifications();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Transaction>> getAllTransactions() { return allTransactions; }
    public void insert(Transaction transaction) { executorService.execute(() -> transactionDao.insert(transaction)); }
    public void update(Transaction transaction) { executorService.execute(() -> transactionDao.update(transaction)); }
    public void delete(Transaction transaction) { executorService.execute(() -> transactionDao.delete(transaction)); }

    public LiveData<List<Goal>> getAllGoals() { return allGoals; }
    public void insertGoal(Goal goal) { executorService.execute(() -> goalDao.insert(goal)); }
    public void updateGoal(Goal goal) { executorService.execute(() -> goalDao.update(goal)); }

    public LiveData<List<Notification>> getAllNotifications() { return allNotifications; }
    public void insertNotification(Notification notification) { executorService.execute(() -> notificationDao.insert(notification)); }
}