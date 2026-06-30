package com.example.project.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.StartingMenuActivity;
import com.example.project.game.AchievementDialog;
import com.example.project.game.ModeMenuActivity;
import com.example.project.utils.CropImageHelper;
import com.example.project.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import Model.Session;

public class UserInterfaceActivity extends AppCompatActivity {

    private static final String TAG = "USER_INTERFACE";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ShapeableImageView imgAvatar;
    private TextView txtName;
    private FloatingActionButton btnEditAvatar;
    private ImageButton btnEditName;
    private CropImageHelper cropImageHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interface);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        btnEditName = findViewById(R.id.btnEditName);

        UserManager.fetchAndSyncSession(imgAvatar, txtName, null);

        cropImageHelper = new CropImageHelper(this, (bitmap, base64String) -> {
            if (!base64String.isEmpty()) {
                saveAvatarToFirestore(base64String);
            }
        });

        btnEditAvatar.setOnClickListener(v -> cropImageHelper.openGallery());

        btnEditName.setOnClickListener(v -> showEditNameDialog());

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

        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        Button btnPlayGame = findViewById(R.id.btnPlayGame);
        btnPlayGame.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModeMenuActivity.class);
            startActivity(intent);
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi tên người dùng");

        final EditText input = new EditText(this);
        input.setText(txtName.getText().toString());
        input.setSelectAllOnFocus(true);
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateNameInFirestore(newName);
            } else {
                Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateNameInFirestore(String newName) {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {
                    if (Session.user != null) {
                        Session.user.name = newName;
                    }
                    UserManager.updateUi(imgAvatar, txtName);

                    Toast.makeText(this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update name", e));
    }

    private void saveAvatarToFirestore(String base64String) {
        if (mAuth.getCurrentUser() == null) return;

        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .update("avatar", base64String)
                .addOnSuccessListener(aVoid -> {
                    if (Session.user != null) {
                        Session.user.avatar = base64String;
                    }
                    UserManager.updateUi(imgAvatar, txtName);

                    Toast.makeText(this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update Firestore", e));
    }
}