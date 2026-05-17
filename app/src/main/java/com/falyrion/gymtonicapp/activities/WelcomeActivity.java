package com.falyrion.gymtonicapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.falyrion.gymtonicapp.R;

public class WelcomeActivity extends AppCompatActivity {

    Button btnSignIn, btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        // Sign In button
        btnSignIn.setOnClickListener(v -> {

            Intent intent = new Intent(
                    WelcomeActivity.this,
                    LoginActivity.class
            );

            startActivity(intent);

        });

        // Create Account button
        btnCreateAccount.setOnClickListener(v -> {

            Intent intent = new Intent(
                    WelcomeActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);

        });
    }
}


