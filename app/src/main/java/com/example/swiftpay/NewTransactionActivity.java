package com.example.swiftpay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NewTransactionActivity extends AppCompatActivity {
    private ProgressBar progressSpinnerStart;
    private EditText etAmount;
    private EditText etAccountNumber;
    private EditText etPassword;
    private EditText etDescription;
    private Button btnSubmitTransaction;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressSpinnerStart = findViewById(R.id.progressSpinnerStart);
        etAmount = findViewById(R.id.etAmount);
        etAccountNumber = findViewById(R.id.etDestinationAccount);
        etPassword = findViewById(R.id.etPassword);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitTransaction = findViewById(R.id.btnSubmitTransaction);
        progressSpinnerStart.setVisibility(View.GONE);


        btnSubmitTransaction.setOnClickListener(v -> {
            var response = prepareTransaction();
        });
    }

    private boolean prepareTransaction(){
        var executor = Executors.newSingleThreadExecutor();
        var handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            handler.post(() -> progressSpinnerStart.setVisibility(View.VISIBLE));




            handler.post(() -> progressSpinnerStart.setVisibility(View.GONE));
        });

        return true;
    }
}