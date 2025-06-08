package com.example.swiftpay.logic.API;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftpay.R;


import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactions;
    private final Context context;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        this.context = context;
        this.transactions = transactions;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus;
        TextView tvDescription, tvDestination, tvAmount, tvDate;
        ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.ivTransactionStatus);
            tvDescription = itemView.findViewById(R.id.tvTransactionDescription);
            tvDestination = itemView.findViewById(R.id.tvDestinationAccount);
            tvAmount = itemView.findViewById(R.id.tvTransactionAmount);
            tvDate = itemView.findViewById(R.id.tvTransactionDate);
            container = itemView.findViewById(R.id.container);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction tx = transactions.get(position);
        holder.tvDescription.setText(tx.getDescription());
        holder.tvDestination.setText("To: " + tx.getDestinationAccount());
        holder.tvAmount.setText("Amount: $" + tx.getAmount());
        holder.tvDate.setText(tx.getDate());
        holder.ivStatus.setImageResource(tx.getStatus() ? R.drawable.ic_status_success : R.drawable.ic_status_fail);
        holder.container.setBackgroundResource(tx.getStatus() ? R.drawable.gradient_background6 : R.drawable.gradient_background7);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
}
