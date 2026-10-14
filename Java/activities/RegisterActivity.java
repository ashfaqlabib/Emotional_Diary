package com.example.emotionaldiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emotionaldiary.models.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private Button btnRegister;
    private TextView txtLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        // =========================
        // VIEWS
        // =========================

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);

        // =========================
        // FIREBASE
        // =========================

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // =========================
        // PROGRESS DIALOG
        // =========================

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        // =========================
        // REGISTER
        // =========================

        btnRegister.setOnClickListener(v -> registerUser());

        // =========================
        // LOGIN
        // =========================

        txtLogin.setOnClickListener(v -> {

            Intent intent = new Intent(
                    RegisterActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });
    }

    // =====================================================
    // REGISTER USER
    // =====================================================

    private void registerUser() {

        String firstName =
                etFirstName.getText().toString().trim();

        String lastName =
                etLastName.getText().toString().trim();

        String email =
                etEmail.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        String confirmPassword =
                etConfirmPassword.getText().toString().trim();

        // =========================
        // VALIDATION
        // =========================

        if (TextUtils.isEmpty(firstName)) {

            etFirstName.setError("Enter your first name");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {

            etLastName.setError("Enter your last name");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {

            etEmail.setError("Enter your email address");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {

            etPassword.setError("Enter a password");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {

            etPassword.setError(
                    "Password must be at least 8 characters"
            );

            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {

            etConfirmPassword.setError(
                    "Confirm your password"
            );

            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {

            etConfirmPassword.setError(
                    "Passwords do not match"
            );

            etConfirmPassword.requestFocus();
            return;
        }

        // =========================
        // START
        // =========================

        btnRegister.setEnabled(false);
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        btnRegister.setEnabled(true);
                        progressDialog.dismiss();

                        showErrorDialog(
                                "Account Creation Failed",
                                getFirebaseErrorMessage(
                                        task.getException()
                                )
                        );

                        return;
                    }

                    FirebaseUser firebaseUser =
                            mAuth.getCurrentUser();

                    if (firebaseUser == null) {

                        btnRegister.setEnabled(true);
                        progressDialog.dismiss();

                        showErrorDialog(
                                "Something Went Wrong",
                                "Your account could not be loaded. Please try again."
                        );

                        return;
                    }

                    String fullName =
                            firstName + " " + lastName;

                    // =========================
                    // UPDATE FIREBASE PROFILE
                    // =========================

                    UserProfileChangeRequest profileUpdates =
                            new UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)
                                    .build();

                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(
                                    profileTask ->
                                            createFirestoreUser(
                                                    firebaseUser,
                                                    firstName,
                                                    lastName,
                                                    email
                                            )
                            );
                });
    }

    // =====================================================
    // CREATE FIRESTORE USER
    // =====================================================

    private void createFirestoreUser(
            FirebaseUser firebaseUser,
            String firstName,
            String lastName,
            String email) {

        // =========================
        // SEND EMAIL VERIFICATION
        // =========================

        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(
                        verificationTask -> {

                            UserModel user =
                                    new UserModel(
                                            firstName,
                                            lastName,
                                            email,
                                            "",
                                            System.currentTimeMillis()
                                    );

                            // =========================
                            // SAVE USER TO FIRESTORE
                            // =========================

                            db.collection("users")
                                    .document(firebaseUser.getUid())
                                    .set(user)
                                    .addOnSuccessListener(
                                            unused -> {

                                                progressDialog.dismiss();
                                                btnRegister.setEnabled(true);

                                                mAuth.signOut();

                                                showSuccessDialog();
                                            }
                                    )
                                    .addOnFailureListener(
                                            e -> {

                                                progressDialog.dismiss();
                                                btnRegister.setEnabled(true);

                                                showErrorDialog(
                                                        "Account Setup Failed",
                                                        "Your account was created, but we couldn't finish setting up your profile. Please try again."
                                                );
                                            }
                                    );
                        }
                );
    }

    // =====================================================
    // SUCCESS DIALOG
    // =====================================================

    private void showSuccessDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Account Created")
                .setMessage(
                        "Your account has been created successfully.\n\n" +
                                "A verification email has been sent to your email address. " +
                                "Please verify your email before logging in."
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Go to Login",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            RegisterActivity.this,
                                            MainActivity.class
                                    );

                            startActivity(intent);
                            finish();
                        }
                )
                .show();
    }

    // =====================================================
    // ERROR DIALOG
    // =====================================================

    private void showErrorDialog(
            String title,
            String message) {

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        "OK",
                        null
                )
                .show();
    }

    // =====================================================
    // FIREBASE ERROR MESSAGE
    // =====================================================

    private String getFirebaseErrorMessage(
            Exception exception) {

        if (exception == null) {

            return "We couldn't create your account. Please try again.";
        }

        String message =
                exception.getMessage();

        if (message == null) {

            return "We couldn't create your account. Please try again.";
        }

        if (message.contains(
                "email address is already in use")) {

            return "This email address is already registered. Please log in instead.";
        }

        if (message.contains(
                "The email address is badly formatted")) {

            return "Please enter a valid email address.";
        }

        if (message.contains(
                "network error")) {

            return "Please check your internet connection and try again.";
        }

        if (message.contains(
                "password is invalid")) {

            return "The password you entered is not valid.";
        }

        return "We couldn't create your account right now. Please try again.";
    }
}