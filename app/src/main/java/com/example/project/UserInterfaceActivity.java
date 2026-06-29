package com.example.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.project.firebase.ChangePasswordActivity;
import com.example.project.game.AchievementDialog;
import com.example.project.utils.ImageUtils;
import com.example.project.utils.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yalantis.ucrop.UCrop;
import java.io.File;
import Model.Session;

public class UserInterfaceActivity extends AppCompatActivity {

    private static final String TAG = "USER_INTERFACE";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ShapeableImageView imgAvatar;
    private TextView txtName;
    private FloatingActionButton btnEditAvatar;
    private ImageButton btnEditName;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        startUCrop(imageUri);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> uCropLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri resultUri = UCrop.getOutput(result.getData());
                    if (resultUri != null) {
                        processAndSaveAvatar(resultUri);
                    }
                } else if (result.getResultCode() == UCrop.RESULT_ERROR && result.getData() != null) {
                    Throwable cropError = UCrop.getError(result.getData());
                    if (cropError != null) Log.e(TAG, "uCrop error: " + cropError.getMessage());
                }
            }
    );

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

        btnEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

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

    private void startUCrop(Uri sourceUri) {
        File destinationFile = new File(getCacheDir(), "cropped_avatar.jpg");
        Uri destinationUri = Uri.fromFile(destinationFile);

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(85);
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);

        Intent uCropIntent = UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(200, 200)
                .withOptions(options)
                .getIntent(this);

        uCropLauncher.launch(uCropIntent);
    }

    private void processAndSaveAvatar(Uri imageUri) {
        Bitmap croppedBitmap = ImageUtils.uriToBitmap(this, imageUri);
        if (croppedBitmap == null) return;

        String base64String = ImageUtils.bitmapToBase64(croppedBitmap, 85);
        if (!base64String.isEmpty()) {
            saveAvatarToFirestore(base64String);
        }
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