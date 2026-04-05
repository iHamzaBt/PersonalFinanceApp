package com.example.personalfinanceapp_0732;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.personalfinanceapp_0732.databinding.AddTransactionBinding;
import com.google.android.material.button.MaterialButton;

public class AddTransactionSheet extends BottomSheetDialogFragment {

    AddTransactionBinding binding;
    TransactionViewModel viewModel;

    private Transaction transactionToEdit = null;

    public void setTransactionToEdit(Transaction transaction) {
        this.transactionToEdit = transaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AddTransactionBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        String[] categories = {"Food", "Transport", "Rent", "Bills", "Salary", "Shopping", "Investment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        binding.autoCompleteCategory.setAdapter(adapter);

        if (transactionToEdit != null) {
            binding.NewTransactionTV.setText("Edit " + transactionToEdit.getCategory());
            binding.TitlePlaceHolder.setText(transactionToEdit.getTitle());
            binding.DescPlaceHolder.setText(transactionToEdit.getDescription());
            binding.AmountPlaceHolder.setText(String.valueOf(transactionToEdit.getAmount()));
            binding.autoCompleteCategory.setText(transactionToEdit.getCategory(), false);

            if ("Income".equals(transactionToEdit.getType())) {
                binding.toggleGroupType.check(R.id.btnTypeIncome);
            } else {
                binding.toggleGroupType.check(R.id.btnTypeExpense);
            }
            binding.SaveBt.setText("Update");
        } else {
            binding.NewTransactionTV.setText(R.string.new_transactions);
        }

        binding.SaveBt.setOnClickListener(view -> saveData());

        return binding.getRoot();


    }

    private void saveData() {
        String title = binding.TitlePlaceHolder.getText().toString().trim();
        String description = binding.DescPlaceHolder.getText().toString().trim();
        String amountStr = binding.AmountPlaceHolder.getText().toString().trim();
        String category = binding.autoCompleteCategory.getText().toString().trim();

        if (title.isEmpty()) {
            binding.TitlePlaceHolder.setError("Please enter a title");
            return;
        }

        if (amountStr.isEmpty()) {
            binding.AmountPlaceHolder.setError("Please enter an amount");
            return;
        }

        if (category.isEmpty()) {
            binding.autoCompleteCategory.setError("Please select a category");
            return;
        }

        String type = "Expense";
        if (binding.toggleGroupType.getCheckedButtonId() == R.id.btnTypeIncome) {
            type = "Income";
        }

        try {
            double amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                binding.AmountPlaceHolder.setError("Amount must be greater than 0");
                return;
            }

            if (transactionToEdit == null) {
                Transaction transaction = new Transaction(title, description, amount, type, category);
                viewModel.insert(transaction);
                Toast.makeText(getContext(), "Transaction Saved!", Toast.LENGTH_SHORT).show();
            }
            else {
                transactionToEdit.setTitle(title);
                transactionToEdit.setDescription(description);
                transactionToEdit.setAmount(amount);
                transactionToEdit.setType(type);
                transactionToEdit.setCategory(category);
                viewModel.update(transactionToEdit);
                Toast.makeText(getContext(), "Transaction Updated!", Toast.LENGTH_SHORT).show();
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