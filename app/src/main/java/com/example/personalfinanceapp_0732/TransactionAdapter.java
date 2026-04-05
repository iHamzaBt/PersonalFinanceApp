package com.example.personalfinanceapp_0732;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinanceapp_0732.databinding.ItemTransactionBinding;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {

    private List<Transaction> transactions = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransactionBinding binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TransactionHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        Transaction current = transactions.get(position);

        holder.binding.tvTransactionTitle.setText(current.getTitle());
        holder.binding.tvTransactionCategory.setText(current.getCategory());

        if ("Investment".equals(current.getCategory())) {
            holder.binding.tvTransactionAmount.setText("-$" + current.getAmount());
            holder.binding.tvTransactionAmount.setTextColor(Color.parseColor("#F57F17"));

            holder.binding.cardItem.setCardBackgroundColor(Color.parseColor("#FFF9C4"));
            holder.binding.cardItem.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FBC02D")));
            holder.binding.viewCategoryColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF59D")));
            holder.binding.ivCategoryIcon.setImageResource(R.drawable.outline_compare_arrows_24);
        } else if ("Income".equals(current.getType())) {
            holder.binding.tvTransactionAmount.setText("+$" + current.getAmount());
            holder.binding.tvTransactionAmount.setTextColor(Color.parseColor("#2E7D32"));

            holder.binding.cardItem.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
            holder.binding.cardItem.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            holder.binding.viewCategoryColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#C8E6C9")));
            holder.binding.ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_info_details);
        } else {
            holder.binding.tvTransactionAmount.setText("-$" + current.getAmount());
            holder.binding.tvTransactionAmount.setTextColor(Color.parseColor("#C62828"));

            holder.binding.cardItem.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
            holder.binding.cardItem.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#F44336")));
            holder.binding.viewCategoryColor.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCDD2")));
            holder.binding.ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_info_details);
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    public Transaction getTransactionAt(int position) {
        return transactions.get(position);
    }

    class TransactionHolder extends RecyclerView.ViewHolder {
        ItemTransactionBinding binding;

        public TransactionHolder(ItemTransactionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(transactions.get(position));
                }
            });
        }
    }
}