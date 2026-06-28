package com.example.project.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Model.Achievement;
import Model.Session;
import Service.AchievementService;

public class AchievementDialog extends DialogFragment {
    private RecyclerView rvAchievements;
    private AchievementAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_achievement, container, false);

        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());

        rvAchievements = view.findViewById(R.id.rvAchievements);
        rvAchievements.setLayoutManager(new LinearLayoutManager(requireContext()));

        AchievementService as = new AchievementService(getContext());
        List<Achievement> achievements = as.getAllAchievements();

        adapter = new AchievementAdapter(achievements);
        rvAchievements.setAdapter(adapter);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Session.user.uid)
                .collection("achievements")
                .get()
                .addOnSuccessListener(query -> {
                    Set<String> unlocked = new HashSet<>();
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        unlocked.add(doc.getId());
                    }
                    for (Achievement achievement : achievements) {
                        achievement.setUnlocked(unlocked.contains(achievement.getAid()));
                    }
                    adapter.notifyDataSetChanged();
                });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
