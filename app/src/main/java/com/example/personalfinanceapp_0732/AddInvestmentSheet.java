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
import com.example.personalfinanceapp_0732.databinding.AddInvestmentBinding;

public class AddInvestmentSheet extends BottomSheetDialogFragment {

    AddInvestmentBinding binding;
    TransactionViewModel viewModel;

    private Transaction transactionToEdit = null;

    public void setTransactionToEdit(Transaction transaction) {
        this.transactionToEdit = transaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddInvestmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        if (transactionToEdit != null) {
            binding.NewInvestmentTV.setText("Edit Investment");
            binding.TitlePlaceHolder.setText(transactionToEdit.getTitle());
            binding.DescPlaceHolder.setText(transactionToEdit.getDescription());
            binding.AmountPlaceHolder.setText(String.valueOf(transactionToEdit.getAmount()));
            binding.SaveBt.setText("Update");
        }

        binding.SaveBt.setOnClickListener(view -> saveData());

        return binding.getRoot();
    }

    private void saveData() {
        String title = binding.TitlePlaceHolder.getText().toString().trim();
        String description = binding.DescPlaceHolder.getText().toString().trim();
        String amountStr = binding.AmountPlaceHolder.getText().toString().trim();

        if (title.isEmpty()) {
            binding.TitlePlaceHolder.setError("Please enter a title");
            return;
        }
        if (amountStr.isEmpty()) {
            binding.AmountPlaceHolder.setError("Please enter an amount");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                binding.AmountPlaceHolder.setError("Amount must be greater than 0");
                return;
            }

            if (transactionToEdit == null) {
                Transaction transaction = new Transaction(title, description, amount, "Expense", "Investment");
                viewModel.insert(transaction);
                Toast.makeText(getContext(), "Investment Saved!", Toast.LENGTH_SHORT).show();
            } else {
                transactionToEdit.setTitle(title);
                transactionToEdit.setDescription(description);
                transactionToEdit.setAmount(amount);
                viewModel.update(transactionToEdit);
                Toast.makeText(getContext(), "Investment Updated!", Toast.LENGTH_SHORT).show();
            }

            dismiss();

        } catch (NumberFormatException e) {
            binding.AmountPlaceHolder.setError("Invalid number format");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}