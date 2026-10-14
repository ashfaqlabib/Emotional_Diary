package com.example.emotionaldiary.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotionaldiary.R;
import com.example.emotionaldiary.ViewDiaryDetailsActivity;
import com.example.emotionaldiary.models.DiaryModel;

import java.util.List;

public class DiaryAdapter
        extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {

    private final List<DiaryModel> diaryList;

    public DiaryAdapter(List<DiaryModel> diaryList) {
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_diary,
                        parent,
                        false
                );

        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DiaryViewHolder holder,
            int position) {

        DiaryModel diary = diaryList.get(position);

        // Title
        holder.txtDiaryTitle.setText(
                diary.getTitle()
        );

        // Mood
        holder.txtDiaryMood.setText(
                diary.getMood()
        );

        // Date
        holder.txtDiaryDate.setText(
                diary.getDate()
        );

        // Content preview
        String content = diary.getContent();

        if (content != null && content.length() > 100) {

            content = content.substring(0, 100)
                    + "...";
        }

        holder.txtDiaryContent.setText(content);

        // Open full diary
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    v.getContext(),
                    ViewDiaryDetailsActivity.class
            );

            intent.putExtra(
                    "diaryId",
                    diary.getId()
            );

            intent.putExtra(
                    "title",
                    diary.getTitle()
            );

            intent.putExtra(
                    "content",
                    diary.getContent()
            );

            intent.putExtra(
                    "mood",
                    diary.getMood()
            );

            intent.putExtra(
                    "date",
                    diary.getDate()
            );

            intent.putExtra(
                    "time",
                    diary.getTime()
            );

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    static class DiaryViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtDiaryTitle;
        TextView txtDiaryContent;
        TextView txtDiaryMood;
        TextView txtDiaryDate;

        DiaryViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtDiaryTitle =
                    itemView.findViewById(
                            R.id.txtDiaryTitle
                    );

            txtDiaryContent =
                    itemView.findViewById(
                            R.id.txtDiaryContent
                    );

            txtDiaryMood =
                    itemView.findViewById(
                            R.id.txtDiaryMood
                    );

            txtDiaryDate =
                    itemView.findViewById(
                            R.id.txtDiaryDate
                    );
        }
    }
}