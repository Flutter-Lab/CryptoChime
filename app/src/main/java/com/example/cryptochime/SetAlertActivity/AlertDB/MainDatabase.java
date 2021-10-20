package com.example.cryptochime.SetAlertActivity.AlertDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cryptochime.FavoriteFragmentPkg.Favorite;
import com.example.cryptochime.FavoriteFragmentPkg.FavoriteDao;

@Database(entities = {Alert.class, Favorite.class}, version = 1)
public abstract class MainDatabase extends RoomDatabase {

    public abstract AlertDao alertDao();
    public abstract FavoriteDao favoriteDao();

    private static MainDatabase INSTANCE;

    public static MainDatabase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MainDatabase.class, "DB_NAME")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }


}
