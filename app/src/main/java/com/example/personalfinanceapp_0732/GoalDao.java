package com.example.personalfinanceapp_0732;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface GoalDao {

    @Insert
    void insert(Goal goal);

    @Update
    void update(Goal goal);

    @Query("SELECT * FROM goal_table ORDER BY id DESC")
    LiveData<List<Goal>> getAllGoals();

    @Query("DELETE FROM goal_table")
    void deleteAll();
}