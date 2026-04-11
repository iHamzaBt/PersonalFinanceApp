package com.example.personalfinanceapp_0732;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinanceapp_0732.databinding.ItemCurrencyBinding;
import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.VH> {

    private List<CurrencyItem> list;
    private String selectedCode;
    private final OnCurrencySelected listener;

    interface OnCurrencySelected {
        void onSelected(CurrencyItem item);
    }

    public CurrencyAdapter(List<CurrencyItem> list, String selectedCode, OnCurrencySelected listener) {
        this.list = list;
        this.selectedCode = selectedCode;
        this.listener = listener;
    }

    public void updateList(List<CurrencyItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemCurrencyBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CurrencyItem item = list.get(position);
        boolean isSelected = item.code.equals(selectedCode);

        holder.binding.tvFlag.setText(item.flag);
        holder.binding.tvCurrencyCode.setText(item.code);
        holder.binding.tvCurrencyName.setText(item.name);
        holder.binding.tvCurrencySymbol.setText(item.symbol);

        holder.binding.ivSelected.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
        holder.binding.getRoot().setAlpha(isSelected ? 1f : 0.85f);

        if (isSelected) {
            holder.binding.tvCurrencyCode.setTextColor(
                    holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
        } else {
            holder.binding.tvCurrencyCode.setTextColor(0xFF212121);
        }

        holder.itemView.setAlpha(0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
        anim.setDuration(200);
        anim.setStartDelay(position * 20L);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

        holder.itemView.setOnClickListener(v -> {
            String prev = selectedCode;
            selectedCode = item.code;
            int prevPos = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).code.equals(prev)) {
                    prevPos = i;
                    break;
                }
            }
            if (prevPos != -1) notifyItemChanged(prevPos);
            notifyItemChanged(holder.getBindingAdapterPosition());
            listener.onSelected(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ItemCurrencyBinding binding;
        VH(ItemCurrencyBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }
}