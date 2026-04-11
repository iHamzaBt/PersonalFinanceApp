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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalfinanceapp_0732.databinding.FragmentAddEditTransactionBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTransactionFragment extends Fragment {

    private FragmentAddEditTransactionBinding binding;
    private TransactionViewModel viewModel;

    private Transaction transactionToEdit = null;
    private Calendar selectedCalendar = Calendar.getInstance();

    public void setTransactionToEdit(Transaction transaction) {
        this.transactionToEdit = transaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditTransactionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        String symbol = CurrencyHelper.getSymbol(requireContext());
        binding.etAmount.setHint(symbol + "0.00");

        setupCategoryDropdown();
        setupDatePicker();
        setupClickListeners();

        if (transactionToEdit != null) {
            populateDataForEdit();
        } else {
            updateDateInView();
        }
    }

    private void setupCategoryDropdown() {
        String[] categories = {"Food", "Transport", "Rent", "Bills", "Salary", "Shopping", "Entertainment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        binding.etCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateInView();
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        binding.etDate.setText(sdf.format(selectedCalendar.getTime()));
    }

    private void populateDataForEdit() {
        binding.tvHeaderTitle.setText("Edit Transaction");
        binding.btnSave.setText("Update Transaction");

        binding.etTitle.setText(transactionToEdit.getTitle());

        String fullDesc = transactionToEdit.getDescription();
        if (fullDesc != null && fullDesc.contains(" | Note: ")) {
            String[] parts = fullDesc.split(" \\| Note: ");
            binding.etDesc.setText(parts[0]);
            if (parts.length > 1) binding.etNote.setText(parts[1]);
        } else {
            binding.etDesc.setText(fullDesc);
        }

        String amountStr = (transactionToEdit.getAmount() % 1 == 0)
                ? String.valueOf((int) transactionToEdit.getAmount())
                : String.valueOf(transactionToEdit.getAmount());

        binding.etAmount.setText(amountStr);
        binding.etCategory.setText(transactionToEdit.getCategory(), false);

        selectedCalendar.setTimeInMillis(transactionToEdit.getTimestamp());
        updateDateInView();

        if ("Income".equals(transactionToEdit.getType())) {
            binding.toggleGroupType.check(R.id.btnTypeIncome);
        } else {
            binding.toggleGroupType.check(R.id.btnTypeExpense);
        }
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        binding.btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String title = binding.etTitle.getText().toString().trim();
        String desc = binding.etDesc.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();
        String category = binding.etCategory.getText().toString().trim();
        String type = binding.toggleGroupType.getCheckedButtonId() == R.id.btnTypeIncome ? "Income" : "Expense";

        if (title.isEmpty()) {
            binding.layoutTitle.setError("Title is required");
            return;
        } else {
            binding.layoutTitle.setError(null);
        }

        if (desc.isEmpty()) {
            binding.layoutDesc.setError("Description is required");
            return;
        } else {
            binding.layoutDesc.setError(null);
        }

        if (amountStr.isEmpty() || Double.parseDouble(amountStr) <= 0) {
            binding.etAmount.setError("Amount must be greater than 0");
            return;
        }

        if (category.isEmpty()) {
            binding.layoutCategory.setError("Category is required");
            return;
        } else {
            binding.layoutCategory.setError(null);
        }

        double amount = Double.parseDouble(amountStr);
        long timestamp = selectedCalendar.getTimeInMillis();

        String finalDesc = note.isEmpty() ? desc : desc + " | Note: " + note;

        if (transactionToEdit == null) {
            Transaction newTransaction = new Transaction(title, finalDesc, amount, type, category);
            newTransaction.setTimestamp(timestamp);
            viewModel.insert(newTransaction);
            Toast.makeText(getContext(), "Transaction Added", Toast.LENGTH_SHORT).show();
        } else {
            transactionToEdit.setTitle(title);
            transactionToEdit.setDescription(finalDesc);
            transactionToEdit.setAmount(amount);
            transactionToEdit.setCategory(category);
            transactionToEdit.setTimestamp(timestamp);
            transactionToEdit.setType(type);
            viewModel.update(transactionToEdit);
            Toast.makeText(getContext(), "Transaction Updated", Toast.LENGTH_SHORT).show();
        }

        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}