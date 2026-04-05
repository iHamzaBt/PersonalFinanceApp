package com.example.personalfinanceapp_0732;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.personalfinanceapp_0732.databinding.FundGoalBinding;

public class FundGoalSheet extends BottomSheetDialogFragment {

    FundGoalBinding binding;
    Goal currentGoal;
    TransactionViewModel viewModel;

    public void setGoal(Goal goal) { this.currentGoal = goal; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FundGoalBinding.inflate(inflater, container, false);

        if (currentGoal != null) binding.tvFundGoalTitle.setText("Funding Goal: " + currentGoal.getTitle());

        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        binding.SaveFundBt.setOnClickListener(view -> saveFund());

        return binding.getRoot();
    }

    private void saveFund() {
        if (currentGoal == null) return;
        String amountStr = binding.FundAmountPlaceHolder.getText().toString().trim();
        if (amountStr.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountStr);
            double newSavedAmount = currentGoal.getSavedAmount() + amount;
            if (newSavedAmount > currentGoal.getTargetAmount()) return;

            currentGoal.setSavedAmount(newSavedAmount);
            viewModel.updateGoal(currentGoal);
            viewModel.insert(new Transaction("Fund: " + currentGoal.getTitle(), "Goal Funding", amount, "Expense", "Goal"));

            if (newSavedAmount >= currentGoal.getTargetAmount()) {
                String motivation = "A person without goals is like a ship without a Captain. Set your next financial goal now and keep growing!";

                viewModel.insertNotification(new Notification("Goal Achieved!", "Congratulations! You fully funded: " + currentGoal.getTitle(), System.currentTimeMillis()));
                viewModel.insertNotification(new Notification("What's Next?", motivation, System.currentTimeMillis() + 1000));

                NotificationHelper.showGoalCompletedNotifications(requireContext(), currentGoal.getTitle());
            }
            dismiss();
        } catch (Exception e) {}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}