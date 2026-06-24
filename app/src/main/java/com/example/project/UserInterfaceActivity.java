package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import Model.Session;

public class UserInterfaceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_interface);

        Button btnLogOut = findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(v -> {
            Session.isLoggedIn = false;
            Session.email = "";
            Intent intent = new Intent(this, StartingMenuActivity.class);
            startActivity(intent);
        });
    }
}
