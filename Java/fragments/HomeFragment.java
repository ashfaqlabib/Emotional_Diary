package com.example.emotionaldiary.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emotionaldiary.AddDiaryActivity;
import com.example.emotionaldiary.R;
import com.example.emotionaldiary.views.EmotionPieChart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private Button btnNewDiary;
    private TextView txtGreeting;

    private EmotionPieChart emotionPieChart;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_home,
                container,
                false
        );

        // =========================
        // VIEWS
        // =========================

        txtGreeting =
                view.findViewById(R.id.txtGreeting);

        btnNewDiary =
                view.findViewById(R.id.btnNewDiary);

        emotionPieChart =
                view.findViewById(R.id.emotionPieChart);

        // =========================
        // FIREBASE
        // =========================

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        // =========================
        // GREETING
        // =========================

        updateGreeting();

        // =========================
        // NEW DIARY
        // =========================

        btnNewDiary.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireActivity(),
                    AddDiaryActivity.class
            );

            startActivity(intent);
        });

        return view;
    }

    // =====================================================
    // RESUME
    // =====================================================

    @Override
    public void onResume() {

        super.onResume();

        updateGreeting();

        loadDiarySummary();
    }

    // =====================================================
    // DYNAMIC GREETING
    // =====================================================

    private void updateGreeting() {

        if (txtGreeting == null) {
            return;
        }

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;

        if (hour >= 5 && hour < 12) {

            greeting = "Good Morning";

        } else if (hour >= 12 && hour < 17) {

            greeting = "Good Afternoon";

        } else if (hour >= 17 && hour < 21) {

            greeting = "Good Evening";

        } else {

            greeting = "Good Night";
        }

        txtGreeting.setText(greeting);
    }

    // =====================================================
    // LOAD DIARY SUMMARY
    // =====================================================

    private void loadDiarySummary() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        // =========================
        // USER NOT LOGGED IN
        // =========================

        if (user == null) {

            resetSummary();

            return;
        }

        // =========================
        // FIRESTORE
        // =========================

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .get()

                .addOnSuccessListener(
                        queryDocumentSnapshots -> {

                            int happy = 0;
                            int excited = 0;
                            int calm = 0;
                            int neutral = 0;
                            int sad = 0;
                            int angry = 0;

                            // =========================
                            // READ EVERY DIARY
                            // =========================

                            for (
                                    DocumentSnapshot document
                                    : queryDocumentSnapshots
                            ) {

                                String mood =
                                        document.getString("mood");

                                if (mood == null ||
                                        mood.trim().isEmpty()) {

                                    continue;
                                }

                                String cleanMood =
                                        cleanMoodText(mood);

                                // =========================
                                // COUNT EMOTION
                                // =========================

                                switch (cleanMood) {

                                    case "Happy":

                                        happy++;

                                        break;

                                    case "Excited":

                                        excited++;

                                        break;

                                    case "Calm":

                                        calm++;

                                        break;

                                    case "Neutral":

                                        neutral++;

                                        break;

                                    case "Sad":

                                        sad++;

                                        break;

                                    case "Angry":

                                        angry++;

                                        break;
                                }
                            }

                            // =========================
                            // UPDATE PIE CHART
                            // =========================

                            if (emotionPieChart != null) {

                                emotionPieChart.setEmotionCounts(
                                        happy,
                                        excited,
                                        calm,
                                        neutral,
                                        sad,
                                        angry
                                );
                            }
                        }
                )

                .addOnFailureListener(
                        e -> {

                            /*
                             * If Firestore fails,
                             * keep the existing chart.
                             */
                        }
                );
    }

    // =====================================================
    // CLEAN MOOD
    // =====================================================

    private String cleanMoodText(
            String mood
    ) {

        if (mood == null) {

            return "";
        }

        String cleanMood =
                mood.trim();

        // =========================
        // REMOVE EMOJIS
        // =========================

        cleanMood =
                cleanMood
                        .replace("😊", "")
                        .replace("😍", "")
                        .replace("😌", "")
                        .replace("😐", "")
                        .replace("😢", "")
                        .replace("😡", "")
                        .trim();

        // =========================
        // REMOVE MARKDOWN
        // =========================

        cleanMood =
                cleanMood
                        .replace("*", "")
                        .replace("_", "")
                        .trim();

        // =========================
        // REMOVE PERIOD
        // =========================

        cleanMood =
                cleanMood
                        .replace(".", "")
                        .trim();

        // =========================
        // CASE NORMALIZATION
        // =========================

        if (cleanMood.equalsIgnoreCase("Happy")) {

            return "Happy";
        }

        if (cleanMood.equalsIgnoreCase("Excited")) {

            return "Excited";
        }

        if (cleanMood.equalsIgnoreCase("Calm")) {

            return "Calm";
        }

        if (cleanMood.equalsIgnoreCase("Neutral")) {

            return "Neutral";
        }

        if (cleanMood.equalsIgnoreCase("Sad")) {

            return "Sad";
        }

        if (cleanMood.equalsIgnoreCase("Angry")) {

            return "Angry";
        }

        return "";
    }

    // =====================================================
    // RESET SUMMARY
    // =====================================================

    private void resetSummary() {

        if (emotionPieChart != null) {

            emotionPieChart.setEmotionCounts(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            );
        }
    }
}