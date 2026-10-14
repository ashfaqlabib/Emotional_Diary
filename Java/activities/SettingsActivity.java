package com.example.emotionaldiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout layoutTheme;
    private LinearLayout layoutNotification;
    private LinearLayout layoutReminder;
    private LinearLayout layoutDeleteAccount;
    private LinearLayout layoutAbout;
    private LinearLayout layoutPrivacy;
    private LinearLayout layoutTerms;

    private MaterialSwitch switchNotification;

    private SharedPreferences preferences;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final int REMINDER_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_settings);

        View root = findViewById(R.id.rootLayout);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (v, insets) -> {

                    Insets bars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            0,
                            bars.top,
                            0,
                            bars.bottom
                    );

                    return insets;
                }
        );

        // =====================================================
        // FIREBASE
        // =====================================================

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // =====================================================
        // VIEWS
        // =====================================================

        layoutTheme =
                findViewById(R.id.layoutTheme);

        layoutNotification =
                findViewById(R.id.layoutNotification);

        layoutReminder =
                findViewById(R.id.layoutReminder);

        layoutDeleteAccount =
                findViewById(R.id.layoutDeleteAccount);

        layoutAbout =
                findViewById(R.id.layoutAbout);

        layoutPrivacy =
                findViewById(R.id.layoutPrivacy);

        layoutTerms =
                findViewById(R.id.layoutTerms);

        switchNotification =
                findViewById(R.id.switchNotification);

        // =====================================================
        // SHARED PREFERENCES
        // =====================================================

        preferences =
                getSharedPreferences(
                        "AppSettings",
                        MODE_PRIVATE
                );

        // =====================================================
        // NOTIFICATION SWITCH
        // =====================================================

        boolean notificationEnabled =
                preferences.getBoolean(
                        "notification",
                        true
                );

        switchNotification.setChecked(
                notificationEnabled
        );

        switchNotification.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(
                                    "notification",
                                    isChecked
                            )
                            .apply();

                    // Notification OFF হলে reminder cancel
                    if (!isChecked) {

                        cancelDailyReminder();

                    } else {

                        // Notification ON করলে
                        // previously saved reminder থাকলে আবার schedule
                        long savedTime =
                                preferences.getLong(
                                        "reminderTime",
                                        -1
                                );

                        if (savedTime != -1) {

                            scheduleDailyReminder(
                                    savedTime
                            );
                        }
                    }
                }
        );

        // =====================================================
        // THEME
        // =====================================================

        layoutTheme.setOnClickListener(
                v -> showThemeDialog()
        );

        // =====================================================
        // NOTIFICATION
        // =====================================================

        layoutNotification.setOnClickListener(v -> {

            switchNotification.setChecked(
                    !switchNotification.isChecked()
            );
        });

        // =====================================================
        // DAILY REMINDER
        // =====================================================

        layoutReminder.setOnClickListener(
                v -> showReminderTimePicker()
        );

        // =====================================================
        // DELETE ACCOUNT
        // =====================================================

        layoutDeleteAccount.setOnClickListener(
                v -> showDeleteAccountConfirmation()
        );

        // =====================================================
        // ABOUT
        // =====================================================

        layoutAbout.setOnClickListener(
                v -> showAboutDialog()
        );

        // =====================================================
        // PRIVACY
        // =====================================================

        layoutPrivacy.setOnClickListener(
                v -> showPrivacyDialog()
        );

        // =====================================================
        // TERMS
        // =====================================================

        layoutTerms.setOnClickListener(
                v -> showTermsDialog()
        );
    }

    // =====================================================
    // THEME
    // =====================================================

    private void showThemeDialog() {

        String[] themes = {
                "Light",
                "Dark",
                "System"
        };

        String currentTheme =
                preferences.getString(
                        "theme",
                        "System"
                );

        int selectedTheme = 2;

        if (currentTheme.equals("Light")) {

            selectedTheme = 0;

        } else if (currentTheme.equals("Dark")) {

            selectedTheme = 1;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose Theme")
                .setSingleChoiceItems(
                        themes,
                        selectedTheme,
                        (dialog, which) -> {

                            SharedPreferences.Editor editor =
                                    preferences.edit();

                            if (which == 0) {

                                AppCompatDelegate
                                        .setDefaultNightMode(
                                                AppCompatDelegate
                                                        .MODE_NIGHT_NO
                                        );

                                editor.putString(
                                        "theme",
                                        "Light"
                                );

                            } else if (which == 1) {

                                AppCompatDelegate
                                        .setDefaultNightMode(
                                                AppCompatDelegate
                                                        .MODE_NIGHT_YES
                                        );

                                editor.putString(
                                        "theme",
                                        "Dark"
                                );

                            } else {

                                AppCompatDelegate
                                        .setDefaultNightMode(
                                                AppCompatDelegate
                                                        .MODE_NIGHT_FOLLOW_SYSTEM
                                        );

                                editor.putString(
                                        "theme",
                                        "System"
                                );
                            }

                            editor.apply();

                            dialog.dismiss();
                        }
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .show();
    }

    // =====================================================
    // DAILY REMINDER TIME PICKER
    // =====================================================

    private void showReminderTimePicker() {

        Calendar calendar =
                Calendar.getInstance();

        int currentHour =
                calendar.get(Calendar.HOUR_OF_DAY);

        int currentMinute =
                calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog =
                new TimePickerDialog(
                        this,
                        (view, hourOfDay, minute) -> {

                            Calendar reminderTime =
                                    Calendar.getInstance();

                            reminderTime.set(
                                    Calendar.HOUR_OF_DAY,
                                    hourOfDay
                            );

                            reminderTime.set(
                                    Calendar.MINUTE,
                                    minute
                            );

                            reminderTime.set(
                                    Calendar.SECOND,
                                    0
                            );

                            reminderTime.set(
                                    Calendar.MILLISECOND,
                                    0
                            );

                            /*
                             * যদি selected time আজকের সময়ের
                             * চেয়ে আগের হয়, তাহলে আগামীকাল
                             * থেকে শুরু হবে।
                             */

                            if (reminderTime.getTimeInMillis()
                                    <= System.currentTimeMillis()) {

                                reminderTime.add(
                                        Calendar.DAY_OF_YEAR,
                                        1
                                );
                            }

                            long time =
                                    reminderTime
                                            .getTimeInMillis();

                            // Save reminder time
                            preferences.edit()
                                    .putLong(
                                            "reminderTime",
                                            time
                                    )
                                    .apply();

                            // Notification ON থাকলে schedule
                            boolean enabled =
                                    preferences.getBoolean(
                                            "notification",
                                            true
                                    );

                            if (enabled) {

                                scheduleDailyReminder(
                                        time
                                );

                                showMessage(
                                        "Reminder Set",
                                        "Your daily diary reminder has been set."
                                );

                            } else {

                                showMessage(
                                        "Notifications Off",
                                        "Turn on Notifications first to receive daily reminders."
                                );
                            }

                        },
                        currentHour,
                        currentMinute,
                        false
                );

        timePickerDialog.setTitle(
                "Choose Daily Reminder Time"
        );

        timePickerDialog.show();
    }

    // =====================================================
    // SCHEDULE DAILY REMINDER
    // =====================================================

    private void scheduleDailyReminder(
            long reminderTimeMillis) {

        AlarmManager alarmManager =
                (AlarmManager)
                        getSystemService(
                                ALARM_SERVICE
                        );

        if (alarmManager == null) {
            return;
        }

        Intent intent =
                new Intent(
                        this,
                        DiaryReminderReceiver.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        REMINDER_REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        /*
         * প্রতিদিন একই সময়ে reminder
         */

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    // =====================================================
    // CANCEL DAILY REMINDER
    // =====================================================

    private void cancelDailyReminder() {

        AlarmManager alarmManager =
                (AlarmManager)
                        getSystemService(
                                ALARM_SERVICE
                        );

        if (alarmManager == null) {
            return;
        }

        Intent intent =
                new Intent(
                        this,
                        DiaryReminderReceiver.class
                );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        REMINDER_REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE
                );

        alarmManager.cancel(
                pendingIntent
        );
    }

    // =====================================================
    // DELETE ACCOUNT CONFIRMATION
    // =====================================================

    private void showDeleteAccountConfirmation() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Account?")
                .setMessage(
                        "Your account, diary entries and profile " +
                                "data will be permanently deleted.\n\n" +
                                "This action cannot be undone."
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Continue",
                        (dialog, which) ->
                                showPasswordDialog()
                )
                .show();
    }

    // =====================================================
    // PASSWORD DIALOG
    // =====================================================

    private void showPasswordDialog() {

        EditText passwordInput =
                new EditText(this);

        passwordInput.setHint(
                "Current Password"
        );

        passwordInput.setSingleLine(true);

        passwordInput.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        passwordInput.setBackground(null);

        int padding =
                (int) (
                        16 *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );

        passwordInput.setPadding(
                padding,
                12,
                padding,
                12
        );

        AlertDialog dialog =
                new MaterialAlertDialogBuilder(this)
                        .setTitle(
                                "Verify Your Password"
                        )
                        .setMessage(
                                "Enter your current password to continue."
                        )
                        .setView(passwordInput)
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Delete Account",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(v -> {

                        String password =
                                passwordInput
                                        .getText()
                                        .toString()
                                        .trim();

                        if (password.isEmpty()) {

                            passwordInput.setError(
                                    "Password is required"
                            );

                            passwordInput.requestFocus();

                            return;
                        }

                        dialog.dismiss();

                        deleteAccount(
                                password
                        );
                    });
                }
        );

        dialog.show();
    }

    // =====================================================
    // DELETE ACCOUNT
    // =====================================================

    private void deleteAccount(
            String currentPassword) {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Account Unavailable",
                    "Your session has expired. Please sign in again."
            );

            return;
        }

        if (user.getEmail() == null) {

            showMessage(
                    "Unable to Delete",
                    "This account does not have an email address."
            );

            return;
        }

        AuthCredential credential =
                EmailAuthProvider.getCredential(
                        user.getEmail(),
                        currentPassword
                );

        showDeletingDialog(
                user,
                credential
        );
    }

    // =====================================================
    // RE-AUTHENTICATION
    // =====================================================

    private void showDeletingDialog(
            FirebaseUser user,
            AuthCredential credential) {

        AlertDialog progressDialog =
                new MaterialAlertDialogBuilder(this)
                        .setTitle(
                                "Deleting Account"
                        )
                        .setMessage(
                                "Please wait..."
                        )
                        .setCancelable(false)
                        .create();

        progressDialog.show();

        user.reauthenticate(credential)
                .addOnSuccessListener(
                        unused -> {

                            deleteDiaryCollection(
                                    user,
                                    progressDialog
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressDialog.dismiss();

                            showMessage(
                                    "Verification Failed",
                                    "The current password is incorrect."
                            );
                        }
                );
    }

    // =====================================================
    // DELETE DIARIES
    // =====================================================

    private void deleteDiaryCollection(
            FirebaseUser user,
            AlertDialog progressDialog) {

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (querySnapshot.isEmpty()) {

                                deleteUserDocument(
                                        user,
                                        progressDialog
                                );

                                return;
                            }

                            deleteDiaryDocuments(
                                    user,
                                    querySnapshot,
                                    progressDialog
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressDialog.dismiss();

                            showMessage(
                                    "Delete Failed",
                                    "We couldn't access your diary data. Please try again."
                            );
                        }
                );
    }

    // =====================================================
    // DELETE EACH DIARY DOCUMENT
    // =====================================================

    private void deleteDiaryDocuments(
            FirebaseUser user,
            QuerySnapshot snapshot,
            AlertDialog progressDialog) {

        int total =
                snapshot.getDocuments().size();

        final int[] deletedCount =
                {0};

        for (
                com.google.firebase.firestore.DocumentSnapshot document
                : snapshot.getDocuments()
        ) {

            document.getReference()
                    .delete()
                    .addOnSuccessListener(
                            unused -> {

                                deletedCount[0]++;

                                if (deletedCount[0]
                                        == total) {

                                    deleteUserDocument(
                                            user,
                                            progressDialog
                                    );
                                }
                            }
                    )
                    .addOnFailureListener(
                            e -> {

                                if (progressDialog.isShowing()) {

                                    progressDialog.dismiss();
                                }

                                showMessage(
                                        "Delete Failed",
                                        "Some diary data could not be deleted."
                                );
                            }
                    );
        }
    }

    // =====================================================
    // DELETE USER DOCUMENT
    // =====================================================

    private void deleteUserDocument(
            FirebaseUser user,
            AlertDialog progressDialog) {

        db.collection("users")
                .document(user.getUid())
                .delete()
                .addOnSuccessListener(
                        unused ->
                                deleteAuthAccount(
                                        user,
                                        progressDialog
                                )
                )
                .addOnFailureListener(
                        e -> {

                            progressDialog.dismiss();

                            showMessage(
                                    "Delete Failed",
                                    "Your profile data could not be deleted."
                            );
                        }
                );
    }

    // =====================================================
    // DELETE FIREBASE AUTH ACCOUNT
    // =====================================================

    private void deleteAuthAccount(
            FirebaseUser user,
            AlertDialog progressDialog) {

        user.delete()
                .addOnSuccessListener(
                        unused -> {

                            progressDialog.dismiss();

                            getSharedPreferences(
                                    "UserSession",
                                    MODE_PRIVATE
                            )
                                    .edit()
                                    .clear()
                                    .apply();

                            // Cancel reminder
                            cancelDailyReminder();

                            showDeleteSuccess();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressDialog.dismiss();

                            showMessage(
                                    "Delete Failed",
                                    "Your account could not be completely deleted. Please try again."
                            );
                        }
                );
    }

    // =====================================================
    // DELETE SUCCESS
    // =====================================================

    private void showDeleteSuccess() {

        new MaterialAlertDialogBuilder(this)
                .setTitle(
                        "Account Deleted"
                )
                .setMessage(
                        "Your Emotional Diary account and diary data " +
                                "have been permanently deleted."
                )
                .setCancelable(false)
                .setPositiveButton(
                        "Done",
                        (dialog, which) -> {

                            Intent intent =
                                    new Intent(
                                            SettingsActivity.this,
                                            MainActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();
                        }
                )
                .show();
    }

    // =====================================================
    // ABOUT
    // =====================================================

    private void showAboutDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle(
                        "About Emotional Diary"
                )
                .setMessage(
                        "Emotional Diary\n\n" +
                                "A personal diary application " +
                                "designed to help you write, " +
                                "manage and reflect on your memories " +
                                "and emotions.\n\n" +
                                "Version 1.0"
                )
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }

    // =====================================================
    // PRIVACY
    // =====================================================

    private void showPrivacyDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle(
                        "Privacy Policy"
                )
                .setMessage(
                        "Your diary entries are stored under " +
                                "your authenticated account.\n\n" +
                                "Your diary content is associated " +
                                "with your account and is not " +
                                "publicly displayed by the app.\n\n" +
                                "Deleting your account permanently " +
                                "removes your stored diary data."
                )
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }

    // =====================================================
    // TERMS
    // =====================================================

    private void showTermsDialog() {

        new MaterialAlertDialogBuilder(this)
                .setTitle(
                        "Terms & Conditions"
                )
                .setMessage(
                        "By using Emotional Diary, you agree " +
                                "to use the application responsibly.\n\n" +
                                "You are responsible for the content " +
                                "you create and store in your diary.\n\n" +
                                "The application is intended for " +
                                "personal diary and reflection purposes."
                )
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }

    // =====================================================
    // GENERAL MESSAGE
    // =====================================================

    private void showMessage(
            String title,
            String message) {

        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }
}