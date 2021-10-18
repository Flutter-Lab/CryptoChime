package com.example.cryptochime.SetAlertActivity;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.cryptochime.AlertFragment2;
import com.example.cryptochime.MainActivity;
import com.example.cryptochime.NotificationBroadcast;
import com.example.cryptochime.R;
import com.example.cryptochime.SetAlertActivity.AlertDB.AlertDatabase;
import com.example.cryptochime.SetAlertActivity.AlertDB.Alert;


public class SetValueFragment extends Fragment {
    MainActivity mainActivity = new MainActivity();

    EditText valueEditText;
    TextView wrongInputTextView;
    CheckBox loudAlertCheckBox;
    Button setAlertButton2;

    int alertCode;
    float currentPrice, pc24h;

    String userValueText, alertTypeText, alertSymbol;

    SharedPreferences prefAlertTypeCode, selectedCoinCurrPrice, selectedCoinPC24h, prefUserValue, selectedCoinPref;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_value, container, false);

        valueEditText = view.findViewById(R.id.valueEditText);
        wrongInputTextView = view.findViewById(R.id.wrongInputTextView);
        loudAlertCheckBox = view.findViewById(R.id.loudAlertCheckBox);

        prefAlertTypeCode = getContext().getSharedPreferences("alertTypeCode", Context.MODE_PRIVATE);
        selectedCoinCurrPrice = getContext().getSharedPreferences("selectedCoinCurrPrice", Context.MODE_PRIVATE);
        selectedCoinPC24h = getContext().getSharedPreferences("selectedCoinPC24h", Context.MODE_PRIVATE);
        prefUserValue = getContext().getSharedPreferences("userValue", Context.MODE_PRIVATE);
        selectedCoinPref = getContext().getSharedPreferences("selectedCoin", Context.MODE_PRIVATE);

        currentPrice = selectedCoinCurrPrice.getFloat("selectedCoinCurrPrice", 0);
        pc24h = selectedCoinPC24h.getFloat("selectedCoinPC24h", 0);
        alertSymbol = selectedCoinPref.getString("selectedCoin", null);


        //Set ALert Button OnClick
        initSetAlertBtn(view);


        //Printing Alert Code to Log
        alertCode = prefAlertTypeCode.getInt("alertTypeCode", 5);
        Log.i("AlertCode: ", alertCode + "");
        Log.i("Curent Price::", currentPrice + "");


        if (alertCode < 2) {
            valueEditText.setText(String.valueOf(mainActivity.dynDF(currentPrice).format(currentPrice)));
        } else {
            valueEditText.setText(String.valueOf(pc24h));
        }


        return view;
    }

    private void initSetAlertBtn(View view) {
        setAlertButton2 = view.findViewById(R.id.setAlertButton2);

        setAlertButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Alert Code is"+alertCode, Toast.LENGTH_SHORT).show();
                Log.i("Alert Code", "Alert code in Button Click " + alertCode);
                setAlarm();

                userValueText = valueEditText.getText().toString();

                float userValue = Float.parseFloat(userValueText);

                switch (alertCode) {
                    case 0:
                        if (userValue > currentPrice) {
                            //Log.i("Info", "User Value is: " + userValue + ", Current Price is $" + currentPrice);
                            //Toast.makeText(getActivity(), "Alert Added for Price Up", Toast.LENGTH_SHORT).show();
                            alertTypeText = "Price above";
                            gotoAlertList();
                        } else {
                            wrongInputTextView.setText("Value should be larger than current price - $" + mainActivity.dynDF(currentPrice).format(currentPrice));
                            wrongInputTextView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 1:
                        if (userValue < currentPrice) {
                            //Toast.makeText(getActivity(), "Alert Added for Price Down", Toast.LENGTH_SHORT).show();
                            alertTypeText = "Price down";
                            gotoAlertList();
                        } else {
                            wrongInputTextView.setText("Value should be smaller than current price - " + mainActivity.dynDF(currentPrice).format(currentPrice));
                            wrongInputTextView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case 2:
                        if (userValue > pc24h) {
                            //Toast.makeText(getActivity(), "Alert Added for % Up", Toast.LENGTH_SHORT).show();
                            alertTypeText = "24H change above";
                            gotoAlertList();
                        } else {
                            wrongInputTextView.setText("Value should be larger than current 24h % Change - " + pc24h + "%");
                            wrongInputTextView.setVisibility(View.VISIBLE);
                        }
                        break;

                    case 3:
                        if (userValue < pc24h) {
                            //Toast.makeText(getActivity(), "Alert Added for % Down", Toast.LENGTH_SHORT).show();
                            alertTypeText = "24H change below";
                            gotoAlertList();
                        } else {
                            wrongInputTextView.setText("Value should be smaller than current 24h % Change - " + pc24h + "%");
                            wrongInputTextView.setVisibility(View.VISIBLE);
                        }
                        break;
                }

                //gotoAlertList();

            }
        });
    }

    private void saveNewAlert(String currencyName, String alertType, String alertValue, int alertCode){

        AlertDatabase db = AlertDatabase.getInstance(getActivity().getApplicationContext());

        Alert alert = new Alert();
        alert.currencyName = currencyName;
        alert.alertType = alertType;
        alert.alertValue = alertValue;
        alert.alertTypeCode = alertCode;

        db.alertDao().insertAlert(alert);

    }

    private void gotoAlertList(){
        saveNewAlert(alertSymbol, alertTypeText, userValueText, alertCode);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_alert_fragment_container,
                new AlertFragment2()).commit();
    }

    public void setAlarm(){
        Toast.makeText(getContext(), "Reminder Set!", Toast.LENGTH_SHORT).show();
        int alertId = 0;

        Intent intent = new Intent(getActivity(), NotificationBroadcast.class );
        intent.putExtra("alertId", alertId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent,0);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        //prefNotify.edit().putInt("isNotified", 0).apply();
        long interval = 5000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                1000, interval,
                pendingIntent);

    }


}