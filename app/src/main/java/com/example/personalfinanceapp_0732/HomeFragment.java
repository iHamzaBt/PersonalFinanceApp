package com.example.personalfinanceapp_0732;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinanceapp_0732.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private HomeGoalAdapter homeGoalAdapter;
    private List<Goal> allCurrentGoals = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerViews();

        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
            calculateFinances(transactions);
        });

        viewModel.getAllGoals().observe(getViewLifecycleOwner(), goals -> {
            allCurrentGoals = goals;
            List<Goal> activeGoals = new ArrayList<>();
            for (Goal g : goals) {
                if (g.getSavedAmount() < g.getTargetAmount()) {
                    activeGoals.add(g);
                }
            }
            homeGoalAdapter.setGoals(activeGoals);
        });

        viewModel.getAllNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications != null && !notifications.isEmpty()) {
                binding.notificationBadge.setVisibility(View.VISIBLE);
            } else {
                binding.notificationBadge.setVisibility(View.GONE);
            }
        });

        setupClickListeners();

        return binding.getRoot();
    }

    private void setupRecyclerViews() {
        binding.recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewHome.setHasFixedSize(true);
        binding.recyclerViewHome.setNestedScrollingEnabled(false);
        adapter = new TransactionAdapter();
        binding.recyclerViewHome.setAdapter(adapter);

        adapter.setOnItemClickListener(transaction -> {
            if ("Goal".equals(transaction.getCategory())) {
                Toast.makeText(getContext(), "Manage goals from the Goals section", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("Investment".equals(transaction.getCategory())) {
                AddInvestmentSheet editSheet = new AddInvestmentSheet();
                editSheet.setTransactionToEdit(transaction);
                editSheet.show(getChildFragmentManager(), "EditInvestmentSheet");
            } else {
                AddTransactionSheet editSheet = new AddTransactionSheet();
                editSheet.setTransactionToEdit(transaction);
                editSheet.show(getChildFragmentManager(), "EditTransactionSheet");
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Transaction transactionToDelete = adapter.getTransactionAt(position);

                if ("Goal".equals(transactionToDelete.getCategory())) {
                    String goalTitle = transactionToDelete.getTitle().replace("Fund: ", "");
                    for (Goal g : allCurrentGoals) {
                        if (g.getTitle().equals(goalTitle)) {
                            double newSaved = g.getSavedAmount() - transactionToDelete.getAmount();
                            if (newSaved < 0) newSaved = 0;
                            g.setSavedAmount(newSaved);
                            viewModel.updateGoal(g);
                            break;
                        }
                    }
                }

                viewModel.delete(transactionToDelete);
                Toast.makeText(getContext(), "Transaction Deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(binding.recyclerViewHome);

        binding.recyclerViewHomeGoals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewHomeGoals.setHasFixedSize(true);
        homeGoalAdapter = new HomeGoalAdapter();
        binding.recyclerViewHomeGoals.setAdapter(homeGoalAdapter);

        homeGoalAdapter.setOnItemClickListener(goal -> {
            startActivity(new Intent(getActivity(), GoalsActivity.class));
        });
    }

    private void setupClickListeners() {
        binding.btnQuickExpense.setOnClickListener(view -> openAddTransactionSheet());
        binding.btnQuickIncome.setOnClickListener(view -> openAddTransactionSheet());

        binding.btnInvestInCard.setOnClickListener(view -> {
            AddInvestmentSheet addInvestmentSheet = new AddInvestmentSheet();
            addInvestmentSheet.show(getChildFragmentManager(), "AddInvestmentSheet");
        });

        binding.btnSeeGoals.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), GoalsActivity.class));
        });

        binding.notificationContainer.setOnClickListener(view -> {
            binding.notificationBadge.setVisibility(View.GONE);
            startActivity(new Intent(getActivity(), NotificationActivity.class));
        });
    }

    private void openAddTransactionSheet() {
        AddTransactionSheet addSheet = new AddTransactionSheet();
        addSheet.show(getChildFragmentManager(), "AddTransactionSheet");
    }

    private void calculateFinances(List<Transaction> transactions) {
        double income = 0;
        double expense = 0;
        double totalInvestment = 0;

        for (Transaction t : transactions) {
            if ("Investment".equals(t.getCategory())) {
                if (t.getAmount() > 0) {
                    totalInvestment += t.getAmount();
                }
            } else if (t.getAmount() > 0 && "Income".equals(t.getType())) {
                income += t.getAmount();
            } else {
                expense += Math.abs(t.getAmount());
            }
        }

        double balance = (income - totalInvestment) - expense;

        binding.tvTotalBalance.setText(String.format(Locale.US, "$%.2f", balance));
        binding.tvIncomeValue.setText(String.format(Locale.US, "$%.2f", income));
        binding.tvExpenseValue.setText(String.format(Locale.US, "$%.2f", expense));
        binding.tvTotalInvestment.setText(String.format(Locale.US, "$%.2f", totalInvestment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}