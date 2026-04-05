package com.example.personalfinanceapp_0732;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.personalfinanceapp_0732.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {

    ActivityNotificationBinding binding;
    private TransactionViewModel viewModel;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackNotification.setOnClickListener(view -> finish());

        binding.recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter();
        binding.recyclerViewNotifications.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        viewModel.getAllNotifications().observe(this, notifications -> adapter.setNotifications(notifications));
    }
}