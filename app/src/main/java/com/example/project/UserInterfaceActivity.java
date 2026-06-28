package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.game.AchievementDialog;
import com.example.project.game.NewGameDialog;
import com.google.firebase.auth.FirebaseAuth;

import Model.Session;

public class UserInterfaceActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interface);

        mAuth = FirebaseAuth.getInstance();

        Button btnAchievement = findViewById(R.id.btnAchievements);
        btnAchievement.setOnClickListener(v -> {
            AchievementDialog dialog = new AchievementDialog();
            dialog.show(getSupportFragmentManager(), "ACHIEVEMENT_DIALOG");
        });

        Button btnLogOut = findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(v -> {
            mAuth.signOut();

            Session.isLoggedIn = false;
            Session.email = "";
            Session.user = null;
            Session.userStats = null;
            Intent intent = new Intent(this, StartingMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
