package com.falyrion.gymtonicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.falyrion.gymtonicapp.Activity_Main;
import com.falyrion.gymtonicapp.R;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {

            // diri nimo ibutang imong register/save account code

            Intent intent = new Intent(RegisterActivity.this, Activity_Main.class);
            startActivity(intent);

            finish();
        });
    }
}