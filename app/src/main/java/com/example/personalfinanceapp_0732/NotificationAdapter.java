package com.example.personalfinanceapp_0732;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinanceapp_0732.databinding.ItemNotificationBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifHolder> {

    private List<Notification> notifications = new ArrayList<>();

    @NonNull
    @Override
    public NotifHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotifHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifHolder holder, int position) {
        Notification current = notifications.get(position);
        holder.binding.tvNotifTitle.setText(current.getTitle());
        holder.binding.tvNotifMessage.setText(current.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
        holder.binding.tvNotifTime.setText(sdf.format(new Date(current.getTimestamp())));
    }

    @Override
    public int getItemCount() { return notifications.size(); }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

   public static class NotifHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;
        public NotifHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}