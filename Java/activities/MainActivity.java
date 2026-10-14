package com.example.emotionaldiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private Button btnLogin;
    private Button btnRegister;

    private View txtForgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences themePref =
                getSharedPreferences("AppSettings", MODE_PRIVATE);

        String theme =
                themePref.getString("theme", "System");

        switch (theme) {

            case "Light":
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
                break;

            case "Dark":
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );
                break;

            default:
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                );
                break;
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        txtForgotPassword =
                findViewById(R.id.txtForgotPassword);

        FirebaseUser currentUser =
                mAuth.getCurrentUser();

        if (currentUser != null &&
                currentUser.isEmailVerified()) {

            startActivity(
                    new Intent(
                            MainActivity.this,
                            HomeActivity.class
                    )
            );

            finish();

            return;
        }

        btnLogin.setOnClickListener(
                v -> loginUser()
        );

        btnRegister.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            MainActivity.this,
                            RegisterActivity.class
                    )
            );

        });

        txtForgotPassword.setOnClickListener(
                v -> showForgotPasswordDialog()
        );
    }

    private void loginUser() {

        String email =
                etEmail.getText()
                        .toString()
                        .trim();

        String password =
                etPassword.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(email)) {

            etEmail.setError(
                    "Enter your email"
            );

            etEmail.requestFocus();

            return;
        }

        if (!Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches()) {

            etEmail.setError(
                    "Enter a valid email"
            );

            etEmail.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(password)) {

            etPassword.setError(
                    "Enter your password"
            );

            etPassword.requestFocus();

            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    btnLogin.setEnabled(true);

                    if (!task.isSuccessful()) {

                        showMessage(
                                "Login Failed",
                                "Unable to login. Please check your email and password."
                        );

                        return;
                    }

                    FirebaseUser user =
                            mAuth.getCurrentUser();

                    if (user == null) {

                        showMessage(
                                "Login Failed",
                                "Unable to access your account. Please try again."
                        );

                        return;
                    }

                    if (!user.isEmailVerified()) {

                        mAuth.signOut();

                        showMessage(
                                "Email Not Verified",
                                "Please verify your email address before logging in."
                        );

                        return;
                    }

                    startActivity(
                            new Intent(
                                    MainActivity.this,
                                    HomeActivity.class
                            )
                    );

                    finish();

                });
    }

    private void showForgotPasswordDialog() {

        View dialogView =
                LayoutInflater
                        .from(this)
                        .inflate(
                                R.layout.dialog_forgot_password,
                                null
                        );

        TextInputEditText etResetEmail =
                dialogView.findViewById(
                        R.id.etResetEmail
                );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Reset Password")
                        .setMessage(
                                "Enter your registered email address. We will send you a secure password reset link."
                        )
                        .setView(dialogView)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Send Link",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    Button sendButton =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    sendButton.setOnClickListener(
                            v -> {

                                String email =
                                        etResetEmail
                                                .getText()
                                                .toString()
                                                .trim();

                                if (TextUtils.isEmpty(email)) {

                                    etResetEmail.setError(
                                            "Enter your email"
                                    );

                                    etResetEmail.requestFocus();

                                    return;
                                }

                                if (!Patterns.EMAIL_ADDRESS
                                        .matcher(email)
                                        .matches()) {

                                    etResetEmail.setError(
                                            "Enter a valid email"
                                    );

                                    etResetEmail.requestFocus();

                                    return;
                                }

                                sendPasswordResetEmail(
                                        email,
                                        dialog
                                );
                            }
                    );
                }
        );

        dialog.show();
    }

    private void sendPasswordResetEmail(
            String email,
            AlertDialog dialog) {

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {

                    dialog.dismiss();

                    showMessage(
                            "Reset Link Sent",
                            "A password reset link has been sent to:\n\n"
                                    + email
                                    + "\n\n"
                                    + "Please open your email and follow the link to create your new password."
                    );

                })
                .addOnFailureListener(e -> {

                    String errorMessage =
                            e.getMessage();

                    if (errorMessage == null ||
                            errorMessage.trim().isEmpty()) {

                        errorMessage =
                                "We couldn't send the password reset email. Please try again.";
                    }

                    showMessage(
                            "Reset Failed",
                            errorMessage
                    );
                });
    }

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
}