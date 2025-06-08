package com.example.swiftpay; // Replace with your actual package name

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftpay.logic.API.APIService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progressSpinnerStart;
    private TextInputLayout tilLoginEmail;
    private TextInputEditText edtLoginEmail;
    private TextInputLayout tilLoginPassword;
    private TextInputEditText edtLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;


    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Views
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        progressSpinnerStart = findViewById(R.id.progressSpinnerStart);
        progressSpinnerStart.setVisibility(View.GONE);
        // tvForgotPassword = findViewById(R.id.tvForgotPassword);

        setupInputValidationListeners();

        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                sendData();
            } else {
                Toast.makeText(LoginActivity.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoToRegister.setOnClickListener(v -> navigateToSignup());

        // Optional: Forgot Password Listener
        // if (tvForgotPassword != null) {
        //     tvForgotPassword.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View v) {
        //             // TODO: Implement forgot password flow
        //             Toast.makeText(LoginActivity.this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();
        //         }
        //     });
        // }
    }

    private void navigateToSignup(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupInputValidationListeners() {
        edtLoginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilLoginEmail.getError() != null) {
                    tilLoginEmail.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtLoginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilLoginPassword.getError() != null) {
                    tilLoginPassword.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String email = Objects.requireNonNull(edtLoginEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(edtLoginPassword.getText()).toString();

        if (email.isEmpty()) {
            tilLoginEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilLoginEmail.setError("Invalid email format");
            isValid = false;
        } else {
            tilLoginEmail.setError(null);
        }

        if (password.isEmpty()) {
            tilLoginPassword.setError("Password cannot be empty");
            isValid = false;
        } else {
            tilLoginPassword.setError(null);
        }

        return isValid;
    }

    private void sendData() {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("email", Objects.requireNonNull(edtLoginEmail.getText()).toString().trim());
        userData.put("password", Objects.requireNonNull(edtLoginPassword.getText()).toString().trim());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            handler.post(() -> progressSpinnerStart.setVisibility(View.VISIBLE));
            HashMap<String, Object> response = APIService.login(userData);

            handler.post(() -> {
                int code = (int) response.getOrDefault("code", 0);
                String message = (String) response.getOrDefault("message", "Unknown error");

                if (code == 200) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                } else {
                    if (message.contains("server"))
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    else if(message.contains("Email"))
                        tilLoginEmail.setError(message);
                    else if (message.contains("password"))
                        tilLoginPassword.setError(message);
                    else
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
            handler.post(() -> progressSpinnerStart.setVisibility(View.GONE));
        });
    }

}