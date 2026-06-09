package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class ModeMenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mode_menu);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {finish();});

        Button btnModeClassic = findViewById(R.id.btnModeClassic);
        MaterialCardView layoutSetupGame = findViewById(R.id.layoutSetupGame);
        btnModeClassic.setOnClickListener(v -> layoutSetupGame.setVisibility(View.VISIBLE));

        Button btnCloseSetup = findViewById(R.id.btnCloseSetup);
        btnCloseSetup.setOnClickListener(v -> layoutSetupGame.setVisibility(View.GONE));

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(ModeMenuActivity.this, GameClassicActivity.class);
            startActivity(intent);
        });
    }
}
