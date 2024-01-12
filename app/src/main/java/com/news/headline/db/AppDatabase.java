package com.news.headline.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.news.headline.db.entities.UserEntity;
import com.news.headline.db.dao.UserDao;

@Database(entities = {UserEntity.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

}
