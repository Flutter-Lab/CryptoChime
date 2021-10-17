package com.example.cryptochime.SetAlertActivity.AlertDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alert {

    @PrimaryKey(autoGenerate = true)
    public int alertId;

    @ColumnInfo(name = "currency_name")
    public String currencyName;

    @ColumnInfo(name = "alert_type")
    public String alertType;

    @ColumnInfo(name = "alert_value")
    public String alertValue;


}
