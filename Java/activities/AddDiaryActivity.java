package com.example.emotionaldiary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddDiaryActivity extends AppCompatActivity {

    private TextInputEditText etTitle;
    private TextInputEditText etContent;

    private Spinner spMood;

    private Button btnSaveDiary;

    private TextView txtDate;
    private TextView txtTime;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressDialog progressDialog;

    private EmotionDetector emotionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_add_diary);

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

        // =========================
        // VIEWS
        // =========================

        etTitle =
                findViewById(R.id.etTitle);

        etContent =
                findViewById(R.id.etContent);

        spMood =
                findViewById(R.id.spMood);

        btnSaveDiary =
                findViewById(R.id.btnSaveDiary);

        txtDate =
                findViewById(R.id.txtDate);

        txtTime =
                findViewById(R.id.txtTime);


        // =========================
        // FIREBASE
        // =========================

        mAuth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();


        // =========================
        // EMOTION DETECTOR
        // =========================

        emotionDetector =
                new EmotionDetector();


        // =========================
        // PROGRESS DIALOG
        // =========================

        progressDialog =
                new ProgressDialog(this);

        progressDialog.setTitle(
                "Saving Diary"
        );

        progressDialog.setMessage(
                "Please wait..."
        );

        progressDialog.setCancelable(false);


        // =========================
        // DATE & TIME
        // =========================

        String currentDate =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                ).format(new Date());

        String currentTime =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                ).format(new Date());

        txtDate.setText(
                currentDate
        );

        txtTime.setText(
                currentTime
        );


        // =========================
        // MOOD OPTIONS
        // =========================

        String[] moods = {

                "Auto Detect Emotion",

                "😊 Happy",

                "😍 Excited",

                "😌 Calm",

                "😐 Neutral",

                "😢 Sad",

                "😡 Angry"
        };


        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        moods
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spMood.setAdapter(
                adapter
        );


        // =========================
        // SAVE BUTTON
        // =========================

        btnSaveDiary.setOnClickListener(
                v -> saveDiary(
                        currentDate,
                        currentTime
                )
        );
    }


    // =====================================================
    // SAVE DIARY
    // =====================================================

    private void saveDiary(
            String currentDate,
            String currentTime
    ) {

        String title =
                etTitle.getText()
                        .toString()
                        .trim();

        String content =
                etContent.getText()
                        .toString()
                        .trim();


        // =========================
        // VALIDATION
        // =========================

        if (TextUtils.isEmpty(title)) {

            etTitle.setError(
                    "Please enter a diary title"
            );

            etTitle.requestFocus();

            return;
        }


        if (TextUtils.isEmpty(content)) {

            etContent.setError(
                    "Please write something in your diary"
            );

            etContent.requestFocus();

            return;
        }


        // =========================
        // USER
        // =========================

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            showMessage(
                    "Session Expired",
                    "Your session has expired. Please sign in again."
            );

            return;
        }


        // =========================
        // SELECTED MOOD
        // =========================

        String selectedMood =
                spMood.getSelectedItem()
                        .toString();


        /*
         * If user manually selected an emotion,
         * save it directly.
         *
         * If Auto Detect is selected,
         * analyze previous diaries first.
         */

        if (!selectedMood.equals(
                "Auto Detect Emotion"
        )) {

            saveDiaryToFirestore(
                    user,
                    title,
                    content,
                    selectedMood,
                    currentDate,
                    currentTime
            );

            return;
        }


        // =========================
        // AUTO EMOTION DETECTION
        // =========================

        progressDialog.setTitle(
                "Analyzing Emotion"
        );

        progressDialog.setMessage(
                "Analyzing your diary and previous entries..."
        );

        progressDialog.show();


        loadPreviousDiariesForAnalysis(
                user,
                title,
                content,
                currentDate,
                currentTime
        );
    }


    // =====================================================
    // LOAD PREVIOUS DIARIES
    // =====================================================

    private void loadPreviousDiariesForAnalysis(
            FirebaseUser user,
            String title,
            String content,
            String currentDate,
            String currentTime
    ) {

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .orderBy(
                        "createdAt"
                )
                .get()
                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            StringBuilder previousData =
                                    new StringBuilder();


                            int diaryNumber = 1;


                            for (
                                    QueryDocumentSnapshot document
                                    : queryDocumentSnapshots
                            ) {

                                String oldTitle =
                                        document.getString(
                                                "title"
                                        );

                                String oldContent =
                                        document.getString(
                                                "content"
                                        );

                                String oldMood =
                                        document.getString(
                                                "mood"
                                        );

                                if (oldTitle == null) {
                                    oldTitle = "";
                                }

                                if (oldContent == null) {
                                    oldContent = "";
                                }

                                if (oldMood == null) {
                                    oldMood = "Unknown";
                                }


                                previousData
                                        .append(
                                                "Diary "
                                        )
                                        .append(
                                                diaryNumber
                                        )
                                        .append(
                                                "\n"
                                        );

                                previousData
                                        .append(
                                                "Title: "
                                        )
                                        .append(
                                                oldTitle
                                        )
                                        .append(
                                                "\n"
                                        );

                                previousData
                                        .append(
                                                "Emotion: "
                                        )
                                        .append(
                                                oldMood
                                        )
                                        .append(
                                                "\n"
                                        );

                                previousData
                                        .append(
                                                "Content: "
                                        )
                                        .append(
                                                oldContent
                                        )
                                        .append(
                                                "\n\n"
                                        );


                                diaryNumber++;
                            }


                            String previousDiaryContext =
                                    previousData.toString();


                            String newDiary =
                                    "Title: "
                                            + title
                                            + "\n"
                                            + "Content: "
                                            + content;


                            analyzeNewDiary(
                                    user,
                                    title,
                                    content,
                                    newDiary,
                                    previousDiaryContext,
                                    currentDate,
                                    currentTime
                            );
                        }
                )
                .addOnFailureListener(
                        e -> {

                            progressDialog.dismiss();

                            showMessage(
                                    "Unable to Analyze Diary",
                                    "We couldn't access your previous diaries. "
                                            + "Please try again."
                            );
                        }
                );
    }


    // =====================================================
    // AI EMOTION ANALYSIS
    // =====================================================

    private void analyzeNewDiary(
            FirebaseUser user,
            String title,
            String content,
            String newDiary,
            String previousDiaryContext,
            String currentDate,
            String currentTime
    ) {

        emotionDetector.detectEmotion(
                newDiary,
                previousDiaryContext,

                new EmotionDetector.EmotionCallback() {

                    @Override
                    public void onSuccess(
                            String emotion
                    ) {

                        runOnUiThread(
                                () -> {

                                    /*
                                     * AI has selected
                                     * the emotion.
                                     */

                                    saveDiaryToFirestore(
                                            user,
                                            title,
                                            content,
                                            emotion,
                                            currentDate,
                                            currentTime
                                    );
                                }
                        );
                    }


                    @Override
                    public void onFailure(
                            Exception e
                    ) {

                        e.printStackTrace();

                        runOnUiThread(
                                () -> {

                                    progressDialog.dismiss();

                                    String errorMessage;

                                    if (e.getMessage() != null &&
                                            !e.getMessage().trim().isEmpty()) {

                                        errorMessage =
                                                e.getMessage();

                                    } else {

                                        errorMessage =
                                                "Unknown error occurred during emotion detection.";
                                    }

                                    showMessage(
                                            "Emotion Detection Failed",
                                            "The AI could not analyze this diary.\n\n"
                                                    + "Error:\n"
                                                    + errorMessage
                                    );
                                }
                        );
                    }
                }
        );
    }


    // =====================================================
    // SAVE TO FIRESTORE
    // =====================================================

    private void saveDiaryToFirestore(
            FirebaseUser user,
            String title,
            String content,
            String mood,
            String currentDate,
            String currentTime
    ) {

        progressDialog.setTitle(
                "Saving Diary"
        );

        progressDialog.setMessage(
                "Please wait..."
        );

        if (!progressDialog.isShowing()) {

            progressDialog.show();
        }


        String diaryId =
                db.collection("users")
                        .document(user.getUid())
                        .collection("diaries")
                        .document()
                        .getId();


        Map<String, Object> diary =
                new HashMap<>();


        diary.put(
                "id",
                diaryId
        );

        diary.put(
                "title",
                title
        );

        diary.put(
                "content",
                content
        );

        diary.put(
                "mood",
                mood
        );

        diary.put(
                "date",
                currentDate
        );

        diary.put(
                "time",
                currentTime
        );

        diary.put(
                "createdAt",
                System.currentTimeMillis()
        );

        diary.put(
                "userId",
                user.getUid()
        );

        diary.put(
                "favorite",
                false
        );

        diary.put(
                "imageUrl",
                ""
        );


        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .document(diaryId)
                .set(diary)

                .addOnSuccessListener(
                        unused -> {

                            progressDialog.dismiss();

                            showSaveSuccessDialog();
                        }
                )

                .addOnFailureListener(
                        e -> {

                            progressDialog.dismiss();

                            showMessage(
                                    "Unable to Save Diary",
                                    "We couldn't save your diary right now. "
                                            + "Please try again."
                            );
                        }
                );
    }


    // =====================================================
    // SUCCESS DIALOG
    // =====================================================

    private void showSaveSuccessDialog() {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Diary Saved"
                )
                .setMessage(
                        "Your diary has been saved successfully."
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
    // MESSAGE DIALOG
    // =====================================================

    private void showMessage(
            String title,
            String message
    ) {

        new AlertDialog.Builder(this)
                .setTitle(
                        title
                )
                .setMessage(
                        message
                )
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }
}