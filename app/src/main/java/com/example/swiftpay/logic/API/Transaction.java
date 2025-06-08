package com.example.swiftpay.logic.API;

import org.json.JSONException;
import org.json.JSONObject;

public class Transaction {
    private String amount;
    private String destinationAccount;
    private String description;
    private String referenceNumber;
    private String status;
    private String date;
    public Transaction(JSONObject jsonObject) throws JSONException {
        this.description = jsonObject.optString("description", "N/A");
        this.amount = jsonObject.optString("amount", "0.00");
        this.destinationAccount = jsonObject.optString("destinationAccount", "Unknown Account");
        this.status = jsonObject.optString("status", "fail").toLowerCase();
        this.date = jsonObject.optString("date", "");
    }

    public String getAmount() {
        return amount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getReferenceNumber(){
        return referenceNumber;
    }

}