package com.example.personalfinanceapp_0732;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.example.personalfinanceapp_0732.databinding.ActivityBiometricLockBinding;
import java.util.concurrent.Executor;

public class BiometricLockActivity extends AppCompatActivity {

    private ActivityBiometricLockBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBiometricLockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showBiometricPrompt();

        binding.btnTryAgain.setOnClickListener(v -> showBiometricPrompt());
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        goToMain();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        binding.tvStatus.setText("Authentication error: " + errString);
                        binding.btnTryAgain.setVisibility(android.view.View.VISIBLE);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        binding.tvStatus.setText("Fingerprint not recognized. Try again.");
                        binding.btnTryAgain.setVisibility(android.view.View.VISIBLE);
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verify Identity")
                .setSubtitle("Use your fingerprint to access the app")
                .setNegativeButtonText("Cancel")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void goToMain() {
        ActivityOptions options = ActivityOptions.makeCustomAnimation(
                this, R.anim.fade_in, R.anim.fade_out);
        startActivity(new Intent(this, MainActivity.class), options.toBundle());
        finish();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}