package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import Model.Session;

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

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);


        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(TextUtils.isEmpty(password)) {
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
                            Toast.makeText(  this,   "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this,StartingMenuActivity.class);
                            startActivity(intent);
                            finish();
            }else {
                   String errorMsg = task.getException() != null ? task.getException().getMessage() : "Sai email hoặc mật khẩu";
                            Toast.makeText(this, "Đăng nhập thất bại: " + errorMsg, Toast.LENGTH_LONG).show();

                        }
            });
        });

        tvForgotPassword.setOnClickListener(view ->
                Toast.makeText(this,"Chức năng Quên mật khẩu đang được xử lý" , Toast.LENGTH_SHORT).show()
                );
        tvRegister.setOnClickListener(view ->
                Toast.makeText(this,"Chức năng Đăng ký đang được xử lý" , Toast.LENGTH_SHORT).show()
                );
    }
}