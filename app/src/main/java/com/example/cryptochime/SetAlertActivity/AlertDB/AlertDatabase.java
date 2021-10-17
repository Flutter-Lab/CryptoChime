package com.example.cryptochime.SetAlertActivity.AlertDB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Alert.class}, version = 1)
public abstract class AlertDatabase extends RoomDatabase {

    public abstract AlertDao alertDao();

    private static AlertDatabase INSTANCE;

    public static AlertDatabase getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AlertDatabase.class, "DB_NAME")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }


}
