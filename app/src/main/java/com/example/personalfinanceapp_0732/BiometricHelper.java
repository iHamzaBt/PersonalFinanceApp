package com.example.personalfinanceapp_0732;

import android.content.Context;
import androidx.biometric.BiometricManager;

public class BiometricHelper {

    public static boolean isBiometricAvailable(Context context) {
        BiometricManager manager = BiometricManager.from(context);
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;
    }
}