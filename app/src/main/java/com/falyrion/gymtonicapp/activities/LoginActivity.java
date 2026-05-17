package com.falyrion.gymtonicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.falyrion.gymtonicapp.Activity_Main;
import com.falyrion.gymtonicapp.R;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {

            // diri nimo ibutang imong login validation

            Intent intent = new Intent(LoginActivity.this, Activity_Main.class);
            startActivity(intent);

            finish();
        });
    }
}