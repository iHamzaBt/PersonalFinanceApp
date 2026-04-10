package com.example.personalfinanceapp_0732;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalfinanceapp_0732.databinding.FragmentReportsTabContentBinding;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class TabContentFragment extends Fragment {

    private FragmentReportsTabContentBinding binding;
    private TransactionViewModel viewModel;
    private int tabPosition;
    private List<Transaction> currentTabTransactions = new ArrayList<>();
    private float currentTotalAmount = 0f;
    private int selectedYear = -1;
    private int selectedMonth = -1;

    private static final int[] CHART_COLORS = {
            Color.parseColor("#2196F3"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#F44336"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#00BCD4"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#8BC34A"),
            Color.parseColor("#FF5722"),
            Color.parseColor("#607D8B"),
            Color.parseColor("#FFC107"),
            Color.parseColor("#795548"),
    };

    public static TabContentFragment newInstance(int position) {
        TabContentFragment fragment = new TabContentFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsTabContentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabPosition = getArguments() != null ? getArguments().getInt("position") : 0;
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        setupChartsConfiguration();

        viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            filterTransactionsForCurrentTab(transactions);
            updatePieChartData();
            updateBarChartData();
        });
    }

    public void setPeriodFilter(int year, int month) {
        this.selectedYear = year;
        this.selectedMonth = month;
        if (viewModel != null) {
            viewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
                filterTransactionsForCurrentTab(transactions);
                updatePieChartData();
                updateBarChartData();
            });
        }
    }

    private void filterTransactionsForCurrentTab(List<Transaction> allTransactions) {
        currentTabTransactions.clear();
        Calendar cal = Calendar.getInstance();

        for (Transaction t : allTransactions) {
            if (tabPosition == 1 && !"Expense".equals(t.getType())) continue;
            if (tabPosition == 2 && !"Income".equals(t.getType())) continue;

            if (selectedYear != -1 && selectedMonth != -1) {
                cal.setTimeInMillis(t.getTimestamp());
                int tYear = cal.get(Calendar.YEAR);
                int tMonth = cal.get(Calendar.MONTH);
                if (tYear != selectedYear || tMonth != selectedMonth) continue;
            }

            currentTabTransactions.add(t);
        }
    }

    private void setupChartsConfiguration() {
        binding.pieChartCategories.setNoDataText("No transactions found.");
        binding.pieChartCategories.setNoDataTextColor(Color.parseColor("#757575"));
        binding.barChartMonthly.setNoDataText("No transactions found.");
        binding.barChartMonthly.setNoDataTextColor(Color.parseColor("#757575"));

        binding.pieChartCategories.setUsePercentValues(false);
        binding.pieChartCategories.getDescription().setEnabled(false);
        binding.pieChartCategories.setExtraOffsets(5, 5, 5, 5);
        binding.pieChartCategories.setDrawEntryLabels(false);
        binding.pieChartCategories.setDrawHoleEnabled(true);
        binding.pieChartCategories.setHoleColor(Color.WHITE);
        binding.pieChartCategories.setHoleRadius(60f);
        binding.pieChartCategories.setTransparentCircleRadius(65f);
        binding.pieChartCategories.setHighlightPerTapEnabled(true);
        binding.pieChartCategories.setRotationEnabled(true);

        Legend pieLegend = binding.pieChartCategories.getLegend();
        pieLegend.setEnabled(true);
        pieLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        pieLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        pieLegend.setWordWrapEnabled(true);
        pieLegend.setDrawInside(false);
        pieLegend.setTextSize(12f);
        pieLegend.setXEntrySpace(16f);
        pieLegend.setYEntrySpace(8f);
        pieLegend.setFormSize(12f);
        pieLegend.setForm(Legend.LegendForm.CIRCLE);

        binding.pieChartCategories.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry pe = (PieEntry) e;
                    String text = String.format(Locale.US, "%s\n$%.2f", pe.getLabel(), pe.getValue());
                    binding.pieChartCategories.setCenterText(text);
                    binding.pieChartCategories.setCenterTextSize(14f);
                }
            }

            @Override
            public void onNothingSelected() {
                showDefaultTotal();
            }
        });

        binding.barChartMonthly.getDescription().setEnabled(false);
        binding.barChartMonthly.setDrawGridBackground(false);
        binding.barChartMonthly.setFitBars(true);
        binding.barChartMonthly.getXAxis().setDrawGridLines(false);
        binding.barChartMonthly.getXAxis().setGranularity(1f);
        binding.barChartMonthly.getXAxis().setGranularityEnabled(true);
        binding.barChartMonthly.getAxisLeft().setDrawGridLines(true);
        binding.barChartMonthly.getAxisLeft().setAxisMinimum(0f);
        binding.barChartMonthly.getAxisRight().setEnabled(false);
        binding.barChartMonthly.getLegend().setEnabled(false);

        binding.barChartMonthly.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            private final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                return (index >= 0 && index < 12) ? months[index] : "";
            }
        });
    }

    private void updatePieChartData() {
        if (currentTabTransactions.isEmpty()) {
            binding.pieChartCategories.clear();
            binding.pieChartCategories.setCenterText("");
            binding.pieChartCategories.invalidate();
            return;
        }

        HashMap<String, Float> categoryMap = new LinkedHashMap<>();
        currentTotalAmount = 0f;

        for (Transaction t : currentTabTransactions) {
            float amount = (float) Math.abs(t.getAmount());
            String cat = t.getCategory() != null ? t.getCategory() : "Other";
            categoryMap.put(cat, categoryMap.getOrDefault(cat, 0f) + amount);
            currentTotalAmount += amount;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();

        int colorIndex = 0;
        for (String category : categoryMap.keySet()) {
            float value = categoryMap.get(category);
            entries.add(new PieEntry(value, category));

            int color = CHART_COLORS[colorIndex % CHART_COLORS.length];
            colors.add(color);

            LegendEntry le = new LegendEntry();
            le.label = category;
            le.formColor = color;
            le.form = Legend.LegendForm.CIRCLE;
            legendEntries.add(le);

            colorIndex++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);

        PieData data = new PieData(dataSet);
        binding.pieChartCategories.setData(data);

        binding.pieChartCategories.getLegend().setCustom(legendEntries);

        showDefaultTotal();
        binding.pieChartCategories.animateY(1000);
        binding.pieChartCategories.invalidate();
    }

    private void showDefaultTotal() {
        if (!currentTabTransactions.isEmpty()) {
            String totalText = String.format(Locale.US, "Total\n$%.2f", currentTotalAmount);
            binding.pieChartCategories.setCenterText(totalText);
            binding.pieChartCategories.setCenterTextSize(16f);
        } else {
            binding.pieChartCategories.setCenterText("");
        }
    }

    private void updateBarChartData() {
        if (currentTabTransactions.isEmpty()) {
            binding.barChartMonthly.clear();
            binding.barChartMonthly.invalidate();
            return;
        }

        float[] monthlyTotals = new float[12];
        for (Transaction t : currentTabTransactions) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(t.getTimestamp());
            int month = cal.get(Calendar.MONTH);
            monthlyTotals[month] += (float) Math.abs(t.getAmount());
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, monthlyTotals[i]));
        }

        String label = (tabPosition == 2) ? "Income" : (tabPosition == 1 ? "Expense" : "Total");
        int barColor = (tabPosition == 2) ? Color.parseColor("#4CAF50") :
                (tabPosition == 1) ? Color.parseColor("#F44336") :
                        Color.parseColor("#2196F3");

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColor(barColor);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        binding.barChartMonthly.setData(barData);
        binding.barChartMonthly.getXAxis().setAxisMinimum(-0.5f);
        binding.barChartMonthly.getXAxis().setAxisMaximum(11.5f);
        binding.barChartMonthly.animateY(1000);
        binding.barChartMonthly.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}