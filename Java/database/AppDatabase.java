package com.example.emotionaldiary.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {User.class, Diary.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // DAO
    public abstract UserDao userDao();
    public abstract DiaryDao diaryDao();

    // Singleton Instance
    private static AppDatabase INSTANCE;

    // Database Instance
    public static synchronized AppDatabase getDatabase(Context context) {

        if (INSTANCE == null) {

            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "emotional_diary_db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return INSTANCE;
    }
}