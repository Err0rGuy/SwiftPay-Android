package com.example.swiftpay; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilLoginEmail;
    private TextInputEditText edtLoginEmail;
    private TextInputLayout tilLoginPassword;
    private TextInputEditText edtLoginPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    // Optional: private TextView tvForgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This assumes you have set a NoActionBar theme in your styles.xml or AndroidManifest.xml
        // If not, and you want to remove it programmatically for this activity:
        // supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        // Initialize Views
        tilLoginEmail = findViewById(R.id.tilLoginEmail);
        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        tilLoginPassword = findViewById(R.id.tilLoginPassword);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        // tvForgotPassword = findViewById(R.id.tvForgotPassword); // Uncomment if you have it and want to use it

        setupInputValidationListeners();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // --- TODO: Implement actual login logic here ---
                    // For example, authenticate with Firebase, your own backend, etc.
                    String email = Objects.requireNonNull(edtLoginEmail.getText()).toString().trim();
                    String password = Objects.requireNonNull(edtLoginPassword.getText()).toString(); // No trim for password

                    // Placeholder for successful login
                    Toast.makeText(LoginActivity.this, "Login attempt for: " + email, Toast.LENGTH_SHORT).show();
                    // Example: Navigate to a MainActivity after successful login
                    // Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // startActivity(intent);
                    // finish(); // Optional: finish LoginActivity so user can't go back to it
                } else {
                    Toast.makeText(LoginActivity.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

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

    private void setupInputValidationListeners() {
        // Clear error when user starts typing in email field
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

        // Clear error when user starts typing in password field
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
        String password = Objects.requireNonNull(edtLoginPassword.getText()).toString(); // No trim for password

        // Validate Email
        if (email.isEmpty()) {
            tilLoginEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilLoginEmail.setError("Invalid email format");
            isValid = false;
        } else {
            tilLoginEmail.setError(null); // Clear error
        }

        // Validate Password
        if (password.isEmpty()) {
            tilLoginPassword.setError("Password cannot be empty");
            isValid = false;
        } else {
            tilLoginPassword.setError(null); // Clear error
        }

        return isValid;
    }
}