package com.example.personalfinanceapp_0732;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.personalfinanceapp_0732.databinding.FragmentReportsBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private FragmentReportsBinding binding;
    private Calendar selectedPeriod;
    private ReportsPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedPeriod = Calendar.getInstance();
        updatePeriodText();

        binding.cardFilter.setOnClickListener(v -> showPeriodPickerDialog());

        adapter = new ReportsPagerAdapter(this);
        binding.viewPagerReports.setAdapter(adapter);
        binding.viewPagerReports.setOffscreenPageLimit(3);

        new TabLayoutMediator(binding.tabLayoutReports, binding.viewPagerReports,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("Overview"); break;
                        case 1: tab.setText("Expense"); break;
                        case 2: tab.setText("Income"); break;
                    }
                }
        ).attach();

        // تطبيق الفلتر الافتراضي (الشهر الحالي)
        applyPeriodFilterToAllTabs();
    }

    private void updatePeriodText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        binding.tvCurrentPeriod.setText(sdf.format(selectedPeriod.getTime()));
    }

    private void showPeriodPickerDialog() {
        Calendar maxDate = Calendar.getInstance(); // اليوم هو الحد الأقصى

        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (pickerView, year, month, dayOfMonth) -> {
                    selectedPeriod.set(Calendar.YEAR, year);
                    selectedPeriod.set(Calendar.MONTH, month);
                    selectedPeriod.set(Calendar.DAY_OF_MONTH, 1);
                    updatePeriodText();
                    applyPeriodFilterToAllTabs();
                },
                selectedPeriod.get(Calendar.YEAR),
                selectedPeriod.get(Calendar.MONTH),
                selectedPeriod.get(Calendar.DAY_OF_MONTH));

        // منع اختيار تاريخ في المستقبل
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        dialog.show();
    }

    private void applyPeriodFilterToAllTabs() {
        int year = selectedPeriod.get(Calendar.YEAR);
        int month = selectedPeriod.get(Calendar.MONTH);

        // تحقق إذا الشهر المختار في المستقبل
        Calendar now = Calendar.getInstance();
        if (year > now.get(Calendar.YEAR) ||
                (year == now.get(Calendar.YEAR) && month > now.get(Calendar.MONTH))) {
            // شهر مستقبلي — لا تعرض بيانات
            notifyAllTabs(-1, -1);
            return;
        }

        notifyAllTabs(year, month);
    }

    private void notifyAllTabs(int year, int month) {
        for (int i = 0; i < 3; i++) {
            Fragment fragment = getChildFragmentManager()
                    .findFragmentByTag("f" + i);
            if (fragment instanceof TabContentFragment) {
                ((TabContentFragment) fragment).setPeriodFilter(year, month);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}