package com.example.cryptochime.FavoriteFragmentPkg;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    public int alertId;

    @ColumnInfo(name = "currency_name")
    public String currencyName;

}
