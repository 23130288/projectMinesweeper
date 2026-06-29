package com.example.project.game;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project.R;

public class NewGameDialog extends DialogFragment {
    private int rows = 9;
    private int cols = 9;
    private int mines = 10;
    private String diff = "easy";
    private Button btnEasy, btnMedium, btnHard, btnExtreme;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_game, container, false);

        Button btnCloseSetup = view.findViewById(R.id.btnCloseSetup);
        btnCloseSetup.setOnClickListener(v -> dismiss());

        btnEasy = view.findViewById(R.id.btnEasy);
        btnMedium = view.findViewById(R.id.btnMedium);
        btnHard = view.findViewById(R.id.btnHard);
        btnExtreme = view.findViewById(R.id.btnExtreme);
        Button btnStart = view.findViewById(R.id.btnStart);
        selectDifficulty(btnEasy);
        btnEasy.setOnClickListener(v -> {
            selectDifficulty(btnEasy);
            rows = 9;
            cols = 9;
            mines = 10;
            diff = "easy";
        });
        btnMedium.setOnClickListener(v -> {
            selectDifficulty(btnMedium);
            rows = 16;
            cols = 16;
            mines = 40;
            diff = "medium";
        });
        btnHard.setOnClickListener(v -> {
            selectDifficulty(btnHard);
            rows = 16;
            cols = 30;
            mines = 99;
            diff = "hard";
        });
        btnExtreme.setOnClickListener(v -> {
            selectDifficulty(btnExtreme);
            rows = 24;
            cols = 30;
            mines = 160;
            diff = "extreme";
        });
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), GameClassicActivity.class);
            intent.putExtra("rows", rows);
            intent.putExtra("columns", cols);
            intent.putExtra("mines", mines);
            intent.putExtra("diff", diff);
            startActivity(intent);
            dismiss();
        });

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
    private void selectDifficulty(Button selectedButton) {
        btnEasy.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnMedium.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnHard.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        btnExtreme.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        selectedButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bomb_check, 0, 0, 0);
    }
}
