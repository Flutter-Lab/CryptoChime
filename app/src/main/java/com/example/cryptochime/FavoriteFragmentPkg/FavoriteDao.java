package com.example.cryptochime.FavoriteFragmentPkg;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM Favorite")
    List<Favorite> getAllFavorites();

    @Insert
    void insertFavorite(Favorite... favorites);


    @Delete
    void deleteFavorite(Favorite favorite);

    @Update
    void updateFavorite(Favorite favorite);

    @Query("DELETE FROM favorite WHERE favoriteID = :favID")
    void deleteByFavId(long favID);

    @Query("DELETE FROM favorite WHERE currency_symbol = :symbol")
    void deleteBySymbol(String symbol);
}
