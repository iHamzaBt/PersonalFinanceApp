package com.example.personalfinanceapp_0732;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    void insert(Notification notification);

    @Query("SELECT * FROM notification_table ORDER BY timestamp DESC")
    LiveData<List<Notification>> getAllNotifications();

    @Query("DELETE FROM notification_table")
    void deleteAll();
}