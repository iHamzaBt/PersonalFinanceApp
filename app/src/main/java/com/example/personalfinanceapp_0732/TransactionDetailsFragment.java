package com.example.personalfinanceapp_0732;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.personalfinanceapp_0732.databinding.FragmentTransactionDetailsBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransactionDetailsFragment extends Fragment {

    private FragmentTransactionDetailsBinding binding;
    private Transaction transaction;
    private TransactionViewModel viewModel;

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        if (transaction == null) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        populateData();
        setupClickListeners();
    }

    private void populateData() {
        String symbol = CurrencyHelper.getSymbol(requireContext());
        String amountStr = String.format(Locale.US, "%s%.2f", symbol, transaction.getAmount());

        binding.tvTitle.setText(transaction.getTitle());
        binding.tvCategory.setText(transaction.getCategory());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        binding.tvDate.setText(sdf.format(new Date(transaction.getTimestamp())));

        String fullDesc = transaction.getDescription();
        if (fullDesc != null && fullDesc.contains(" | Note: ")) {
            String[] parts = fullDesc.split(" \\| Note: ");
            binding.tvDescription.setText(parts[0].trim());
            binding.tvNote.setText(parts.length > 1 ? parts[1].trim() : "-");
        } else {
            binding.tvDescription.setText(fullDesc != null && !fullDesc.trim().isEmpty() ? fullDesc.trim() : "-");
            binding.tvNote.setText("-");
        }

        binding.tvTypeBadge.setText(transaction.getType());

        if ("Investment".equals(transaction.getCategory())) {
            binding.tvAmount.setTextColor(Color.parseColor("#F57F17"));
            binding.tvAmount.setText("-" + amountStr);
            binding.tvTypeBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F57F17")));
            binding.tvTypeBadge.setText("Investment");
        } else if ("Income".equals(transaction.getType())) {
            binding.tvAmount.setTextColor(Color.parseColor("#2E7D32"));
            binding.tvAmount.setText("+" + amountStr);
            binding.tvTypeBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2E7D32")));
        } else {
            binding.tvAmount.setTextColor(Color.parseColor("#E53935"));
            binding.tvAmount.setText("-" + amountStr);
            binding.tvTypeBadge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
        }
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        binding.btnDelete.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.delete(transaction);
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Cancel", null)
                .show());

        binding.btnEdit.setOnClickListener(v -> {
            if ("Investment".equals(transaction.getCategory())) {
                AddInvestmentSheet editSheet = new AddInvestmentSheet();
                editSheet.setTransactionToEdit(transaction);
                editSheet.show(getChildFragmentManager(), "EditInvestmentSheet");
            } else {
                AddEditTransactionFragment editFragment = new AddEditTransactionFragment();
                editFragment.setTransactionToEdit(transaction);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, editFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}