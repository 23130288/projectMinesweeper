package com.example.project.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtEmailReset;
    private Button btnSendEmail;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Toast.makeText(this, "Bạn đã đăng nhập rồi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        edtEmailReset = findViewById(R.id.edtEmailReset);
        btnSendEmail = findViewById(R.id.btnSendEmail);

        btnSendEmail.setOnClickListener(v -> {
            String email = edtEmailReset.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ResetPasswordActivity.this, "Vui lòng nhập email của bạn!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this,
                                    "Liên kết đặt lại mật khẩu đã được gửi vào Email của bạn!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(ResetPasswordActivity.this,
                                    "Lỗi: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}