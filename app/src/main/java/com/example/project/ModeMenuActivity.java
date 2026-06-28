package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.game.NewGameDialog;
import com.google.android.material.card.MaterialCardView;

public class ModeMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_menu);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {finish();});

        Button btnModeClassic = findViewById(R.id.btnModeClassic);
        btnModeClassic.setOnClickListener(v -> {
            NewGameDialog dialog = new NewGameDialog();
            dialog.show(getSupportFragmentManager(), "NEW_GAME_DIALOG");
        });
    }
}
