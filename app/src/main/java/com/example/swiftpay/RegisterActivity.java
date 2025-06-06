package com.example.swiftpay; // Replace with your actual package name

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilFirstName;
    private TextInputEditText edtFirstName;
    private TextInputLayout tilLastName;
    private TextInputEditText edtLastName;
    private TextInputLayout tilEmail;
    private TextInputEditText edtEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText edtPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText edtConfirmPassword;
    private ProgressBar pbPasswordStrength;
    private TextView tvPasswordStrength;

    private TextView tvGoToLogin;
    private Button btnSignUp;
    private TextView tvLoginPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize
        tilFirstName = findViewById(R.id.tilFirstName);
        edtFirstName = findViewById(R.id.edtFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        edtLastName = findViewById(R.id.edtLastName);
        tilEmail = findViewById(R.id.tilEmail);
        edtEmail = findViewById(R.id.edtEmail);
        tilPassword = findViewById(R.id.tilPassword);
        edtPassword = findViewById(R.id.edtPassword);
        tilConfirmPassword = findViewById(R.id.tilPasswordConfirm);
        edtConfirmPassword = findViewById(R.id.edtPasswordConfirm);
        pbPasswordStrength = findViewById(R.id.pbPasswordStrength);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLoginPrompt = findViewById(R.id.tvLoginPrompt);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        setupInputValidation();
        setupPasswordStrengthChecker();

        btnSignUp.setOnClickListener(v -> {
            if (validateAllInputs()) {
                sendData();
            } else {
                Toast.makeText(RegisterActivity.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
            }
        });
        tvGoToLogin.setOnClickListener(v -> navigateToLogin());
    }
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void setupInputValidation() {
        // Clear error when user starts typing
        TextWatcher clearErrorTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                View focusedView = getCurrentFocus();
                if (focusedView != null && focusedView.getParent() != null && focusedView.getParent().getParent() instanceof TextInputLayout) {
                    ((TextInputLayout) focusedView.getParent().getParent()).setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtFirstName.addTextChangedListener(clearErrorTextWatcher);
        edtLastName.addTextChangedListener(clearErrorTextWatcher);

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    tilEmail.setError("Invalid email format");
                } else {
                    tilEmail.setError(null);
                }
            }
        });

        View.OnFocusChangeListener clearErrorOnFocus = (v, hasFocus) -> {
            if (hasFocus && v.getParent() != null && v.getParent().getParent() instanceof TextInputLayout) {
                ((TextInputLayout) v.getParent().getParent()).setError(null);
            }
        };
        edtPassword.setOnFocusChangeListener(clearErrorOnFocus);
        edtConfirmPassword.setOnFocusChangeListener(clearErrorOnFocus);
    }

    private void setupPasswordStrengthChecker() {
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                updatePasswordStrength(password);

                if (edtConfirmPassword.getText() != null && !edtConfirmPassword.getText().toString().isEmpty()) {
                    validateConfirmPassword();
                } else {
                    tilConfirmPassword.setError(null);
                }
                tilPassword.setError(null);
            }
        });

        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updatePasswordStrength(String password) {
        int strengthScore = 0;

        if (password.isEmpty()) {
            pbPasswordStrength.setProgress(0);
            tvPasswordStrength.setText("");
            tvPasswordStrength.setTextColor(Color.GRAY);
            return;
        }

        boolean hasLength = password.length() >= 8;
        boolean toLong = password.length() >= 11;
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`]").matcher(password).find();

        if (hasUpper) strengthScore += 15;
        if (hasLower) strengthScore += 15;
        if (hasDigit) strengthScore += 15;
        if (hasSpecial) strengthScore +=15;
        if (hasUpper && hasLower && hasDigit && hasSpecial){
            if (hasLength) strengthScore += 20;
            if (toLong) strengthScore += 20;   // Total 100%
        }

        pbPasswordStrength.setProgress(strengthScore);

        if (strengthScore < 40) {
            tvPasswordStrength.setText("Weak");
            tvPasswordStrength.setTextColor(Color.RED);
        } else if (strengthScore < 80) {
            tvPasswordStrength.setText("Medium");
            tvPasswordStrength.setTextColor(Color.rgb(255, 165, 0)); // Orange
        } else if (strengthScore < 100) {
            tvPasswordStrength.setText("Strong");
            tvPasswordStrength.setTextColor(Color.GREEN);
        } else {
            tvPasswordStrength.setText("Very Strong");
            tvPasswordStrength.setTextColor(Color.GREEN); // Or a darker green
        }
    }

    private boolean validateAllInputs() {
        boolean isValid = true;

        // Validate First Name
        String firstName = Objects.requireNonNull(edtFirstName.getText()).toString().trim();
        if (firstName.isEmpty()) {
            tilFirstName.setError("First name cannot be empty");
            isValid = false;
        } else {
            tilFirstName.setError(null);
        }

        // Validate Last Name
        String lastName = Objects.requireNonNull(edtLastName.getText()).toString().trim();
        if (lastName.isEmpty()) {
            tilLastName.setError("Last name cannot be empty");
            isValid = false;
        } else {
            tilLastName.setError(null);
        }

        // Validate Email
        String email = Objects.requireNonNull(edtEmail.getText()).toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate Password
        String password = Objects.requireNonNull(edtPassword.getText()).toString();
        if (password.isEmpty()) {
            tilPassword.setError("Password cannot be empty");
            isValid = false;
        } else
            tilPassword.setError(null);

        if (!validateConfirmPassword())
            isValid = false;

         if (pbPasswordStrength.getProgress() < 80 && !password.isEmpty()) { // Example: require at least "Medium"
             tilPassword.setError("Password strength is not acceptable");
             isValid = false;
         }


        return isValid;
    }

    private boolean validateConfirmPassword() {
        String password = Objects.requireNonNull(edtPassword.getText()).toString();
        String confirmPassword = Objects.requireNonNull(edtConfirmPassword.getText()).toString();

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Confirm password cannot be empty");
            return false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            return false;
        } else {
            tilConfirmPassword.setError(null);
            return true;
        }
    }

    private void sendData() {
        HashMap<String, String> userData = new HashMap<>();
        userData.put("first_name", Objects.requireNonNull(edtFirstName.getText()).toString().trim());
        userData.put("last_name", Objects.requireNonNull(edtLastName.getText()).toString().trim());
        userData.put("email", Objects.requireNonNull(edtEmail.getText()).toString().trim());
        userData.put("password", Objects.requireNonNull(edtPassword.getText()).toString().trim());
        userData.put("password_confirmation", Objects.requireNonNull(edtConfirmPassword.getText()).toString());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            HashMap<String, Object> response = APIService.register(userData);

            handler.post(() -> {
                int code = (int) response.getOrDefault("code", 0);
                String message = (String) response.getOrDefault("message", "Unknown error");

                if (code == 201) {
                    Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                } else {
                    if (message.contains("server"))
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    else if (message.contains("email"))
                        tilEmail.setError(message);
                    else if (message.contains("first_name"))
                        tilFirstName.setError(message);
                    else if (message.contains("last_name"))
                        tilFirstName.setError(message);
                    else if (message.contains("password"))
                        tilFirstName.setError(message);
                    else if (message.contains("password_confirmation"))
                        tilFirstName.setError(message);


                }
            });
        });
    }


}