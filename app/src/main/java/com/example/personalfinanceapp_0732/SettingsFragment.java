package com.example.personalfinanceapp_0732;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.personalfinanceapp_0732.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences prefs;
    private TransactionViewModel viewModel;

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_THEME = "theme";
    private static final String KEY_FIRST_DAY = "first_day";
    private static final String KEY_USER_NAME = "user_name";

    private final String[] firstDays = {"Sunday", "Monday", "Saturday"};
    private final String[] themes = {"Light", "Dark", "System Default"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        viewModel = new ViewModelProvider(requireActivity()).get(TransactionViewModel.class);

        loadCurrentSettings();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.tvCurrencyValue.setText(prefs.getString(KEY_CURRENCY, "USD $"));
    }

    private void loadCurrentSettings() {
        binding.tvUserNameValue.setText(prefs.getString(KEY_USER_NAME, "Hamza Barakat"));
        binding.tvCurrencyValue.setText(prefs.getString(KEY_CURRENCY, "USD $"));
        binding.tvThemeValue.setText(prefs.getString(KEY_THEME, "System Default"));
        binding.tvFirstDayValue.setText(prefs.getString(KEY_FIRST_DAY, "Sunday"));
        boolean biometricEnabled = prefs.getBoolean("biometric_enabled", false);
        binding.tvBiometricValue.setText(biometricEnabled ? "Enabled" : "Disabled");
        binding.switchBiometric.setChecked(biometricEnabled);
    }

    private void setupClickListeners() {

        binding.rowUserName.setOnClickListener(v -> showChangeNameDialog());

        binding.rowCurrency.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left,
                                R.anim.slide_in_left,
                                R.anim.slide_out_right)
                        .replace(R.id.fragment_container, new CurrencySelectionFragment())
                        .addToBackStack(null)
                        .commit()
        );

        binding.rowTheme.setOnClickListener(v -> showSelectionDialog(
                "Select Theme",
                themes,
                prefs.getString(KEY_THEME, "System Default"),
                selected -> {
                    prefs.edit().putString(KEY_THEME, selected).apply();
                    binding.tvThemeValue.setText(selected);
                    applyTheme(selected);
                }
        ));

        binding.rowFirstDay.setOnClickListener(v -> showSelectionDialog(
                "First Day of Week",
                firstDays,
                prefs.getString(KEY_FIRST_DAY, "Sunday"),
                selected -> {
                    prefs.edit().putString(KEY_FIRST_DAY, selected).apply();
                    binding.tvFirstDayValue.setText(selected);
                }
        ));

        binding.rowResetData.setOnClickListener(v -> showResetConfirmationDialog());

        binding.rowBiometric.setOnClickListener(v -> {
            boolean currentlyEnabled = prefs.getBoolean("biometric_enabled", false);

            if (!BiometricHelper.isBiometricAvailable(requireContext())) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Not Available")
                        .setMessage("Biometric authentication is not set up on this device. Please add a fingerprint in your device settings first.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            if (!currentlyEnabled) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Enable Fingerprint Lock")
                        .setMessage("Important:\n\n• You will need your fingerprint every time you open the app.\n\n• Make sure your fingerprint is registered on this device.\n\nDo you want to enable fingerprint lock?")
                        .setPositiveButton("Enable", (dialog, which) -> {
                            prefs.edit().putBoolean("biometric_enabled", true).apply();
                            binding.tvBiometricValue.setText("Enabled");
                            binding.switchBiometric.setChecked(true);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                prefs.edit().putBoolean("biometric_enabled", false).apply();
                binding.tvBiometricValue.setText("Disabled");
                binding.switchBiometric.setChecked(false);
            }
        });

        binding.rowWidget.setOnClickListener(v ->
                new AlertDialog.Builder(requireContext())
                        .setTitle("Balance Widget")
                        .setMessage("To add the widget:\n\n1. Long press on your home screen\n\n2. Tap 'Widgets'\n\n3. Find 'Elevate'\n\n4. Drag the Balance widget to your screen")
                        .setPositiveButton("Got it", null)
                        .show()
        );
    }

    private void showChangeNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Change Your Name");

        final EditText input = new EditText(requireContext());
        input.setText(prefs.getString(KEY_USER_NAME, "Hamza Barakat"));

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                prefs.edit().putString(KEY_USER_NAME, newName).apply();
                binding.tvUserNameValue.setText(newName);
                Toast.makeText(getContext(), "Name saved!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSelectionDialog(String title, String[] options, String current, OnOptionSelected callback) {
        int currentIndex = 0;
        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(current)) {
                currentIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setSingleChoiceItems(options, currentIndex, (dialog, which) -> {
                    callback.onSelected(options[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showResetConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Reset All Data")
                .setMessage("This will permanently delete all transactions, goals, and notifications. This action cannot be undone.")
                .setPositiveButton("Reset", (dialog, which) -> {
                    viewModel.deleteAllTransactions();
                    viewModel.deleteAllGoals();
                    viewModel.deleteAllNotifications();
                    showSuccessDialog();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Done")
                .setMessage("All data has been reset successfully.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void applyTheme(String theme) {
        switch (theme) {
            case "Light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    interface OnOptionSelected {
        void onSelected(String option);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}