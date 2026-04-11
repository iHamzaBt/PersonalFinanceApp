package com.example.personalfinanceapp_0732;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetHolder> {

    private List<Budget> budgets = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(Budget budget);
        void onItemClick(Budget budget);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetHolder holder, int position) {
        Budget currentBudget = budgets.get(position);
        String symbol = CurrencyHelper.getSymbol(holder.itemView.getContext());
        holder.tvCategoryName.setText(currentBudget.getCategory());
        holder.tvBudgetAmount.setText(String.format(Locale.US, "Limit: %s%.2f", symbol, currentBudget.getAmount()));
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    class BudgetHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private TextView tvBudgetAmount;
        private ImageView btnDeleteBudget;

        public BudgetHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvBudgetAmount = itemView.findViewById(R.id.tvBudgetAmount);
            btnDeleteBudget = itemView.findViewById(R.id.btnDeleteBudget);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(budgets.get(position));
                }
            });

            btnDeleteBudget.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(budgets.get(position));
                }
            });
        }
    }
}