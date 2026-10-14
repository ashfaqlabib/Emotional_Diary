package com.example.emotionaldiary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;

    private Button btnChangePassword;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_change_password);

        View root = findViewById(R.id.rootLayout);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            v.setPadding(
                    0,
                    bars.top,
                    0,
                    bars.bottom
            );

            return insets;
        });

        // Views
        etCurrentPassword =
                findViewById(R.id.etCurrentPassword);

        etNewPassword =
                findViewById(R.id.etNewPassword);

        etConfirmPassword =
                findViewById(R.id.etConfirmPassword);

        btnChangePassword =
                findViewById(R.id.btnChangePassword);

        // Firebase
        mAuth = FirebaseAuth.getInstance();

        // Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Changing Password");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // Button
        btnChangePassword.setOnClickListener(
                v -> changePassword()
        );
    }

    private void changePassword() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Session Expired",
                    "Your session has expired. Please sign in again."
            );

            return;
        }

        // Email/password authentication check
        if (user.getEmail() == null) {

            showMessage(
                    "Unable to Change Password",
                    "This account does not have an email password login."
            );

            return;
        }

        String currentPassword =
                etCurrentPassword.getText()
                        .toString()
                        .trim();

        String newPassword =
                etNewPassword.getText()
                        .toString()
                        .trim();

        String confirmPassword =
                etConfirmPassword.getText()
                        .toString()
                        .trim();

        // =========================
        // VALIDATION
        // =========================

        if (TextUtils.isEmpty(currentPassword)) {

            etCurrentPassword.setError(
                    "Enter your current password"
            );

            etCurrentPassword.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(newPassword)) {

            etNewPassword.setError(
                    "Enter a new password"
            );

            etNewPassword.requestFocus();

            return;
        }

        if (newPassword.length() < 8) {

            etNewPassword.setError(
                    "Password must contain at least 8 characters"
            );

            etNewPassword.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {

            etConfirmPassword.setError(
                    "Confirm your new password"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        if (!newPassword.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();

            return;
        }

        if (currentPassword.equals(newPassword)) {

            etNewPassword.setError(
                    "New password must be different"
            );

            etNewPassword.requestFocus();

            return;
        }

        // Disable button
        btnChangePassword.setEnabled(false);

        progressDialog.show();

        // =========================
        // RE-AUTHENTICATION
        // =========================

        AuthCredential credential =
                EmailAuthProvider.getCredential(
                        user.getEmail(),
                        currentPassword
                );

        user.reauthenticate(credential)

                .addOnSuccessListener(unused -> {

                    // Current password correct
                    updatePassword(
                            user,
                            newPassword
                    );
                })

                .addOnFailureListener(e -> {

                    progressDialog.dismiss();

                    btnChangePassword.setEnabled(true);

                    showMessage(
                            "Incorrect Password",
                            "The current password you entered is incorrect."
                    );
                });
    }

    private void updatePassword(
            FirebaseUser user,
            String newPassword) {

        user.updatePassword(newPassword)

                .addOnSuccessListener(unused -> {

                    progressDialog.dismiss();

                    btnChangePassword.setEnabled(true);

                    showSuccessDialog();
                })

                .addOnFailureListener(e -> {

                    progressDialog.dismiss();

                    btnChangePassword.setEnabled(true);

                    showMessage(
                            "Update Failed",
                            getFirebaseErrorMessage(e)
                    );
                });
    }

    // =========================
    // SUCCESS
    // =========================

    private void showSuccessDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Password Changed")
                .setMessage(
                        "Your password has been changed successfully."
                )
                .setPositiveButton(
                        "Done",
                        (dialog, which) -> finish()
                )
                .setCancelable(false)
                .show();
    }

    // =========================
    // MESSAGE
    // =========================

    private void showMessage(
            String title,
            String message) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }

    // =========================
    // FIREBASE ERROR
    // =========================

    private String getFirebaseErrorMessage(
            Exception e) {

        String message = e.getMessage();

        if (message == null ||
                message.trim().isEmpty()) {

            return "We couldn't change your password right now. Please try again.";
        }

        return message;
    }
}