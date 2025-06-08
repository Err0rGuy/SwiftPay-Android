package com.example.swiftpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.swiftpay.logic.API.Transaction;
import com.example.swiftpay.logic.API.TransactionAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private static final String TAG = "TransactionHistoryAct";

    private RecyclerView rvTransactionHistory;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList; // Initialize here
    private ProgressBar progressBarHistory;
    private TextView tvNoTransactions;
    private MaterialToolbar toolbar;

    public interface FetchTransactionsCallback {
        void onSuccess(JSONObject response);

        void onFailure(Exception error);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolbarTransactionHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rvTransactionHistory = findViewById(R.id.rvTransactionHistory);
        progressBarHistory = findViewById(R.id.progressBarHistory);
        tvNoTransactions = findViewById(R.id.tvNoTransactions);

        transactionList = new ArrayList<>(); // Initialize the list
        transactionAdapter = new TransactionAdapter(this, transactionList);

        rvTransactionHistory.setLayoutManager(new LinearLayoutManager(this));
        rvTransactionHistory.setAdapter(transactionAdapter);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(TransactionHistoryActivity.this, DashboardActivity.class);
            startActivity(intent);
        });
        loadTransactionData();
    }

    private void loadTransactionData() {
        showLoading(true);
        simulateFetchTransactions(new FetchTransactionsCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                showLoading(false);
                try {
                    // Assuming your JSON response has a JSONArray named "transactions"
                    // Adjust "transactions" if your JSON key is different
                    if (response.has("transactions")) {
                        JSONArray transactionsArray = response.getJSONArray("transactions");
                        if (transactionsArray.length() == 0) {
                            showNoTransactionsMessage(true);
                            transactionList.clear(); // Clear any old data
                            transactionAdapter.notifyDataSetChanged();
                            return;
                        }

                        showNoTransactionsMessage(false);
                        List<Transaction> newTransactions = new ArrayList<>();
                        for (int i = 0; i < transactionsArray.length(); i++) {
                            JSONObject transactionObject = transactionsArray.getJSONObject(i);
                            newTransactions.add(new Transaction(transactionObject));
                        }
                        transactionAdapter.updateTransactions(newTransactions);
                    } else {
                        Log.e(TAG, "JSON response does not contain 'transactions' array.");
                        showNoTransactionsMessage(true);
                        Toast.makeText(TransactionHistoryActivity.this, "Could not parse transaction data.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                    showLoading(false);
                    showNoTransactionsMessage(true);
                    Toast.makeText(TransactionHistoryActivity.this, "Error parsing transaction data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception error) {
                Log.e(TAG, "Failed to fetch transactions: " + error.getMessage(), error);
                showLoading(false);
                showNoTransactionsMessage(true);
                Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void simulateFetchTransactions(FetchTransactionsCallback callback) {
        new android.os.Handler().postDelayed(() -> {
            try {
                JSONObject mockResponse = new JSONObject();
                JSONArray transactionsArray = new JSONArray();

                // Example: Add a few mock transactions
                JSONObject tx1 = new JSONObject();
                tx1.put("description", "Grocery Shopping");
                tx1.put("amount", "-$55.20");
                tx1.put("destinationAccount", "**** 1234");
                tx1.put("status", "success");
                tx1.put("date", "Oct 27, 2023, 09:15 AM");
                transactionsArray.put(tx1);

                JSONObject tx2 = new JSONObject();
                tx2.put("description", "Salary Deposit");
                tx2.put("amount", "+$2500.00");
                tx2.put("destinationAccount", "Primary Account");
                tx2.put("status", "success");
                tx2.put("date", "Oct 26, 2023, 03:00 PM");
                transactionsArray.put(tx2);

                JSONObject tx3 = new JSONObject();
                tx3.put("description", "Online Subscription");
                tx3.put("amount", "-$9.99");
                tx3.put("destinationAccount", "PayPal");
                tx3.put("status", "fail");
                tx3.put("date", "Oct 26, 2023, 11:00 AM");
                transactionsArray.put(tx3);

                mockResponse.put("transactions", transactionsArray);
                if (callback != null) {
                    callback.onSuccess(mockResponse);
                }
            } catch (JSONException e) {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        }, 1500);
    }


    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBarHistory.setVisibility(View.VISIBLE);
            rvTransactionHistory.setVisibility(View.GONE);
            tvNoTransactions.setVisibility(View.GONE);
        } else {
            progressBarHistory.setVisibility(View.GONE);
        }
    }



    private void showNoTransactionsMessage(boolean show) {
        if (show) {
            tvNoTransactions.setVisibility(View.VISIBLE);
            rvTransactionHistory.setVisibility(View.GONE);
        } else {
            tvNoTransactions.setVisibility(View.GONE);
            rvTransactionHistory.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressedDispatcher.onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}