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
    private final BudgetDao budgetDao;

    private final LiveData<List<Transaction>> allTransactions;
    private final LiveData<List<Goal>> allGoals;
    private final LiveData<List<Notification>> allNotifications;
    private final LiveData<List<Budget>> allBudgets;

    private final ExecutorService executorService;

    public TransactionRepository(Application application) {
        TransactionDB database = TransactionDB.getDatabase(application);
        transactionDao = database.transactionDao();
        goalDao = database.goalDao();
        notificationDao = database.notificationDao();
        budgetDao = database.budgetDao();

        allTransactions = transactionDao.getAllTransactions();
        allGoals = goalDao.getAllGoals();
        allNotifications = notificationDao.getAllNotifications();
        allBudgets = budgetDao.getAllBudgets();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Transaction>> getAllTransactions() { return allTransactions; }
    public void insert(Transaction transaction) { executorService.execute(() -> transactionDao.insert(transaction)); }
    public void update(Transaction transaction) { executorService.execute(() -> transactionDao.update(transaction)); }
    public void delete(Transaction transaction) { executorService.execute(() -> transactionDao.delete(transaction)); }
    public void deleteAllTransactions() { executorService.execute(() -> transactionDao.deleteAllTransactions()); }

    public LiveData<List<Goal>> getAllGoals() { return allGoals; }
    public void insertGoal(Goal goal) { executorService.execute(() -> goalDao.insert(goal)); }
    public void updateGoal(Goal goal) { executorService.execute(() -> goalDao.update(goal)); }
    public void deleteAllGoals() { executorService.execute(() -> goalDao.deleteAll()); }

    public LiveData<List<Notification>> getAllNotifications() { return allNotifications; }
    public void insertNotification(Notification notification) { executorService.execute(() -> notificationDao.insert(notification)); }
    public void deleteAllNotifications() { executorService.execute(() -> notificationDao.deleteAll()); }

    public LiveData<List<Budget>> getAllBudgets() { return allBudgets; }
    public void insertBudget(Budget budget) { executorService.execute(() -> budgetDao.insert(budget)); }
    public void updateBudget(Budget budget) { executorService.execute(() -> budgetDao.update(budget)); }
    public void deleteBudget(Budget budget) { executorService.execute(() -> budgetDao.delete(budget)); }
    public void deleteAllBudgets() { executorService.execute(() -> budgetDao.deleteAll()); }

    public void deleteNotification(Notification notification) {
        executorService.execute(() -> notificationDao.delete(notification));
    }
}