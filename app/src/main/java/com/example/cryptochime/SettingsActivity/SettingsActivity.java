package com.example.cryptochime.SettingsActivity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptochime.R;

public class SettingsActivity extends AppCompatActivity {

    TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        titleText = findViewById(R.id.toolbarTitleText);
        titleText.setText("Settings");


        getSupportFragmentManager().beginTransaction().replace(R.id.settingsContainer_atvt,
                new SettingsFragment()).commit();

    }
}