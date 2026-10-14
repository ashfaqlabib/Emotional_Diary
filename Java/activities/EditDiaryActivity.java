package com.example.emotionaldiary;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditDiaryActivity extends AppCompatActivity {

    private TextInputEditText etEditTitle;
    private TextInputEditText etEditContent;
    private Spinner spEditMood;
    private Button btnSaveChanges;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String diaryId;

    private final String[] moods = {
            "😊 Happy",
            "😍 Excited",
            "😌 Calm",
            "😐 Neutral",
            "😢 Sad",
            "😡 Angry"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_edit_diary);

        View root = findViewById(R.id.rootLayout);

        ViewCompat.setOnApplyWindowInsetsListener(
                root,
                (view, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    // Keep content below camera cutout/status bar
                    view.setPadding(
                            0,
                            systemBars.top,
                            0,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // =========================
        // FIND VIEWS
        // =========================

        etEditTitle = findViewById(
                R.id.etEditTitle
        );

        etEditContent = findViewById(
                R.id.etEditContent
        );

        spEditMood = findViewById(
                R.id.spEditMood
        );

        btnSaveChanges = findViewById(
                R.id.btnSaveChanges
        );

        // =========================
        // FIREBASE
        // =========================

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // =========================
        // GET DIARY DATA
        // =========================

        diaryId = getIntent().getStringExtra(
                "diaryId"
        );

        String title =
                getIntent().getStringExtra("title");

        String content =
                getIntent().getStringExtra("content");

        String mood =
                getIntent().getStringExtra("mood");

        // =========================
        // SET EXISTING DATA
        // =========================

        if (title != null) {
            etEditTitle.setText(title);
        }

        if (content != null) {
            etEditContent.setText(content);
        }

        // =========================
        // MOOD SPINNER
        // =========================

        ArrayAdapter<String> moodAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        moods
                );

        moodAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spEditMood.setAdapter(moodAdapter);

        if (mood != null) {

            for (int i = 0; i < moods.length; i++) {

                if (moods[i].equalsIgnoreCase(mood)) {

                    spEditMood.setSelection(i);

                    break;
                }
            }
        }

        // =========================
        // SAVE CHANGES
        // =========================

        btnSaveChanges.setOnClickListener(
                v -> saveChanges()
        );
    }

    // =====================================================
    // SAVE CHANGES
    // =====================================================

    private void saveChanges() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Unable to Update",
                    "Your session has expired. Please sign in again."
            );

            return;
        }

        if (diaryId == null ||
                diaryId.isEmpty()) {

            showMessage(
                    "Unable to Update",
                    "We couldn't identify this diary. Please try again."
            );

            return;
        }

        String title =
                etEditTitle.getText()
                        .toString()
                        .trim();

        String content =
                etEditContent.getText()
                        .toString()
                        .trim();

        String mood =
                spEditMood.getSelectedItem()
                        .toString();

        // =========================
        // VALIDATION
        // =========================

        if (title.isEmpty()) {

            etEditTitle.setError(
                    "Please enter a diary title"
            );

            etEditTitle.requestFocus();

            return;
        }

        if (content.isEmpty()) {

            etEditContent.setError(
                    "Please write something in your diary"
            );

            etEditContent.requestFocus();

            return;
        }

        // Disable button while saving
        btnSaveChanges.setEnabled(false);

        // =========================
        // UPDATE FIRESTORE
        // =========================

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .document(diaryId)
                .update(
                        "title",
                        title,

                        "content",
                        content,

                        "mood",
                        mood
                )

                .addOnSuccessListener(unused -> {

                    btnSaveChanges.setEnabled(true);

                    showUpdateSuccessDialog();
                })

                .addOnFailureListener(e -> {

                    btnSaveChanges.setEnabled(true);

                    showMessage(
                            "Update Failed",
                            "We couldn't update your diary right now. Please try again."
                    );
                });
    }

    // =====================================================
    // UPDATE SUCCESS
    // =====================================================

    private void showUpdateSuccessDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Diary Updated")

                .setMessage(
                        "Your changes have been saved successfully."
                )

                .setPositiveButton(
                        "Done",
                        (dialog, which) -> finish()
                )

                .setCancelable(false)

                .show();
    }

    // =====================================================
    // PROFESSIONAL MESSAGE
    // =====================================================

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