package com.example.cryptochime.SetAlertActivity.AlertDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlertDao {

    @Query("SELECT * FROM alert")
    List<Alert> getAllAlerts();

    @Insert
    void insertAlert(Alert... alerts);


    @Delete
    void deleteAlert(Alert alert);
}
