package com.example.cryptochime.FavoriteFragmentPkg;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    public int favoriteID;

    @ColumnInfo(name = "currency_symbol")
    public String currencySymbol;

    @ColumnInfo(name = "currency_name")
    public String currencyName;

    @ColumnInfo(name = "currency_price")
    public float currencyPrice;

    @ColumnInfo(name = "currency_icon_url")
    public String currencyIconURL;





}
