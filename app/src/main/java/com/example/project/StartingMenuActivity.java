package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.imageview.ShapeableImageView;

import Model.Session;

public class StartingMenuActivity extends AppCompatActivity {
    private ShapeableImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.starting_menu);

        imgAvatar = findViewById(R.id.imgAvatar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnPlay = findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(v -> {
            Intent intent = new Intent(StartingMenuActivity.this, ModeMenuActivity.class);
            startActivity(intent);
        });

        imgAvatar.setOnClickListener(v -> {
            Intent intent;
            if (Session.isLoggedIn) {
                intent = new Intent(StartingMenuActivity.this, UserInterfaceActivity.class);
            } else {
                intent = new Intent(StartingMenuActivity.this, LoginActivity.class);
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Session.isLoggedIn){
            imgAvatar.setImageResource(R.drawable.avatar_admin);
        }
        else{
            imgAvatar.setImageResource(R.drawable.default_avatar);
        }
    }
}