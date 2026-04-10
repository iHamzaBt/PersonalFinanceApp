package com.example.personalfinanceapp_0732;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.airbnb.lottie.LottieAnimationView;
import com.example.personalfinanceapp_0732.databinding.ActivityOnboardingBinding;
import android.content.Intent;
import android.app.ActivityOptions;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter adapter;

    private final int[] lottieAnimations = {
            R.raw.onboarding_track,
            R.raw.onboarding_goals,
            R.raw.onboarding_reports,
            R.raw.onboarding_ready
    };

    private final String[] titles = {
            "Track Every Penny",
            "Set Smart Goals",
            "Visual Insights",
            "Ready to Start?"
    };

    private final String[] subtitles = {
            "Log income, expenses & investments in seconds. Stay on top of every transaction effortlessly.",
            "Create savings goals and watch your progress grow. Turn dreams into achievable milestones.",
            "Beautiful charts and reports that help you understand where your money goes each month.",
            "Take control of your financial future. Your journey to smarter money starts now."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new OnboardingAdapter();
        binding.viewPager.setAdapter(adapter);

        setupDots(0);

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setupDots(position);
                updateButtons(position);
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            if (current < lottieAnimations.length - 1) {
                binding.viewPager.setCurrentItem(current + 1);
            } else {
                finishOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupDots(int position) {
        binding.dotsContainer.removeAllViews();
        for (int i = 0; i < lottieAnimations.length; i++) {
            ImageView dot = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    i == position ? 24 : 8, 8);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == position ?
                    R.drawable.dot_active : R.drawable.dot_inactive);
            binding.dotsContainer.addView(dot);
        }
    }

    private void updateButtons(int position) {
        boolean isLast = position == lottieAnimations.length - 1;
        binding.btnSkip.setVisibility(isLast ? View.GONE : View.VISIBLE);
        binding.btnNext.setText(isLast ? "Get Started" : "Next");
    }

    private void finishOnboarding() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        prefs.edit().putBoolean("onboarding_done", true).apply();

        ActivityOptions options = ActivityOptions.makeCustomAnimation(
                this, R.anim.fade_in, R.anim.fade_out);
        startActivity(new Intent(this, MainActivity.class), options.toBundle());
        finish();
    }

    class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_onboarding, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.lottie.setAnimation(lottieAnimations[position]);
            holder.lottie.playAnimation();
            holder.title.setText(titles[position]);
            holder.subtitle.setText(subtitles[position]);
        }

        @Override
        public int getItemCount() {
            return lottieAnimations.length;
        }

        class VH extends RecyclerView.ViewHolder {
            LottieAnimationView lottie;
            android.widget.TextView title, subtitle;

            VH(View itemView) {
                super(itemView);
                lottie = itemView.findViewById(R.id.lottieOnboarding);
                title = itemView.findViewById(R.id.tvOnboardingTitle);
                subtitle = itemView.findViewById(R.id.tvOnboardingSubtitle);
            }
        }
    }
}