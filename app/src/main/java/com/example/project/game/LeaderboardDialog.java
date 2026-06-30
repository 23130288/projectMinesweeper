package com.example.project.game;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import Model.LeaderBoard;

public class LeaderboardDialog extends DialogFragment {
    private String difficulty;
    private LeaderboardAdapter adapter;
    private final List<LeaderBoard> leaderBoards = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_leaderboard, container, false);

        RecyclerView rvLeaderboard = view.findViewById(R.id.rvLeaderboard);
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LeaderboardAdapter(leaderBoards);
        rvLeaderboard.setAdapter(adapter);

        TextView txtDiff = view.findViewById(R.id.txtDifficulty);
        txtDiff.setText(difficulty);

        FirebaseFirestore.getInstance()
                .collection("Leaderboards")
                .document("classic")
                .collection(difficulty)
                .orderBy("completedTime", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    leaderBoards.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d("Leaderboard", doc.getData().toString());
                        LeaderBoard item = new LeaderBoard();
                        item.setUserId(doc.getString("userId"));
                        item.setPlayerName(doc.getString("username"));
                        Log.d("Leaderboard", "username = " + item.getPlayerName());
                        Long score = doc.getLong("score");
                        item.setScore(score == null ? 0 : score.intValue());
                        Long completedTime = doc.getLong("completedTime");
                        item.setCompletedTime(completedTime == null ? 0 : completedTime.intValue());
                        item.setCompletedAt(doc.getTimestamp("completedAt"));
                        leaderBoards.add(item);
                    }
                    adapter.notifyDataSetChanged();
                });
        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void setDifficulty(String diff) {
        this.difficulty = diff;
    }
}
