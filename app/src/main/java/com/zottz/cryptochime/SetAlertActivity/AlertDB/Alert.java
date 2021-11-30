package com.zottz.cryptochime.SetAlertActivity.AlertDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Alert {

    @PrimaryKey(autoGenerate = true)
    public int alertId;

    @ColumnInfo(name = "currency_symbol")
    public String currencySymbol;

    @ColumnInfo(name = "alert_type")
    public String alertType;

    @ColumnInfo(name = "alert_value")
    public float alertValue;

    @ColumnInfo(name="alert_type_code")
    public int alertTypeCode;

    @ColumnInfo(name = "loud_alert")
    public boolean isLoudAlert;

    @ColumnInfo(name = "isNotified")
    public boolean isNotified;


}
