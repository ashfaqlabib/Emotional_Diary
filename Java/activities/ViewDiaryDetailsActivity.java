package com.example.emotionaldiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewDiaryDetailsActivity extends AppCompatActivity {

    private TextView txtDiaryTitle;
    private TextView txtDiaryDateTime;
    private TextView txtDiaryMood;
    private TextView txtDiaryContent;

    private ImageButton btnEditDiary;
    private ImageButton btnDeleteDiary;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String diaryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_view_diary_details);

        // Root layout
        View root = findViewById(R.id.rootLayout);

        // Keep content below camera cutout / status bar
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {

            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    0,
                    systemBars.top,
                    0,
                    systemBars.bottom
            );

            return insets;
        });

        // =========================
        // FIND VIEWS
        // =========================

        txtDiaryTitle = findViewById(
                R.id.txtDiaryTitle
        );

        txtDiaryDateTime = findViewById(
                R.id.txtDiaryDateTime
        );

        txtDiaryMood = findViewById(
                R.id.txtDiaryMood
        );

        txtDiaryContent = findViewById(
                R.id.txtDiaryContent
        );

        btnEditDiary = findViewById(
                R.id.btnEditDiary
        );

        btnDeleteDiary = findViewById(
                R.id.btnDeleteDiary
        );

        // =========================
        // FIREBASE
        // =========================

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // =========================
        // GET DIARY ID
        // =========================

        diaryId = getIntent().getStringExtra(
                "diaryId"
        );

        // =========================
        // LOAD DIARY
        // =========================

        loadDiary();

        // =========================
        // EDIT DIARY
        // =========================

        btnEditDiary.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ViewDiaryDetailsActivity.this,
                    EditDiaryActivity.class
            );

            intent.putExtra(
                    "diaryId",
                    diaryId
            );

            intent.putExtra(
                    "title",
                    txtDiaryTitle.getText().toString()
            );

            intent.putExtra(
                    "content",
                    txtDiaryContent.getText().toString()
            );

            intent.putExtra(
                    "mood",
                    txtDiaryMood.getText().toString()
            );

            startActivity(intent);
        });

        // =========================
        // DELETE DIARY
        // =========================

        btnDeleteDiary.setOnClickListener(v ->
                showDeleteConfirmation()
        );
    }

    // =====================================================
    // LOAD DIARY
    // =====================================================

    private void loadDiary() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Diary Unavailable",
                    "Your session has expired. Please sign in again."
            );

            return;
        }

        if (diaryId == null ||
                diaryId.isEmpty()) {

            showMessage(
                    "Diary Unavailable",
                    "This diary could not be opened."
            );

            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .document(diaryId)
                .get()

                .addOnSuccessListener(
                        this::handleDiaryResult
                )

                .addOnFailureListener(e -> {

                    showMessage(
                            "Unable to Load Diary",
                            "We couldn't load this diary right now. Please try again."
                    );
                });
    }

    // =====================================================
    // HANDLE DIARY RESULT
    // =====================================================

    private void handleDiaryResult(
            @NonNull DocumentSnapshot document) {

        if (!document.exists()) {

            showMessage(
                    "Diary Not Found",
                    "This diary may have already been deleted."
            );

            return;
        }

        displayDiary(document);
    }

    // =====================================================
    // DISPLAY DIARY
    // =====================================================

    private void displayDiary(
            DocumentSnapshot document) {

        String title =
                document.getString("title");

        String content =
                document.getString("content");

        String mood =
                document.getString("mood");

        String date =
                document.getString("date");

        String time =
                document.getString("time");

        // Title
        if (title != null &&
                !title.isEmpty()) {

            txtDiaryTitle.setText(title);
        }

        // Content
        if (content != null &&
                !content.isEmpty()) {

            txtDiaryContent.setText(content);
        }

        // Mood
        if (mood != null &&
                !mood.isEmpty()) {

            txtDiaryMood.setText(mood);
        }

        // =========================
        // DATE + TIME
        // =========================

        StringBuilder dateTime =
                new StringBuilder();

        if (date != null &&
                !date.isEmpty()) {

            dateTime.append(date);
        }

        if (time != null &&
                !time.isEmpty()) {

            if (dateTime.length() > 0) {

                dateTime.append(" • ");
            }

            dateTime.append(time);
        }

        txtDiaryDateTime.setText(
                dateTime.toString()
        );
    }

    // =====================================================
    // REFRESH AFTER EDIT
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        /*
         * EditDiaryActivity থেকে ফিরে আসার পর
         * Firestore থেকে সর্বশেষ diary data load হবে।
         */

        if (db != null &&
                diaryId != null &&
                !diaryId.isEmpty()) {

            loadDiary();
        }
    }

    // =====================================================
    // DELETE CONFIRMATION
    // =====================================================

    private void showDeleteConfirmation() {

        new AlertDialog.Builder(this)

                .setTitle("Delete Diary?")

                .setMessage(
                        "This diary will be permanently removed. Do you want to continue?"
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            deleteDiary();
                        }
                )

                .show();
    }

    // =====================================================
    // DELETE DIARY
    // =====================================================

    private void deleteDiary() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Action Unavailable",
                    "Your session has expired. Please sign in again."
            );

            return;
        }

        if (diaryId == null ||
                diaryId.isEmpty()) {

            showMessage(
                    "Action Unavailable",
                    "We couldn't identify this diary. Please try again."
            );

            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .document(diaryId)
                .delete()

                .addOnSuccessListener(
                        unused ->
                                showDeleteSuccessDialog()
                )

                .addOnFailureListener(e -> {

                    showMessage(
                            "Delete Failed",
                            "We couldn't delete this diary right now. Please try again."
                    );
                });
    }

    // =====================================================
    // DELETE SUCCESS
    // =====================================================

    private void showDeleteSuccessDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Diary Deleted")

                .setMessage(
                        "The diary has been removed from your diary collection."
                )

                .setPositiveButton(
                        "Done",
                        (dialog, which) -> {

                            setResult(
                                    RESULT_OK
                            );

                            finish();
                        }
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