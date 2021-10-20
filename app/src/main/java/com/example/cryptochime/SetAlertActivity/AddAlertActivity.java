package com.example.cryptochime.SetAlertActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptochime.R;

public class AddAlertActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alert);

        getSupportFragmentManager().beginTransaction().replace(R.id.add_alert_fragment_container,
                new ChoseCurrencyFragment()).commit();

    }


    //When finish this activity
    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("passed_item", "Null");
        setResult(RESULT_OK, returnIntent);
        super.finish();
    }



}