package com.example.personalfinanceapp_0732;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.personalfinanceapp_0732.databinding.ActivityGoalsBinding;

public class GoalsActivity extends AppCompatActivity {

    ActivityGoalsBinding binding;
    private TransactionViewModel viewModel;
    private GoalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBackGoals.setOnClickListener(view -> finish());

        binding.recyclerViewGoals.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewGoals.setHasFixedSize(true);
        adapter = new GoalAdapter();
        binding.recyclerViewGoals.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        viewModel.getAllGoals().observe(this, goals -> adapter.setGoals(goals));

        binding.btnAddGoal.setOnClickListener(view -> {
            AddGoalSheet addGoalSheet = new AddGoalSheet();
            addGoalSheet.show(getSupportFragmentManager(), "AddGoalSheet");
        });

        adapter.setOnItemClickListener(goal -> {
            FundGoalSheet fundSheet = new FundGoalSheet();
            fundSheet.setGoal(goal);
            fundSheet.show(getSupportFragmentManager(), "FundGoalSheet");
        });
    }
}