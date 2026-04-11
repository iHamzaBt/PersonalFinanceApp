package com.example.personalfinanceapp_0732;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.personalfinanceapp_0732.databinding.FragmentCurrencySelectionBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencySelectionFragment extends Fragment {

    private FragmentCurrencySelectionBinding binding;
    private CurrencyAdapter adapter;
    private SharedPreferences prefs;

    private final List<CurrencyItem> allCurrencies = Arrays.asList(
            new CurrencyItem("USD", "US Dollar", "$", "🇺🇸"),
            new CurrencyItem("EUR", "Euro", "€", "🇪🇺"),
            new CurrencyItem("GBP", "British Pound", "£", "🇬🇧"),
            new CurrencyItem("JPY", "Japanese Yen", "¥", "🇯🇵"),
            new CurrencyItem("SAR", "Saudi Riyal", "﷼", "🇸🇦"),
            new CurrencyItem("AED", "UAE Dirham", "د.إ", "🇦🇪"),
            new CurrencyItem("ILS", "Israeli Shekel", "₪", "🇮🇱"),
            new CurrencyItem("JOD", "Jordanian Dinar", "د.أ", "🇯🇴"),
            new CurrencyItem("CAD", "Canadian Dollar", "$", "🇨🇦"),
            new CurrencyItem("AUD", "Australian Dollar", "$", "🇦🇺"),
            new CurrencyItem("CHF", "Swiss Franc", "Fr", "🇨🇭"),
            new CurrencyItem("CNY", "Chinese Yuan", "¥", "🇨🇳"),
            new CurrencyItem("INR", "Indian Rupee", "₹", "🇮🇳"),
            new CurrencyItem("KWD", "Kuwaiti Dinar", "د.ك", "🇰🇼"),
            new CurrencyItem("QAR", "Qatari Riyal", "ر.ق", "🇶🇦"),
            new CurrencyItem("EGP", "Egyptian Pound", "E£", "🇪🇬"),
            new CurrencyItem("TRY", "Turkish Lira", "₺", "🇹🇷"),
            new CurrencyItem("BRL", "Brazilian Real", "R$", "🇧🇷"),
            new CurrencyItem("MXN", "Mexican Peso", "$", "🇲🇽"),
            new CurrencyItem("SGD", "Singapore Dollar", "$", "🇸🇬"),
            new CurrencyItem("HKD", "Hong Kong Dollar", "$", "🇭🇰"),
            new CurrencyItem("NOK", "Norwegian Krone", "kr", "🇳🇴"),
            new CurrencyItem("SEK", "Swedish Krona", "kr", "🇸🇪"),
            new CurrencyItem("DKK", "Danish Krone", "kr", "🇩🇰"),
            new CurrencyItem("PLN", "Polish Złoty", "zł", "🇵🇱"),
            new CurrencyItem("ZAR", "South African Rand", "R", "🇿🇦"),
            new CurrencyItem("NGN", "Nigerian Naira", "₦", "🇳🇬"),
            new CurrencyItem("PKR", "Pakistani Rupee", "₨", "🇵🇰"),
            new CurrencyItem("BDT", "Bangladeshi Taka", "৳", "🇧🇩"),
            new CurrencyItem("IDR", "Indonesian Rupiah", "Rp", "🇮🇩")
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCurrencySelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireContext().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE);
        String selectedCode = prefs.getString("selected_currency_code", "USD");

        adapter = new CurrencyAdapter(new ArrayList<>(allCurrencies), selectedCode, item -> {
            prefs.edit()
                    .putString("currency", item.code + " " + item.symbol)
                    .putString("selected_currency_code", item.code)
                    .apply();
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        binding.recyclerCurrencies.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));
        binding.recyclerCurrencies.setAdapter(adapter);
        binding.recyclerCurrencies.setHasFixedSize(true);

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCurrencies(s.toString());
                binding.ivClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.ivClear.setOnClickListener(v -> binding.etSearch.setText(""));

        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void filterCurrencies(String query) {
        List<CurrencyItem> filtered = new ArrayList<>();
        for (CurrencyItem item : allCurrencies) {
            if (item.name.toLowerCase().contains(query.toLowerCase()) ||
                    item.code.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        adapter.updateList(filtered);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}