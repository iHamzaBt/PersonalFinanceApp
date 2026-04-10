package com.example.personalfinanceapp_0732;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.personalfinanceapp_0732.databinding.FragmentBudgetsBinding;
import java.util.ArrayList;
import java.util.List;

public class BudgetsFragment extends Fragment {

    private FragmentBudgetsBinding binding;
    private TransactionViewModel viewModel;
    private BudgetAdapter adapter;
    private Budget overallBudget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBudgetsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        binding.btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // تفعيل زر التصفير
        binding.btnResetBudgets.setOnClickListener(v -> showResetConfirmationDialog());

        setupRecyclerView();

        viewModel.getAllBudgets().observe(getViewLifecycleOwner(), budgets -> {
            List<Budget> categoryBudgets = new ArrayList<>();
            overallBudget = null;

            for (Budget b : budgets) {
                if ("Overall".equals(b.getCategory())) {
                    overallBudget = b;
                    binding.etOverallBudget.setText(String.valueOf(b.getAmount()));
                } else {
                    categoryBudgets.add(b);
                }
            }
            adapter.setBudgets(categoryBudgets);

            // في حال تم التصفير وقاعدة البيانات فارغة، نفرغ حقل الإدخال
            if (overallBudget == null) {
                binding.etOverallBudget.setText("");
            }
        });

        binding.btnSaveOverall.setOnClickListener(v -> saveOverallBudget());

        binding.fabAddBudget.setOnClickListener(v -> {
            AddEditBudget sheet = new AddEditBudget();
            sheet.show(getChildFragmentManager(), "AddEditBudget");
        });
    }

    private void setupRecyclerView() {
        binding.recyclerViewBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BudgetAdapter();
        binding.recyclerViewBudgets.setAdapter(adapter);

        adapter.setOnItemClickListener(new BudgetAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(Budget budget) {
                viewModel.deleteBudget(budget);
                Toast.makeText(getContext(), "Budget deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClick(Budget budget) {
                AddEditBudget sheet = new AddEditBudget();
                sheet.setBudgetToEdit(budget);
                sheet.show(getChildFragmentManager(), "AddEditBudget");
            }
        });
    }

    // دالة إظهار رسالة تأكيد التصفير
    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset All Budgets")
                .setMessage("Are you sure you want to delete all budget limits? This action cannot be undone.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    viewModel.deleteAllBudgets();
                    Toast.makeText(getContext(), "All budgets cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveOverallBudget() {
        String valueStr = binding.etOverallBudget.getText().toString().trim();
        if (valueStr.isEmpty()) {
            Toast.makeText(getContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(valueStr);
        if (overallBudget == null) {
            viewModel.insertBudget(new Budget("Overall", amount));
        } else {
            overallBudget.setAmount(amount);
            viewModel.updateBudget(overallBudget);
        }
        Toast.makeText(getContext(), "Overall budget updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}