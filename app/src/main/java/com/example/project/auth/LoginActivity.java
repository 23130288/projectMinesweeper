package com.example.project.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.StartingMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import Model.AchievementManager;
import Model.Session;
import Model.User;
import Model.UserStats;
import database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;

    private TextView tvForgotPassword;
    private TextView tvRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
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

            Intent intent = new Intent(LoginActivity.this, StartingMenuActivity.class);
            startActivity(intent);
            finish();
        }

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);


        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)) {
                edtEmail.setError("Vui lòng nhập email");
                return;
            }
            if(TextUtils.isEmpty(password)) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this,task -> {
                        if (task.isSuccessful()) {
                            Session.isLoggedIn = true;
                            Session.email = email;

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String uid = firebaseUser != null ? firebaseUser.getUid() : null;

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

                            // database
                            DatabaseHelper helper = new DatabaseHelper(this);
                            helper.getWritableDatabase();

                            AchievementManager achievementManager = new AchievementManager();
                            achievementManager.unlockAchievement(uid, "login");

                            Toast.makeText(  this,   "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, StartingMenuActivity.class);
                            startActivity(intent);
                            finish();
            }else {
                   String errorMsg = task.getException() != null ? task.getException().getMessage() : "Sai email hoặc mật khẩu";
                            Toast.makeText(this, "Đăng nhập thất bại: " + errorMsg, Toast.LENGTH_LONG).show();

                        }
            });
        });

        tvForgotPassword.setOnClickListener(view ->{
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
                });

        tvRegister.setOnClickListener(view ->{
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
                });

    }
}