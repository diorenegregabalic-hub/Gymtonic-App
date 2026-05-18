package com.falyrion.gymtonicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.falyrion.gymtonicapp.Activity_Main;
import com.falyrion.gymtonicapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI Elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Handle Registration Process Configuration
        btnRegister.setOnClickListener(v -> {
            String username = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Client-Side Structural Inputs Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "All input fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Password Integrity and Match Verification
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proceed to API interaction if checks pass
            executeRegistrationRequest(username, email, password);
        });
    }

    /**
     * Executes an HTTP POST Request to create a new user profile record in the database backend.
     */
    private void executeRegistrationRequest(String username, String email, String password) {
        String url = "http://10.0.2.2:3000/api/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        String message = response.getString("message");

                        if (success) {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                            // Redirect to Main Activity dashboard following registration success
                            Intent intent = new Intent(RegisterActivity.this, Activity_Main.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Data parsing exception occurred.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String errorData = new String(error.networkResponse.data);
                            JSONObject errorJson = new JSONObject(errorData);
                            String message = errorJson.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Registration process failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Server connection timeout. Ensure network execution is active.", Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}