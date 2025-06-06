package com.example.swiftpay.logic.API;

import android.os.StrictMode;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIService {

    private final static String BASE_URL = "BASE URL";

    private final static String usersEndpoint = "";

    private final static String pingEndpoint = "";

    public static JSONArray fetchUsersData() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(BASE_URL + usersEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder responseBuilder = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                responseBuilder.append(output);
            }

            conn.disconnect();
            return new JSONArray(responseBuilder.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean pingApi(int maxTries) {
        for (int i = 0; i < maxTries; i++) {
            try {
                URL url = new URL(BASE_URL + pingEndpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(2000);
                conn.connect();

                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) return true;

            } catch (Exception e) {
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }

        return false;
    }
}
