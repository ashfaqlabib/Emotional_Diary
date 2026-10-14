package com.example.emotionaldiary.models;

public class DiaryModel {

    private String id;
    private String title;
    private String content;
    private String mood;
    private String date;
    private String time;
    private long createdAt;
    private String userId;
    private boolean favorite;
    private String imageUrl;

    // Required empty constructor for Firestore
    public DiaryModel() {
    }

    public DiaryModel(
            String id,
            String title,
            String content,
            String mood,
            String date,
            String time,
            long createdAt,
            String userId,
            boolean favorite,
            String imageUrl) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.date = date;
        this.time = time;
        this.createdAt = createdAt;
        this.userId = userId;
        this.favorite = favorite;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}