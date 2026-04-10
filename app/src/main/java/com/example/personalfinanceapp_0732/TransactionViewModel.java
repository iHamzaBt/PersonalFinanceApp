package com.example.personalfinanceapp_0732;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class TransactionViewModel extends AndroidViewModel {

    private TransactionRepository repository;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<List<Goal>> allGoals;
    private LiveData<List<Notification>> allNotifications;
    private LiveData<List<Budget>> allBudgets;

    public TransactionViewModel(@NonNull Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
        allGoals = repository.getAllGoals();
        allNotifications = repository.getAllNotifications();
        allBudgets = repository.getAllBudgets();
    }

    public LiveData<List<Transaction>> getAllTransactions() { return allTransactions; }
    public void insert(Transaction transaction) { repository.insert(transaction); }
    public void update(Transaction transaction) { repository.update(transaction); }
    public void delete(Transaction transaction) { repository.delete(transaction); }
    public void deleteAllTransactions() { repository.deleteAllTransactions(); }

    public LiveData<List<Goal>> getAllGoals() { return allGoals; }
    public void insertGoal(Goal goal) { repository.insertGoal(goal); }
    public void updateGoal(Goal goal) { repository.updateGoal(goal); }
    public void deleteAllGoals() { repository.deleteAllGoals(); }

    public LiveData<List<Notification>> getAllNotifications() { return allNotifications; }
    public void insertNotification(Notification notification) { repository.insertNotification(notification); }
    public void deleteAllNotifications() { repository.deleteAllNotifications(); }

    public LiveData<List<Budget>> getAllBudgets() { return allBudgets; }
    public void insertBudget(Budget budget) { repository.insertBudget(budget); }
    public void updateBudget(Budget budget) { repository.updateBudget(budget); }
    public void deleteBudget(Budget budget) { repository.deleteBudget(budget); }
    public void deleteAllBudgets() { repository.deleteAllBudgets(); }

    public void deleteNotification(Notification notification) {
        repository.deleteNotification(notification);
    }

}