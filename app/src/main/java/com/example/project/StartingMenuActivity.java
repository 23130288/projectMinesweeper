package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.auth.LoginActivity;
import com.example.project.game.LeaderboardActivity;
import com.example.project.game.ModeMenuActivity;
import com.example.project.profile.UserInterfaceActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.Session;
import Model.User;
import Model.UserStats;

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

        ImageButton btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnLeaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(StartingMenuActivity.this, LeaderboardActivity.class);
            startActivity(intent);
        });

        Button btnQuit = findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.google.firebase.auth.FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String email = firebaseUser.getEmail();
            Session.isLoggedIn = true;
            Session.email = email;

            String uid = firebaseUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String name = document.getString("name");
                            Session.user = new User(uid, name, email, "", 0, "");

                            UserStats stats = new UserStats();
                            stats.setGamesPlayed(document.getLong("gamesPlayed") == null ? 0 : document.getLong("gamesPlayed").intValue());
                            stats.setGamesWon(document.getLong("gamesWon") == null ? 0 : document.getLong("gamesWon").intValue());
                            stats.setTotalTilesOpened(document.getLong("totalTilesOpened") == null ? 0 : document.getLong("totalTilesOpened").intValue());
                            Session.userStats = stats;
                        }
                    });
            com.example.project.utils.UserManager.fetchAndSyncSession(imgAvatar, null, null);
        } else {
            Session.isLoggedIn = false;
            Session.email = "";
            Session.coins = 0;
            Session.user = null;
            Session.userStats = null;
            imgAvatar.setImageResource(R.drawable.default_avatar);
        }
    }
}