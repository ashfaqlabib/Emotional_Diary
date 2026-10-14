package com.example.emotionaldiary.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionaldiary.R;
import com.example.emotionaldiary.adapter.DiaryAdapter;
import com.example.emotionaldiary.models.DiaryModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiaryFragment extends Fragment {

    private RecyclerView recyclerDiary;
    private DiaryAdapter diaryAdapter;

    private TextInputEditText etSearchDiary;
    private TextView txtEmptyDiary;

    private final List<DiaryModel> diaryList =
            new ArrayList<>();

    private final List<DiaryModel> allDiaryList =
            new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_diary,
                container,
                false
        );

        // =========================
        // FIND VIEWS
        // =========================

        recyclerDiary =
                view.findViewById(R.id.recyclerDiary);

        etSearchDiary =
                view.findViewById(R.id.etSearchDiary);

        txtEmptyDiary =
                view.findViewById(R.id.txtEmptyDiary);

        // =========================
        // FIREBASE
        // =========================

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // =========================
        // RECYCLER VIEW
        // =========================

        recyclerDiary.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        diaryAdapter =
                new DiaryAdapter(diaryList);

        recyclerDiary.setAdapter(
                diaryAdapter
        );

        // =========================
        // SEARCH
        // =========================

        etSearchDiary.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterDiaries(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        return view;
    }

    // =========================
    // REFRESH WHEN SCREEN OPENS
    // =========================

    @Override
    public void onResume() {

        super.onResume();

        loadDiaries();
    }

    // =========================
    // LOAD ALL DIARIES
    // =========================

    private void loadDiaries() {

        FirebaseUser user =
                mAuth.getCurrentUser();

        if (user == null) {

            allDiaryList.clear();
            diaryList.clear();

            diaryAdapter.notifyDataSetChanged();

            updateEmptyState(
                    etSearchDiary.getText()
                            .toString()
            );

            return;
        }

        db.collection("users")
                .document(user.getUid())
                .collection("diaries")
                .orderBy(
                        "createdAt",
                        Query.Direction.DESCENDING
                )
                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            allDiaryList.clear();

                            for (
                                    com.google.firebase.firestore.DocumentSnapshot document
                                    : querySnapshot.getDocuments()
                            ) {

                                DiaryModel diary =
                                        document.toObject(
                                                DiaryModel.class
                                        );

                                if (diary != null) {

                                    allDiaryList.add(
                                            diary
                                    );
                                }
                            }

                            filterDiaries(
                                    etSearchDiary
                                            .getText()
                                            .toString()
                            );
                        }
                )

                .addOnFailureListener(
                        e -> {

                            if (!isAdded()) {
                                return;
                            }

                            showLoadError();
                        }
                );
    }

    // =========================
    // FILTER DIARIES
    // =========================

    private void filterDiaries(
            String searchText) {

        String query =
                searchText
                        .trim()
                        .toLowerCase(
                                Locale.getDefault()
                        );

        diaryList.clear();

        // No search
        if (query.isEmpty()) {

            diaryList.addAll(
                    allDiaryList
            );

        } else {

            // Search
            for (
                    DiaryModel diary
                    : allDiaryList
            ) {

                String title =
                        diary.getTitle() != null
                                ? diary.getTitle()
                                  .toLowerCase(
                                          Locale.getDefault()
                                  )
                                : "";

                String content =
                        diary.getContent() != null
                                ? diary.getContent()
                                  .toLowerCase(
                                          Locale.getDefault()
                                  )
                                : "";

                String mood =
                        diary.getMood() != null
                                ? diary.getMood()
                                  .toLowerCase(
                                          Locale.getDefault()
                                  )
                                : "";

                if (
                        title.contains(query)
                                || content.contains(query)
                                || mood.contains(query)
                ) {

                    diaryList.add(
                            diary
                    );
                }
            }
        }

        diaryAdapter.notifyDataSetChanged();

        updateEmptyState(
                searchText
        );
    }

    // =========================
    // EMPTY STATE
    // =========================

    private void updateEmptyState(
            String searchText) {

        if (!diaryList.isEmpty()) {

            txtEmptyDiary.setVisibility(
                    View.GONE
            );

            recyclerDiary.setVisibility(
                    View.VISIBLE
            );

            return;
        }

        recyclerDiary.setVisibility(
                View.GONE
        );

        txtEmptyDiary.setVisibility(
                View.VISIBLE
        );

        String query =
                searchText
                        .trim();

        if (query.isEmpty()) {

            txtEmptyDiary.setText(
                    "No Diaries Yet\n\n"
                            + "Start writing your first "
                            + "personal memory."
            );

        } else {

            txtEmptyDiary.setText(
                    "No Matching Diaries\n\n"
                            + "We couldn't find any diary "
                            + "matching your search."
            );
        }
    }

    // =========================
    // LOAD ERROR
    // =========================

    private void showLoadError() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Unable to Load Diaries"
                )
                .setMessage(
                        "We couldn't load your diaries right now. "
                                + "Please try again."
                )
                .setPositiveButton(
                        "Done",
                        null
                )
                .show();
    }
}