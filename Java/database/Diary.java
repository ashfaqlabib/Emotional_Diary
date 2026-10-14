package com.example.emotionaldiary.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "diary")
public class Diary {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String title;

    @NonNull
    public String content;

    @NonNull
    public String mood;

    @NonNull
    public String date;

    @NonNull
    public String time;

    public Diary(@NonNull String title,
                 @NonNull String content,
                 @NonNull String mood,
                 @NonNull String date,
                 @NonNull String time) {

        this.title = title;
        this.content = content;
        this.mood = mood;
        this.date = date;
        this.time = time;
    }
    public void setId(int id) {
        this.id = id;
    }
}