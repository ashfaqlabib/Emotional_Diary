package com.example.emotionaldiary;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionaldiary.adapter.DiaryAdapter;
import com.example.emotionaldiary.models.DiaryModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ViewDiaryActivity extends AppCompatActivity {

    private RecyclerView recyclerDiary;
    private DiaryAdapter adapter;

    private final List<DiaryModel> diaryList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_diary);

        recyclerDiary = findViewById(R.id.recyclerDiary);

        recyclerDiary.setLayoutManager(
                new LinearLayoutManager(this)
        );

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        adapter = new DiaryAdapter(diaryList);

        recyclerDiary.setAdapter(adapter);

        loadDiaries();
    }

    private void loadDiaries() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
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
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    diaryList.clear();

                    for (var document :
                            queryDocumentSnapshots.getDocuments()) {

                        DiaryModel diary =
                                document.toObject(
                                        DiaryModel.class
                                );

                        if (diary != null) {
                            diaryList.add(diary);
                        }
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ViewDiaryActivity.this,
                            "Failed to load diaries",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mAuth != null) {
            loadDiaries();
        }
    }
}