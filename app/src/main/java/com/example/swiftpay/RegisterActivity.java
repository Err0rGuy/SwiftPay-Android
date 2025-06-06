package com.example.swiftpay; // Replace with your actual package name

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.regex.Matcher;
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
    private TextView tvLoginPrompt; // Assuming you have this for navigation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Make sure this matches your XML file name

        // Initialize Views
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
        tvGoToLogin = findViewById(R.id.tvGoToLogin);// Initialize if you have it

        setupInputValidation();
        setupPasswordStrengthChecker();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateAllInputs()) {
                    // All inputs are valid, proceed with registration logic
                    // For example, make an API call, save to database, etc.
                    Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    // TODO: Implement actual registration logic here
                } else {
                    Toast.makeText(RegisterActivity.this, "Please correct the errors.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Optional: Handle click on login prompt

        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

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
        // Email, Password, and Confirm Password will have their errors cleared by their specific logic or on focus change.

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
                    tilEmail.setError(null); // Clear error if format becomes valid or field is empty
                }
            }
        });

        // Clear error on focus for password fields as their validation is more complex
        View.OnFocusChangeListener clearErrorOnFocus = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v.getParent() != null && v.getParent().getParent() instanceof TextInputLayout) {
                    ((TextInputLayout) v.getParent().getParent()).setError(null);
                }
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
                // Also validate confirm password if password changes
                if (edtConfirmPassword.getText() != null && !edtConfirmPassword.getText().toString().isEmpty()) {
                    validateConfirmPassword();
                } else {
                    tilConfirmPassword.setError(null); // Clear confirm password error if it's empty
                }
                tilPassword.setError(null); // Clear password field error as user types
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
            if (toLong) strengthScore += 20;   // Total 100% if all are met
        }

        // If you want the "all of these is 80%" logic, and then scale up:
        // This interpretation is a bit more complex if you want exactly 80% for the base set
        // and then distribute the remaining 20%.
        // Let's stick to a simpler additive score first, then adjust if needed.
        // The current model gives 20% for each of the 5 criteria.

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
        } else { // strengthScore == 100
            tvPasswordStrength.setText("Very Strong");
            tvPasswordStrength.setTextColor(Color.GREEN); // Or a darker green
        }
    }


    // Inside your RegisterActivity.java class

    private boolean validateAllInputs() {
        boolean isValid = true;

        // Validate First Name
        String firstName = Objects.requireNonNull(edtFirstName.getText()).toString().trim();
        if (firstName.isEmpty()) {
            tilFirstName.setError("First name cannot be empty");
            isValid = false;
        } else {
            tilFirstName.setError(null); // Clear error
        }

        // Validate Last Name
        String lastName = Objects.requireNonNull(edtLastName.getText()).toString().trim();
        if (lastName.isEmpty()) {
            tilLastName.setError("Last name cannot be empty");
            isValid = false;
        } else {
            tilLastName.setError(null); // Clear error
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
            tilEmail.setError(null); // Clear error
        }

        // Validate Password
        String password = Objects.requireNonNull(edtPassword.getText()).toString(); // No trim for password
        if (password.isEmpty()) {
            tilPassword.setError("Password cannot be empty");
            isValid = false;
        } else {
            // Further password strength validation can be implicitly handled by the strength checker,
            // but you might want to enforce a minimum strength here if the user hasn't typed enough
            // to trigger a "strong enough" state. For now, just checking if empty.
            // The strength checker provides visual feedback, this is for submission blocking.
            tilPassword.setError(null); // Clear error if not empty
        }

        // Validate Confirm Password (and ensure it matches password)
        if (!validateConfirmPassword()) { // This also checks if confirm password is empty
            isValid = false;
            // The error message for tilConfirmPassword is set within validateConfirmPassword()
        }


        // Optional: Check password strength before allowing submission
        // You could add a check here like:
         if (pbPasswordStrength.getProgress() < 80 && !password.isEmpty()) { // Example: require at least "Medium"
             tilPassword.setError("Password strength is not acceptable");
             isValid = false;
         }


        return isValid;
    }

    // You should already have this method from previous snippets
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
            tilConfirmPassword.setError(null); // Clear error
            return true;
        }
    }

    // The rest of your RegisterActivity.java code (onCreate, setup methods, updatePasswordStrength, etc.)
    // ...
}