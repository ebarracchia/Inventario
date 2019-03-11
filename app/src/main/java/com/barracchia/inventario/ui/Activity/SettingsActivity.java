package com.barracchia.inventario.ui.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.barracchia.inventario.ui.Fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_PREF_VALIDATE = "pref_validate";
    public static final String KEY_PREF_URL = "pref_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
