package com.example.personalfinanceapp_0732;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinanceapp_0732.databinding.ItemGoalHomeBinding;
import java.util.ArrayList;
import java.util.List;

public class HomeGoalAdapter extends RecyclerView.Adapter<HomeGoalAdapter.HomeGoalHolder> {

    private List<Goal> goals = new ArrayList<>();
    private OnItemClickListener listener;

    private final String[] bgColors = {"#F1F8E9", "#FFF8E1", "#E0F2F1", "#FBE9E7", "#E8EAF6"};
    private final String[] textColors = {"#388E3C", "#FBC02D", "#00796B", "#D84315", "#3F51B5"};

    public interface OnItemClickListener {
        void onItemClick(Goal goal);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeGoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalHomeBinding binding = ItemGoalHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new HomeGoalHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeGoalHolder holder, int position) {
        Goal current = goals.get(position);
        String symbol = CurrencyHelper.getSymbol(holder.itemView.getContext());

        holder.binding.tvHomeGoalTitle.setText(current.getTitle());
        String amountText = symbol + current.getSavedAmount() + " / " + symbol + current.getTargetAmount();
        holder.binding.tvHomeGoalAmounts.setText(amountText);

        int colorIndex = current.getId() % bgColors.length;
        holder.binding.cardHomeGoal.setCardBackgroundColor(Color.parseColor(bgColors[colorIndex]));
        holder.binding.tvHomeGoalAmounts.setTextColor(Color.parseColor(textColors[colorIndex]));
    }

    @Override
    public int getItemCount() { return goals.size(); }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    class HomeGoalHolder extends RecyclerView.ViewHolder {
        ItemGoalHomeBinding binding;

        public HomeGoalHolder(ItemGoalHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.cardHomeGoal.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(goals.get(position));
                }
            });
        }
    }
}