package com.barracchia.inventario.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.barracchia.inventario.MyApplication;

public class SharedPreferencesUtil {
    public static boolean getSharedPreference(String key, boolean defValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        return sharedPref.getBoolean(key, defValue);
    }
}
