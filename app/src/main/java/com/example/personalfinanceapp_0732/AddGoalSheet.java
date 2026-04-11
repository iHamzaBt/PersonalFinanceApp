package com.example.personalfinanceapp_0732;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.personalfinanceapp_0732.databinding.AddGoalBinding;

public class AddGoalSheet extends BottomSheetDialogFragment {

    AddGoalBinding binding;
    TransactionViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddGoalBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        String symbol = CurrencyHelper.getSymbol(requireContext());
        binding.GoalAmountPlaceHolder.setHint(symbol + "0.00");

        binding.SaveGoalBt.setOnClickListener(view -> saveGoal());

        return binding.getRoot();
    }

    private void saveGoal() {
        String name = binding.GoalNamePlaceHolder.getText().toString().trim();
        String amountStr = binding.GoalAmountPlaceHolder.getText().toString().trim();

        if (name.isEmpty()) {
            binding.GoalNamePlaceHolder.setError("Please enter a name");
            return;
        }

        if (amountStr.isEmpty()) {
            binding.GoalAmountPlaceHolder.setError("Please enter an amount");
            return;
        }

        try {
            double targetAmount = Double.parseDouble(amountStr);
            if (targetAmount <= 0) {
                binding.GoalAmountPlaceHolder.setError("Amount must be greater than 0");
                return;
            }

            Goal goal = new Goal(name, targetAmount, 0);
            viewModel.insertGoal(goal);

            Toast.makeText(getContext(), "Goal Saved!", Toast.LENGTH_SHORT).show();
            dismiss();

        } catch (NumberFormatException e) {
            binding.GoalAmountPlaceHolder.setError("Invalid number format");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}