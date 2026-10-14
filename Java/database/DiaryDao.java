package com.example.emotionaldiary.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DiaryDao {

    @Insert
    void insert(Diary diary);

    @Update
    void update(Diary diary);

    @Delete
    void delete(Diary diary);

    @Query("SELECT * FROM diary ORDER BY id DESC")
    List<Diary> getAllDiaries();

    @Query("SELECT * FROM diary WHERE id = :id")
    Diary getDiaryById(int id);
}