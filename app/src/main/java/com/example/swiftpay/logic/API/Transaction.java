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
    private boolean success;
    public Transaction(String description, String destination, String amount, String date, boolean success) {
        this.description = description;
        this.destinationAccount = destination;
        this.amount = amount;
        this.date = date;
        this.success = success;
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

    public boolean getStatus() {
        return success;
    }

    public String getDate() {
        return date;
    }

    public String getReferenceNumber(){
        return referenceNumber;
    }

}