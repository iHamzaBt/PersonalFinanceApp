package com.example.personalfinanceapp_0732;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalfinanceapp_0732.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ActivityOptions options = ActivityOptions.makeCustomAnimation(SplashActivity.this, R.anim.fade_in, R.anim.fade_out);
            startActivity(new Intent(SplashActivity.this, MainActivity.class), options.toBundle());
            finish();
        }, 2500);
    }
}