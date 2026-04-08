package com.example.personalfinanceapp_0732;

import android.app.DatePickerDialog;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionSheet extends BottomSheetDialogFragment {

    AddTransactionBinding binding;
    TransactionViewModel viewModel;

    private Transaction transactionToEdit = null;
    private Calendar selectedCalendar = Calendar.getInstance();

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

        updateDateInView();

        binding.DatePlaceHolder.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                selectedCalendar.set(Calendar.YEAR, year);
                selectedCalendar.set(Calendar.MONTH, month);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateInView();
            }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH), selectedCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.CancelBt.setOnClickListener(view -> dismiss());

        if (transactionToEdit != null) {
            binding.NewTransactionTV.setText("Edit " + transactionToEdit.getCategory());
            binding.TitlePlaceHolder.setText(transactionToEdit.getTitle());

            String fullDesc = transactionToEdit.getDescription();
            if (fullDesc != null && fullDesc.contains(" | Note: ")) {
                String[] parts = fullDesc.split(" \\| Note: ");
                binding.DescPlaceHolder.setText(parts[0]);
                if (parts.length > 1) binding.NotePlaceHolder.setText(parts[1]);
            } else {
                binding.DescPlaceHolder.setText(fullDesc);
            }

            String amountStr = (transactionToEdit.getAmount() % 1 == 0)
                    ? String.valueOf((int) transactionToEdit.getAmount())
                    : String.valueOf(transactionToEdit.getAmount());
            binding.AmountPlaceHolder.setText(amountStr);

            binding.autoCompleteCategory.setText(transactionToEdit.getCategory(), false);

            selectedCalendar.setTimeInMillis(transactionToEdit.getTimestamp());
            updateDateInView();

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

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        binding.DatePlaceHolder.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void saveData() {
        String title = binding.TitlePlaceHolder.getText().toString().trim();
        String description = binding.DescPlaceHolder.getText().toString().trim();
        String note = binding.NotePlaceHolder.getText().toString().trim();
        String amountStr = binding.AmountPlaceHolder.getText().toString().trim();
        String category = binding.autoCompleteCategory.getText().toString().trim();

        if (title.isEmpty()) {
            binding.TitlePlaceHolder.setError("Please enter a title");
            return;
        }

        if (description.isEmpty()) {
            binding.DescPlaceHolder.setError("Please enter a description");
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

            String finalDesc = note.isEmpty() ? description : description + " | Note: " + note;
            long timestamp = selectedCalendar.getTimeInMillis();

            if (transactionToEdit == null) {
                Transaction transaction = new Transaction(title, finalDesc, amount, type, category);
                transaction.setTimestamp(timestamp);
                viewModel.insert(transaction);
                Toast.makeText(getContext(), "Transaction Saved!", Toast.LENGTH_SHORT).show();
            }
            else {
                transactionToEdit.setTitle(title);
                transactionToEdit.setDescription(finalDesc);
                transactionToEdit.setAmount(amount);
                transactionToEdit.setType(type);
                transactionToEdit.setCategory(category);
                transactionToEdit.setTimestamp(timestamp);
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