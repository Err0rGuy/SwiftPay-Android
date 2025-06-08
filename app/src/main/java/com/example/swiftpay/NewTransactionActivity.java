package com.example.swiftpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.swiftpay.logic.API.APIService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NewTransactionActivity extends AppCompatActivity {
    private ProgressBar progressSpinnerStart;
    private TextInputEditText etAmount;
    private TextInputEditText etAccountNumber;
    private TextInputEditText etPassword;
    private TextInputEditText etDescription;
    private TextInputLayout tilAmount;
    private TextInputLayout tilAccountNumber;
    private TextInputLayout tilPassword;
    private TextInputLayout tilDescription;
    private Button btnSubmit;
    private TextView responseLbl;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        EdgeToEdge.enable(this);

        tilAmount = findViewById(R.id.tilAmount);
        tilAccountNumber = findViewById(R.id.tilAccountNumber);
        tilPassword = findViewById(R.id.tilPassword);
        tilDescription = findViewById(R.id.tilDescription);
        progressSpinnerStart = findViewById(R.id.progressSpinnerStart);
        etAmount = findViewById(R.id.edtAmount);
        etAccountNumber = findViewById(R.id.edtAccountNumber);
        etPassword = findViewById(R.id.edtPassword);
        etDescription = findViewById(R.id.edtDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        responseLbl = findViewById(R.id.tvPrompt);
        progressSpinnerStart.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(v -> {
            if (validateAllInputs())
                prepareTransaction();
        });
    }

    private boolean validateAllInputs() {
        boolean isValid = true;

        String amount = Objects.requireNonNull(etAmount.getText()).toString().trim();
        if (amount.isEmpty()) {
            tilAmount.setError("Amount cannot be empty");
            isValid = false;
        } else {
            tilAmount.setError(null);
        }

        String accountNumber = Objects.requireNonNull(etAccountNumber.getText()).toString().trim();
        if (accountNumber.isEmpty()) {
            tilAccountNumber.setError("Account number cannot be empty");
            isValid = false;
        } else {
            tilAccountNumber.setError(null);
        }

        String password = Objects.requireNonNull(etPassword.getText()).toString().trim();
        if (password.isEmpty()) {
            tilPassword.setError("Password cannot be empty");
            isValid = false;
        }
         else {
            tilPassword.setError(null);
        }
        return isValid;
    }


    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    private void prepareTransaction(){
        HashMap<String, String>data = new HashMap<>();
        data.put("amount", Objects.requireNonNull(etAmount.getText()).toString().trim());
        data.put("account_number", Objects.requireNonNull(etAccountNumber.getText()).toString().trim());
        data.put("password", Objects.requireNonNull(etPassword.getText()).toString().trim());
        data.put("description", Objects.requireNonNull(etDescription.getText()).toString().trim());

        var executor = Executors.newSingleThreadExecutor();
        var handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            handler.post(() -> {
                progressSpinnerStart.setVisibility(View.VISIBLE);
                responseLbl.setText("Loading...");
            });
            HashMap<String, Object>response = APIService.transaction(data);
            handler.post(() -> {
               String message = (String) response.getOrDefault("message", "Unknown Error");
               if (message != null) {
                   if (message.contains("successful")) {
                       responseLbl.setText(message);
                       responseLbl.setTextColor(R.color.transaction_amount_positive);
                       Toast.makeText(NewTransactionActivity.this, message, Toast.LENGTH_LONG).show();
                   }
                   else {
                       responseLbl.setTextColor(R.color.custom3);
                        if (message.contains("credentials"))
                           tilPassword.setError(message);
                       else if (message.contains("enough"))
                           tilAmount.setError(message);
                       else if (message.toLowerCase().contains("account"))
                           tilAccountNumber.setError(message);
                       else
                           responseLbl.setText(message);

                   }
               }
            });
            handler.post(() -> {
                progressSpinnerStart.setVisibility(View.GONE);
                responseLbl.setText("");
            });
        });
    }
}