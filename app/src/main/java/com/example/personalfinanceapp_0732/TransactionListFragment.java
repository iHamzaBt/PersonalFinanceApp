package com.example.personalfinanceapp_0732;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personalfinanceapp_0732.databinding.FragmentTransactionListBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TransactionListFragment extends Fragment {

    private FragmentTransactionListBinding binding;
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private List<Transaction> allTransactionsList = new ArrayList<>();

    private String currentSearchQuery = "";
    private String currentTypeFilter = "All";
    private String currentCategoryFilter = "All";
    private String currentDateFilter = "All Time";
    private String currentSortMethod = "Newest";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransactionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSearchAndFilters();
        setupClickListeners();

        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);
        viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                allTransactionsList = transactions;
                applyFiltersAndSort();
            }
        });
    }

    private void setupRecyclerView() {
        binding.recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TransactionAdapter();
        binding.recyclerViewTransactions.setAdapter(adapter);

        adapter.setOnItemClickListener(transaction -> {
            if ("Goal".equals(transaction.getCategory())) {
                Toast.makeText(requireContext(), "Manage goals from the Goals section", Toast.LENGTH_SHORT).show();
                return;
            }

            TransactionDetailsFragment detailsFragment = new TransactionDetailsFragment();
            detailsFragment.setTransaction(transaction);
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
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
                viewModel.delete(transactionToDelete);
                Toast.makeText(requireContext(), "Transaction Deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(binding.recyclerViewTransactions);
    }

    private void setupSearchAndFilters() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipTypeFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add("All");
            popup.getMenu().add("Income");
            popup.getMenu().add("Expense");
            popup.getMenu().add("Investment");
            popup.setOnMenuItemClickListener(item -> {
                currentTypeFilter = item.getTitle().toString();
                binding.chipTypeFilter.setText("Type: " + currentTypeFilter);
                applyFiltersAndSort();
                return true;
            });
            popup.show();
        });

        binding.chipCategoryFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add("All");
            popup.getMenu().add("Food");
            popup.getMenu().add("Bills");
            popup.getMenu().add("Shopping");
            popup.getMenu().add("Salary");
            popup.setOnMenuItemClickListener(item -> {
                currentCategoryFilter = item.getTitle().toString();
                binding.chipCategoryFilter.setText("Cat: " + currentCategoryFilter);
                applyFiltersAndSort();
                return true;
            });
            popup.show();
        });

        binding.chipDateFilter.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add("All Time");
            popup.getMenu().add("This Month");
            popup.getMenu().add("This Year");
            popup.setOnMenuItemClickListener(item -> {
                currentDateFilter = item.getTitle().toString();
                binding.chipDateFilter.setText("Date: " + currentDateFilter);
                applyFiltersAndSort();
                return true;
            });
            popup.show();
        });

        binding.chipSort.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add("Newest");
            popup.getMenu().add("Oldest");
            popup.getMenu().add("Highest Amount");
            popup.getMenu().add("Lowest Amount");
            popup.setOnMenuItemClickListener(item -> {
                currentSortMethod = item.getTitle().toString();
                binding.chipSort.setText("Sort: " + currentSortMethod.split(" ")[0]);
                applyFiltersAndSort();
                return true;
            });
            popup.show();
        });
    }

    private void setupClickListeners() {
        binding.btnEmptyStateAdd.setOnClickListener(v -> openAddFragment());

        if (binding.fabAddTransaction != null) {
            binding.fabAddTransaction.setOnClickListener(v -> openAddFragment());
        }
    }

    private void openAddFragment() {
        AddEditTransactionFragment addFragment = new AddEditTransactionFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.fragment_container, addFragment)
                .addToBackStack(null)
                .commit();
    }

    private void applyFiltersAndSort() {
        List<Transaction> filteredList = new ArrayList<>();

        long startTime = 0;
        Calendar cal = Calendar.getInstance();
        if (currentDateFilter.equals("This Month")) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTimeInMillis();
        } else if (currentDateFilter.equals("This Year")) {
            cal.set(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTimeInMillis();
        }

        for (Transaction t : allTransactionsList) {
            boolean matchesSearch = currentSearchQuery.isEmpty() ||
                    t.getTitle().toLowerCase().contains(currentSearchQuery) ||
                    t.getCategory().toLowerCase().contains(currentSearchQuery);

            boolean matchesType = false;
            if (currentTypeFilter.equals("All")) {
                matchesType = true;
            } else if (currentTypeFilter.equals("Investment") && t.getCategory().equals("Investment")) {
                matchesType = true;
            } else if (currentTypeFilter.equals("Income") && t.getType().equals("Income") && !t.getCategory().equals("Investment")) {
                matchesType = true;
            } else if (currentTypeFilter.equals("Expense") && t.getType().equals("Expense") && !t.getCategory().equals("Investment")) {
                matchesType = true;
            }

            boolean matchesCategory = currentCategoryFilter.equals("All") || t.getCategory().equals(currentCategoryFilter);

            boolean matchesDate = t.getTimestamp() >= startTime;

            if (matchesSearch && matchesType && matchesCategory && matchesDate) {
                filteredList.add(t);
            }
        }

        Collections.sort(filteredList, (t1, t2) -> {
            switch (currentSortMethod) {
                case "Oldest":
                    return Long.compare(t1.getTimestamp(), t2.getTimestamp());
                case "Highest Amount":
                    return Double.compare(t2.getAmount(), t1.getAmount());
                case "Lowest Amount":
                    return Double.compare(t1.getAmount(), t2.getAmount());
                case "Newest":
                default:
                    return Long.compare(t2.getTimestamp(), t1.getTimestamp());
            }
        });

        adapter.setTransactions(filteredList);
        toggleEmptyState(filteredList.isEmpty());
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.recyclerViewTransactions.setVisibility(View.GONE);
            binding.groupEmptyState.setVisibility(View.VISIBLE);

            if (!currentSearchQuery.isEmpty() || !currentTypeFilter.equals("All") || !currentCategoryFilter.equals("All") || !currentDateFilter.equals("All Time")) {
                binding.tvEmptyMessage.setText("No results match your filters.");
                binding.btnEmptyStateAdd.setVisibility(View.GONE);
                if (binding.fabAddTransaction != null) binding.fabAddTransaction.setVisibility(View.VISIBLE);
            } else {
                binding.tvEmptyMessage.setText("No transactions yet.");
                binding.btnEmptyStateAdd.setVisibility(View.VISIBLE);
                if (binding.fabAddTransaction != null) binding.fabAddTransaction.setVisibility(View.GONE);
            }
        } else {
            binding.recyclerViewTransactions.setVisibility(View.VISIBLE);
            binding.groupEmptyState.setVisibility(View.GONE);
            if (binding.fabAddTransaction != null) binding.fabAddTransaction.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}