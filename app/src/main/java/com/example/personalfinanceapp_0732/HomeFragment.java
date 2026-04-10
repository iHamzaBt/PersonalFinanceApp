package com.example.personalfinanceapp_0732;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.example.personalfinanceapp_0732.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    private TransactionViewModel viewModel;
    private TransactionAdapter adapter;
    private HomeGoalAdapter homeGoalAdapter;
    private List<Goal> allCurrentGoals = new ArrayList<>();
    private List<Transaction> allTransactionsList = new ArrayList<>();
    private int currentPeriodFilter = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerViews();
        setupChart();
        setupPeriodSpinner();

        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            allTransactionsList = transactions;
            filterAndDisplayTransactions();
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

    private void setupPeriodSpinner() {
        String[] periods = {"This Month", "This Year", "All Time"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, periods);
        binding.spinnerPeriod.setAdapter(spinnerAdapter);

        binding.spinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPeriodFilter = position;
                filterAndDisplayTransactions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void filterAndDisplayTransactions() {
        if (allTransactionsList == null) return;

        List<Transaction> filteredList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        long startTime = 0;

        if (currentPeriodFilter == 0) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTimeInMillis();
        } else if (currentPeriodFilter == 1) {
            cal.set(Calendar.DAY_OF_YEAR, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startTime = cal.getTimeInMillis();
        }

        for (Transaction t : allTransactionsList) {
            if (t.getTimestamp() >= startTime) {
                filteredList.add(t);
            }
        }

        adapter.setTransactions(filteredList);
        calculateFinances(filteredList);
        updateChartData(filteredList);
    }

    private void setupChart() {
        binding.mainChart.setNoDataText("No transactions found.");
        binding.mainChart.setNoDataTextColor(Color.parseColor("#757575"));

        binding.mainChart.getDescription().setEnabled(false);
        binding.mainChart.setTouchEnabled(true);
        binding.mainChart.setDragEnabled(true);
        binding.mainChart.setScaleEnabled(false);
        binding.mainChart.setPinchZoom(false);
        binding.mainChart.setDrawGridBackground(false);

        binding.mainChart.getXAxis().setEnabled(false);
        binding.mainChart.getAxisRight().setEnabled(false);
        binding.mainChart.getLegend().setEnabled(false);

        YAxis leftAxis = binding.mainChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E0E0E0"));
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        binding.mainChart.setExtraTopOffset(15f);
        binding.mainChart.setExtraLeftOffset(20f);
        binding.mainChart.setExtraRightOffset(20f);

        CustomMarkerView mv = new CustomMarkerView(getContext(), R.layout.custom_marker_view);
        binding.mainChart.setMarker(mv);
    }

    private void updateChartData(List<Transaction> transactions) {
        List<Entry> entries = new ArrayList<>();
        List<Integer> pointColors = new ArrayList<>();
        float runningBalance = 0;

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction t = transactions.get(i);

            if ("Investment".equals(t.getCategory())) {
                runningBalance -= t.getAmount();
                pointColors.add(Color.parseColor("#FBC02D"));
            } else if ("Income".equals(t.getType())) {
                runningBalance += t.getAmount();
                pointColors.add(Color.parseColor("#4CAF50"));
            } else {
                runningBalance -= t.getAmount();
                pointColors.add(Color.parseColor("#E53935"));
            }

            entries.add(new Entry(transactions.size() - 1 - i, runningBalance, t));
        }

        if (entries.isEmpty()) {
            binding.mainChart.clear();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Balance Trend");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColor(Color.parseColor("#4CAF50"));
        dataSet.setLineWidth(3f);

        dataSet.setDrawCircles(true);
        dataSet.setCircleColors(pointColors);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(Color.WHITE);

        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        binding.mainChart.setData(lineData);

        binding.mainChart.animateX(1000);
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

        updateWidget(balance, income, expense);
    }

    private void updateWidget(double balance, double income, double expense) {
        SharedPreferences prefs = requireContext().getSharedPreferences("widget_data",
                android.content.Context.MODE_PRIVATE);
        prefs.edit()
                .putString("balance", String.format(Locale.US, "$%.2f", balance))
                .putString("income",  String.format(Locale.US, "$%.2f", income))
                .putString("expense", String.format(Locale.US, "$%.2f", expense))
                .apply();

        AppWidgetManager manager = AppWidgetManager.getInstance(requireContext());
        int[] ids = manager.getAppWidgetIds(
                new ComponentName(requireContext(), BalanceWidget.class));
        for (int id : ids) {
            BalanceWidget.updateWidget(requireContext(), manager, id);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}