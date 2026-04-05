package com.example.personalfinanceapp_0732;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinanceapp_0732.databinding.ItemGoalBinding;
import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalHolder> {

    private List<Goal> goals = new ArrayList<>();
    private OnItemClickListener listener;

    private String[] bgColors = {"#F1F8E9", "#FFF8E1", "#E0F2F1", "#FBE9E7", "#E8EAF6"};
    private String[] textColors = {"#388E3C", "#FBC02D", "#00796B", "#D84315", "#3F51B5"};

    public interface OnItemClickListener {
        void onItemClick(Goal goal);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGoalBinding binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GoalHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalHolder holder, int position) {
        Goal current = goals.get(position);

        holder.binding.tvGoalTitle.setText(current.getTitle());
        String amountText = "$" + current.getSavedAmount() + " / $" + current.getTargetAmount();
        holder.binding.tvGoalAmounts.setText(amountText);

        holder.binding.progressBarGoal.setMax((int) current.getTargetAmount());
        holder.binding.progressBarGoal.setProgress((int) current.getSavedAmount());

        if (current.getSavedAmount() >= current.getTargetAmount()) {
            holder.binding.tvGoalTitle.setPaintFlags(holder.binding.tvGoalTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.binding.cardGoal.setAlpha(0.6f);
        } else {
            holder.binding.tvGoalTitle.setPaintFlags(holder.binding.tvGoalTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.binding.cardGoal.setAlpha(1.0f);
        }

        int colorIndex = current.getId() % bgColors.length;

        holder.binding.cardGoal.setCardBackgroundColor(Color.parseColor(bgColors[colorIndex]));
        holder.binding.tvGoalAmounts.setTextColor(Color.parseColor(textColors[colorIndex]));
        holder.binding.progressBarGoal.setProgressTintList(ColorStateList.valueOf(Color.parseColor(textColors[colorIndex])));
    }

    @Override
    public int getItemCount() { return goals.size(); }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
        notifyDataSetChanged();
    }

    class GoalHolder extends RecyclerView.ViewHolder {
        ItemGoalBinding binding;

        public GoalHolder(ItemGoalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.cardGoal.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(goals.get(position));
                }
            });
        }
    }
}