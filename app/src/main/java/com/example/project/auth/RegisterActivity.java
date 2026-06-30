package com.example.project.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.R;
import com.example.project.profile.UserInterfaceActivity;
import com.example.project.utils.CropImageHelper;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


import Model.Session;
import Model.User;
import Model.UserStats;
import database.AchievementFirebase;
import database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private ShapeableImageView imgRegisterAvatar;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private Button btnRegister;

    private FirebaseAuth mAuth;
    private String encodedAvatarBase64 = "";
    private CropImageHelper cropImageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        imgRegisterAvatar = findViewById(R.id.imgRegisterAvatar);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        cropImageHelper = new CropImageHelper(this, (bitmap, base64String) -> {
            imgRegisterAvatar.setImageBitmap(bitmap);
            encodedAvatarBase64 = base64String;
        });

        imgRegisterAvatar.setOnClickListener(v -> cropImageHelper.openGallery());

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirm = edtConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải lớn hơn hoặc bằng 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("uid", uid);
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("avatar", encodedAvatarBase64);

                            userMap.put("gamesPlayed", 0);
                            userMap.put("gamesWon", 0);
                            userMap.put("totalTilesOpened", 0);

                            db.collection("users")
                                    .document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener(unused -> {

                                        // Khởi tạo Session giống LoginActivity
                                        Session.isLoggedIn = true;
                                        Session.email = email;

                                        Session.user = new User(uid, name, email, "", 0, encodedAvatarBase64);

                                        UserStats stats = new UserStats();
                                        stats.setGamesPlayed(0);
                                        stats.setGamesWon(0);
                                        stats.setTotalTilesOpened(0);
                                        Session.userStats = stats;

                                        // Khởi tạo Database SQLite nếu cần
                                        DatabaseHelper helper = new DatabaseHelper(RegisterActivity.this);
                                        helper.getWritableDatabase();

                                        // Mở thành tựu đăng nhập nếu bạn dùng
                                        AchievementFirebase af = new AchievementFirebase();
                                        af.unlockAchievement(uid, "login");

                                        Toast.makeText(RegisterActivity.this,
                                                "Đăng ký thành công!",
                                                Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegisterActivity.this, UserInterfaceActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this,
                                                "Lưu thông tin thất bại: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}