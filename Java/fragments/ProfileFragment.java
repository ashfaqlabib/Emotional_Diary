package com.example.emotionaldiary.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emotionaldiary.EditProfileActivity;
import com.example.emotionaldiary.MainActivity;
import com.example.emotionaldiary.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.emotionaldiary.ChangePasswordActivity;
import com.example.emotionaldiary.SettingsActivity;

public class ProfileFragment extends Fragment {

    private TextView txtAvatar;
    private TextView txtUserName;
    private TextView txtUserEmail;

    private LinearLayout btnEditProfile;
    private LinearLayout btnChangePassword;
    private LinearLayout btnSettings;
    private LinearLayout btnLogout;

    private FirebaseAuth mAuth;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtAvatar = view.findViewById(R.id.txtAvatar);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSettings = view.findViewById(R.id.btnSettings);
        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();

        loadUserInfo();

        // Edit Profile
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), EditProfileActivity.class))
        );

        // Change Password (পরে করব)
        btnChangePassword.setOnClickListener(v -> {

            startActivity(new Intent(
                    requireActivity(),
                    ChangePasswordActivity.class
            ));

        });

        // Settings (পরে করব)
        btnSettings.setOnClickListener(v -> {

            startActivity(new Intent(
                    requireActivity(),
                    SettingsActivity.class
            ));

        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            mAuth.signOut();

            SharedPreferences.Editor editor =
                    requireActivity()
                            .getSharedPreferences("UserSession", requireActivity().MODE_PRIVATE)
                            .edit();

            editor.clear();
            editor.apply();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            requireActivity().finish();

        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void loadUserInfo() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) return;

        txtUserEmail.setText(user.getEmail());

        String name;

        if (user.getDisplayName() != null &&
                !user.getDisplayName().trim().isEmpty()) {

            name = user.getDisplayName();

        } else {

            name = "User";

        }

        txtUserName.setText(name);
        txtAvatar.setText(String.valueOf(Character.toUpperCase(name.charAt(0))));
    }
}