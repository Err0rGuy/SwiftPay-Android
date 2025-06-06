package com.example.swiftpay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftpay.logic.API.APIService;

import org.json.JSONArray;

public class StartActivity extends AppCompatActivity {

    private ProgressBar progressSpinnerStart;
    private TextView tvLoadingMessage;
    public static JSONArray userData;
    private final Handler handler = new Handler();
    private final int CHECK_INTERVAL = 3000; // milliseconds
    private final int MAX_RETRIES = 3;
    private int retryCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        progressSpinnerStart = findViewById(R.id.progressSpinnerStart);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);

        checkInternetAndProceed();
    }

    @SuppressLint("SetTextI18n")
    private void checkInternetAndProceed() {
        tvLoadingMessage.setText("Checking internet connection...");

        if (isNetworkAvailable(this)) {
            noErrorAndRetry("Internet available. Checking server...");
            pingApiAndProceed();
        } else {
            showErrorAndRetry("No internet connection. Waiting...");
            handler.postDelayed(this::checkInternetAndProceed, CHECK_INTERVAL);
        }
    }

    private void pingApiAndProceed() {
        new Thread(() -> {
            boolean isAlive = APIService.pingApi(4);

            runOnUiThread(() -> {
                if (isAlive) {
                    noErrorAndRetry("Server is online. Redirecting...");
                    fetchingData();
                } else {
                    retryCount++;
                    if (retryCount < MAX_RETRIES) {
                        showErrorAndRetry("Server is down. Retrying (" + retryCount + "/" + MAX_RETRIES + ")...");
                        handler.postDelayed(this::checkInternetAndProceed, CHECK_INTERVAL);
                    } else {
                        showErrorAndRetry("Server is unreachable!\nThis is not your problem, Try again later");
                        progressSpinnerStart.setVisibility(View.GONE);
                    }
                }
            });
        }).start();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        Network network = connectivityManager.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    private void fetchingData(){
        userData = APIService.fetchUsersData();
        if (userData == null) navigateToLogin();
        else navigateToDashboard();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(StartActivity.this, DashboardActivity.class);
        intent.putExtra("userData", (CharSequence) userData);
        startActivity(intent);
        finish();
    }

    private void showErrorAndRetry(String message) {
        tvLoadingMessage.setText(message);
        tvLoadingMessage.setTextColor(getResources().getColor(R.color.red, null));
    }

    private void noErrorAndRetry(String message) {
        tvLoadingMessage.setText(message);
        tvLoadingMessage.setTextColor(getResources().getColor(R.color.black, null));
    }
}
