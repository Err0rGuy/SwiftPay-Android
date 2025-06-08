package com.example.swiftpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftpay.logic.API.APIService;
import com.example.swiftpay.logic.API.Transaction;
import com.example.swiftpay.logic.API.TransactionAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView rvTransactions;
    private ProgressBar progressBar;
    private TextView tvNoTransactions;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        rvTransactions = findViewById(R.id.rvTransactionHistory);
        progressBar = findViewById(R.id.progressBarHistory);
        tvNoTransactions = findViewById(R.id.tvNoTransactions);
        toolbar = findViewById(R.id.toolbarTransactionHistory);
        toolbar.setNavigationOnClickListener(v -> {
            var intent = new Intent(TransactionHistoryActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
        loadTransactions();
    }

    @SuppressLint("SetTextI18n")
    private void loadTransactions() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoTransactions.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                JSONObject response = APIService.fetchTransaction();
                if (response == null || !response.has("transactions")) {
                    throw new Exception("Invalid response");
                }

                JSONArray jsonArray = response.getJSONArray("transactions");
                List<Transaction> transactionList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);

                    String description = obj.optString("description", "No Description");
                    String destination = obj.optString("destination_account", "Unknown");
                    String amount = obj.optString("amount", "$0.00");
                    String date = obj.optString("created_at", "");
                    boolean success = (obj.optString("status", "success").equals("success"));

                    transactionList.add(new Transaction(description, destination, amount, date, success));
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (transactionList.isEmpty()) {
                        tvNoTransactions.setVisibility(View.VISIBLE);
                    } else {
                        rvTransactions.setAdapter(new TransactionAdapter(this, transactionList));
                        rvTransactions.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvNoTransactions.setVisibility(View.VISIBLE);
                    tvNoTransactions.setText("Failed to load transactions.");
                });
            }
        }).start();
    }
}
