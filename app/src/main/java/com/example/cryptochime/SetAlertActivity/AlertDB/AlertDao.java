package com.example.cryptochime.SetAlertActivity.AlertDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlertDao {

    @Query("SELECT * FROM Alert")
    List<Alert> getAllAlerts();

    @Insert
    void insertAlert(Alert... alerts);


    @Delete
    void deleteAlert(Alert alert);

    @Update
    void updateAlert(Alert alert);

    @Query("UPDATE alert SET isNotified = :isNotified WHERE currency_symbol = :currencySymbol AND alert_value = :alertValue ")
    void updateField(boolean isNotified, String currencySymbol, float alertValue);

    @Query("DELETE FROM alert WHERE currency_symbol = :symbol")
    void deleteBySymbol(String symbol);

}
