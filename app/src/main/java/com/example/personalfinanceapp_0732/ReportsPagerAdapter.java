package com.example.personalfinanceapp_0732;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ReportsPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 3;

    public ReportsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TabContentFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return NUM_TABS;
    }
}