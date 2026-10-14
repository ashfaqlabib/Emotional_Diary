package com.example.emotionaldiary.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    @NonNull
    public String email;

    @NonNull
    public String username;

    @NonNull
    public String password;

    public User(@NonNull String name,
                @NonNull String email,
                @NonNull String username,
                @NonNull String password) {

        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}