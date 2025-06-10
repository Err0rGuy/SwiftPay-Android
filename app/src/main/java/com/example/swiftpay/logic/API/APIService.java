package com.example.swiftpay.logic.API;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class APIService {
    private static final String BASE_URL = "https://proud-existence-pressure-devoted.trycloudflare.com";
    private static final CookieManager cookieManager = new CookieManager();

    static {
        java.net.CookieHandler.setDefault(cookieManager);
    }

    public static boolean pingServer() {
        try {
            URL url = new URL(BASE_URL + "/ping");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            int responseCode = conn.getResponseCode();
            conn.disconnect();
            return responseCode == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean pingServerMultipleTimes(int tries) {
        for (int i = 0; i < tries; i++) {
            if (pingServer()) return true;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    public static boolean checkLogin() {
        try {
            URL url = new URL(BASE_URL + "/check");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            setCookies(conn);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();

            JSONObject json = new JSONObject(response.toString());
            return json.optBoolean("status", false);
        } catch (Exception e) {
            return false;
        }
    }

    public static HashMap<String, Object> register(HashMap<String, String> data) {
        return postRequest("/register", data);
    }

    public static HashMap<String, Object> login(HashMap<String, String> data) {
        return postRequest("/login", data);
    }

    public static HashMap<String,Object>transaction(HashMap<String, String>data){
        return postRequest("/transaction/transfer", data);
    }

    public static boolean logout(){
        try {
            URL url = new URL(BASE_URL + "/logout");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(2000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            setCookies(conn);
            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            Log.d("Exception happened -->", String.valueOf(e.getStackTrace()));
            throw new RuntimeException(e);
        }
    }



    public static JSONObject fetchData() {
        return getRequest("/detail");
    }

    public static JSONObject fetchTransaction(){
        return getRequest("/transaction");
    }

    private static JSONObject getRequest(String endpoint){
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            setCookies(conn);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            conn.disconnect();

            return new JSONObject(response.toString());
        } catch (Exception e) {
            Log.d("Exception happened ---> ", Arrays.toString(e.getStackTrace()));
            return null;
        }

    }
    private static HashMap<String, Object> postRequest(String endpoint, HashMap<String, String> data) {
        HashMap<String, Object> result = new HashMap<>();
        HttpURLConnection conn = null;

        try {
            URL url = new URL(BASE_URL + endpoint);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            setCookies(conn);

            String jsonInputString = toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream;

            if (responseCode >= 200 && responseCode < 300) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line.trim());
            }
            reader.close();

            saveCookies(conn);

            JSONObject jsonResponse = new JSONObject(responseBody.toString());
            String message = jsonResponse.optString("message", "No message");

            result.put("code", responseCode);
            result.put("message", message);
            return result;

        } catch (Exception e) {
            result.put("code", 0);
            result.put("message", "No response from server!");
            Log.d("API_ERROR", Log.getStackTraceString(e));
            return result;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private static void setCookies(HttpURLConnection conn) {
        List<HttpCookie> cookies = cookieManager.getCookieStore().getCookies();
        if (!cookies.isEmpty()) {
            StringBuilder cookieHeader = new StringBuilder();
            for (HttpCookie cookie : cookies) {
                cookieHeader.append(cookie.toString()).append("; ");
            }
            conn.setRequestProperty("Cookie", cookieHeader.toString());
        }
    }

    private static void saveCookies(HttpURLConnection conn) {
        for (int i = 1;; i++) {
            String headerKey = conn.getHeaderFieldKey(i);
            if (headerKey == null) break;
            if (headerKey.equalsIgnoreCase("Set-Cookie")) {
                List<HttpCookie> cookies = HttpCookie.parse(conn.getHeaderField(i));
                for (HttpCookie cookie : cookies) {
                    cookieManager.getCookieStore().add(null, cookie);
                }
            }
        }
    }

    private static String toJson(HashMap<String, String> map) {
        StringBuilder json = new StringBuilder("{");
        int count = 0;
        for (String key : map.keySet()) {
            json.append("\"").append(key).append("\":\"").append(map.get(key)).append("\"");
            if (++count < map.size()) json.append(",");
        }
        json.append("}");
        return json.toString();
    }
}
