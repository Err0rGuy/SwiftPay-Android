package com.example.swiftpay.logic.API;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftpay.R;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;
    private Context context;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.tvTransactionDescription.setText(transaction.getDescription());
        holder.tvDestinationAccount.setText("To: " + transaction.getDestinationAccount()); // Or however you want to format it
        holder.tvTransactionAmount.setText(transaction.getAmount()); // You might want to format this (e.g., add currency symbol)

        if (transaction.getDate() != null && !transaction.getDate().isEmpty()) {
            holder.tvTransactionDate.setText(transaction.getDate());
            holder.tvTransactionDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvTransactionDate.setVisibility(View.GONE);
        }

        // Set status icon and amount color
        if ("success".equals(transaction.getStatus().toLowerCase(Locale.ROOT))) {
            holder.ivTransactionStatus.setImageResource(R.drawable.ic_status_success);
            // Assuming positive amount is incoming, negative is outgoing.
            // You might need more sophisticated logic or a field from API to determine this.
            if (transaction.getAmount().startsWith("-")) {
                holder.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.transaction_amount_negative)); // Define this color
            } else {
                holder.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.transaction_amount_positive)); // Define this color
            }
        } else { // "fail" or any other status
            holder.ivTransactionStatus.setImageResource(R.drawable.ic_status_fail);
            holder.tvTransactionAmount.setTextColor(ContextCompat.getColor(context, R.color.transaction_amount_failed)); // Define this color
        }
    }

    @Override
    public int getItemCount() {
        return transactionList == null ? 0 : transactionList.size();
    }

    // Method to update the list of transactions
    public void updateTransactions(List<Transaction> newTransactions) {
        this.transactionList.clear();
        if (newTransactions != null) {
            this.transactionList.addAll(newTransactions);
        }
        notifyDataSetChanged(); // Consider using DiffUtil for better performance with large lists
    }


    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTransactionStatus;
        TextView tvTransactionDescription;
        TextView tvDestinationAccount;
        TextView tvTransactionAmount;
        TextView tvTransactionDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTransactionStatus = itemView.findViewById(R.id.ivTransactionStatus);
            tvDestinationAccount = itemView.findViewById(R.id.tvDestinationAccount);
            tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
            tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
        }
    }
}