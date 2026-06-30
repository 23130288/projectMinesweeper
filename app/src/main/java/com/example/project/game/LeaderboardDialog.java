package com.example.project.game;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
        txtDiff.setText(difficulty != null ? difficulty : "Easy");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String diffPath = difficulty != null ? difficulty.toLowerCase() : "easy";

        db.collection("Leaderboards")
                .document("classic")
                .collection(diffPath)
                .orderBy("score", Query.Direction.DESCENDING)
                .orderBy("completedTime", Query.Direction.ASCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {
                    leaderBoards.clear();

                    for (DocumentSnapshot doc : snapshot) {
                        LeaderBoard item = new LeaderBoard();
                        String uid = doc.getString("userId");
                        if (uid == null || uid.isEmpty()) {
                            uid = doc.getId();
                        }
                        item.setUserId(uid);
                        item.setPlayerName("Đang tải...");

                        Long score = doc.getLong("score");
                        item.setScore(score == null ? 0 : score.intValue());

                        Long time = doc.getLong("completedTime");
                        item.setCompletedTime(time == null ? 0 : time.intValue());

                        item.setCompletedAt(doc.getTimestamp("completedAt"));
                        leaderBoards.add(item);
                    }

                    adapter.notifyDataSetChanged();

                    if (!leaderBoards.isEmpty()) {
                        loadUserNamesSynchronized();
                    }
                });

        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        return view;
    }

    private void loadUserNamesSynchronized() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final int total = leaderBoards.size();
        final int[] counter = {0};

        for (LeaderBoard item : leaderBoards) {
            String uid = item.getUserId();

            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                            String name = task.getResult().getString("name");
                            if (name != null && !name.isEmpty()) {
                                item.setPlayerName(name);
                            }
                        }

                        counter[0]++;
                        if (counter[0] == total) {
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
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