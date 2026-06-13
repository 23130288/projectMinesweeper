package com.example.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtEmailReset;
    private Button btnSendEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        mAuth = FirebaseAuth.getInstance();

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
    }
}