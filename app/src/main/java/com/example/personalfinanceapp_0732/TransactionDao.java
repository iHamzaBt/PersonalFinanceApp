package com.example.personalfinanceapp_0732;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transaction_table ORDER BY id DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("DELETE FROM transaction_table")
    void deleteAllTransactions();
}