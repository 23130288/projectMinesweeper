package com.example.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import Model.Session;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if(email.equals("admin@gmail.com") && password.equals("123456")) {
                Session.isLoggedIn = true;
                Session.email = email;
                Toast.makeText(  this,   "Đăng nhập thành công",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(this,"Sai email hoặc mật khẩu",Toast.LENGTH_SHORT).show();
            }
        });
    }
}