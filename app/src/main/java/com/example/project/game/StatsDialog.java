package com.example.project.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project.R;

import Model.Session;
import Model.UserStats;

public class StatsDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_stats, container, false);

        TextView tvGamesPlayed = view.findViewById(R.id.tvGamesPlayed);
        TextView tvGamesWon = view.findViewById(R.id.tvGamesWon);
        TextView tvTilesOpened = view.findViewById(R.id.tvTilesOpened);

        UserStats stats = Session.userStats;
        tvGamesPlayed.setText("Games Played: " + stats.getGamesPlayed());
        tvGamesWon.setText("Games Won: " + stats.getGamesWon());
        tvTilesOpened.setText("Tiles Opened: " + stats.getTotalTilesOpened());

        Button btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout((int)(getResources().getDisplayMetrics().widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
