package com.example.personalfinanceapp_0732;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class AddEditBudget extends BottomSheetDialogFragment {

    private Budget budgetToEdit;
    private TransactionViewModel viewModel;

    public void setBudgetToEdit(Budget budget) {
        this.budgetToEdit = budget;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_edit_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        TextView tvSheetTitle = view.findViewById(R.id.tvSheetTitle);
        AutoCompleteTextView etCategoryName = view.findViewById(R.id.etCategoryName);
        EditText etLimitAmount = view.findViewById(R.id.etLimitAmount);
        MaterialButton btnSaveBudgetSheet = view.findViewById(R.id.btnSaveBudgetSheet);

        String symbol = CurrencyHelper.getSymbol(requireContext());
        etLimitAmount.setHint(symbol + "0.00");

        String[] categories = {"Food", "Transport", "Rent", "Bills", "Shopping", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        etCategoryName.setAdapter(adapter);

        if (budgetToEdit != null) {
            tvSheetTitle.setText("Edit Budget");
            etCategoryName.setText(budgetToEdit.getCategory(), false);
            etCategoryName.setEnabled(false);
            etLimitAmount.setText(String.valueOf(budgetToEdit.getAmount()));
        }

        btnSaveBudgetSheet.setOnClickListener(v -> {
            String category = etCategoryName.getText().toString().trim();
            String amountStr = etLimitAmount.getText().toString().trim();

            if (category.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            if (budgetToEdit == null) {
                viewModel.insertBudget(new Budget(category, amount));
            } else {
                budgetToEdit.setAmount(amount);
                viewModel.updateBudget(budgetToEdit);
            }

            dismiss();
        });
    }
}