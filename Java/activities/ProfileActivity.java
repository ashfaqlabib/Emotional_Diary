package com.example.emotionaldiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.emotionaldiary.database.AppDatabase;
import com.example.emotionaldiary.database.User;
import com.example.emotionaldiary.database.UserDao;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtName, txtEmail, txtUsername;
    private Button btnEditProfile, btnChangePassword;

    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtUsername = findViewById(R.id.txtUsername);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        userDao = AppDatabase.getDatabase(this).userDao();

        // প্রথমবার Profile Load
        loadProfile();

        // Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        btnChangePassword.setOnClickListener(v -> {

            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);

        });
    }

    // Profile Load Method
    private void loadProfile() {

        SharedPreferences preferences =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        String username = preferences.getString("username", "");

        if (!username.isEmpty()) {

            User user = userDao.getUserByUsername(username);

            if (user != null) {
                txtName.setText(user.name);
                txtEmail.setText(user.email);
                txtUsername.setText(user.username);
            }
        }
    }

    // Edit Profile থেকে ফিরে এলে আবার Data Load হবে
    @Override
    protected void onResume() {
        super.onResume();
        loadProfile();
    }
}