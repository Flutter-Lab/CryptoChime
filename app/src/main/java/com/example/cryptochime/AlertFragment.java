package com.example.cryptochime;

import static android.content.Context.ALARM_SERVICE;

import androidx.fragment.app.Fragment;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlertFragment extends Fragment implements View.OnClickListener {

    //To call a method from this fragment to another class/fragment
    //private static AlertFragment instance;


    // Variable for alertView Layout
    TextView spinningTextView;
    EditText valueEditText;
    Dialog selectCoinDialog;
    Button setAlertButton;
    String selectedSymbol;
    TextView alertRLSymbolTextView, alertRLNameTextView, alertRLPriceTextView, alertRL24PercentTextView;
    ProgressBar loadingPB;
    boolean isLongAlarm;
    float userValue;

    CheckBox longAlarmCheckbox;

    //For Alert Types SpningList
    TextView alertTypeTextView;
    Dialog alertTypeDialog;

    //Alerm Manerger Variables
    Intent intent;
    PendingIntent pendingIntent;
    AlarmManager alarmManager;




    ArrayList<String> coinNameList;


    ArrayList<CurrencyRVModel> currencyRVModelArrayList;

    //Declare SharedPref variables
    SharedPreferences pref, prefUserValue, prefNotify, prefAlertTypeCode, prefisLongAlarm;

    static DecimalFormat df2 = new DecimalFormat("#.##");




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        //instance = this;

        //Assign SharedPref variables
        pref = this.getActivity().getSharedPreferences("mySymbol", Context.MODE_PRIVATE);
        prefUserValue = this.getActivity().getSharedPreferences("userValue", Context.MODE_PRIVATE);
        prefNotify = this.getActivity().getSharedPreferences("isNotified", Context.MODE_PRIVATE);
        prefAlertTypeCode = this.getActivity().getSharedPreferences("alertTypeCode", Context.MODE_PRIVATE);
        prefisLongAlarm = this.getActivity().getSharedPreferences("isLongAlarm", Context.MODE_PRIVATE);




        //Assign alertView Variables
        spinningTextView = view.findViewById(R.id.spinningTextViewId);
        coinNameList = new ArrayList<String>();
        //puEditText = view.findViewById(R.id.puEdtTxt);
        //pdEditText = view.findViewById(R.id.pdEdtTxt);
        alertTypeTextView = view.findViewById(R.id.alertTypeTextView);
        valueEditText = view.findViewById(R.id.valueEditText);
        setAlertButton = view.findViewById(R.id.setAlertButton);
        setAlertButton.setOnClickListener(this);

        loadingPB = view.findViewById(R.id.progressBar);
        longAlarmCheckbox = view.findViewById(R.id.longAlarmCheckBox);

        currencyRVModelArrayList = new ArrayList<CurrencyRVModel>();



        //Alert Page Coin Preview Block
        alertRLSymbolTextView = view.findViewById(R.id.alertRLSymbolTextView);
        alertRLNameTextView = view.findViewById(R.id.alertRLNameTextView);
        alertRLPriceTextView = view.findViewById(R.id.alerRLPriceTextView);
        alertRL24PercentTextView = view.findViewById(R.id.alertRL24PercentTextView);

        //Set Long Alarm Checkbox status
        isLongAlarm = prefisLongAlarm.getBoolean("isLongAlarm", false);
        longAlarmCheckbox.setChecked(isLongAlarm);

        



        getCurrencyData();
        instatiateSpinningList();
        alertTypesList();







        longAlarmCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                prefisLongAlarm.edit().putBoolean("isLongAlarm", b).apply();

                //Log.i("Long Alarm", String.valueOf(b));


            }
        });



        return view;

    }


    private void instatiateSpinningList(){
        spinningTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize dialog
                selectCoinDialog = new Dialog(getContext());
                //Set custom dialog
                selectCoinDialog.setContentView(R.layout.dialog_searchable_spinner);
                //Set custom height and width
                selectCoinDialog.getWindow().setLayout(650, 800);
                //Set transparent background
                selectCoinDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Show dialog
                selectCoinDialog.show();

                //Initialize and assign variable
                EditText editText = selectCoinDialog.findViewById(R.id.searchEditText);
                ListView listView = selectCoinDialog.findViewById(R.id.searchListView);


                //Initialize Array adapter for spinning list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, coinNameList);

                //Set Adapter to spinning list
                listView.setAdapter(adapter);



                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        //Filter array list
                        adapter.getFilter().filter(charSequence);

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //When item selected from list
                        //Set selected item on text view
                        spinningTextView.setText(adapter.getItem(i));

                        selectedSymbol = adapter.getItem(i);

                        pref.edit().putString("mySymbol", selectedSymbol).apply();

                        //Show Info in Infor RL
                        for (CurrencyRVModel element : currencyRVModelArrayList) {

                            if (element.symbol == selectedSymbol) {

                                alertRLSymbolTextView.setText(element.symbol);
                                alertRLNameTextView.setText(element.name);
                                alertRLPriceTextView.setText("$"+ df2.format(element.price));
                                alertRL24PercentTextView.setText(df2.format(element.pc24h)+"%");
                            }
                        }


                        //Dismiss dialog
                        selectCoinDialog.dismiss();
                    }
                });
            }
        });
    }


    private void getCurrencyData(){

        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.nomics.com/v1/currencies/ticker?key=ecae4f8ae82014deed75f16f14d03f2c21a819b1&per-page=100&page=1";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        long mRequestStartTime = System.currentTimeMillis(); // set the request start time just before you send the request.

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                loadingPB.setVisibility(View.GONE);

                try {
                    for (int i=0; i<response.length(); i++){
                        JSONObject dataObj = response.getJSONObject(i);
                        String symbol = dataObj.getString("symbol");
                        String name = dataObj.getString("name");
                        double price = dataObj.getDouble("price");

//                        JSONObject quote = dataObj.getJSONObject("quote");
//                        JSONObject USD = quote.getJSONObject("USD");
//                        double price = USD.getDouble("price");
//                        double pc24h = USD.getDouble("percent_change_24h");

                        JSONObject oneDay = dataObj.getJSONObject("1d");
                        double pc24h = oneDay.getDouble("price_change_pct")* 100;

                        currencyRVModelArrayList.add(new CurrencyRVModel(symbol, name, price, pc24h));
                        //Add name to coinNameList Array fro AlertPage;
                        coinNameList.add(symbol);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getContext(), "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                }

                // calculate the duration in milliseconds
                long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;

                Log.i("TotalRequest Time", ""+totalRequestTime);
                Log.i("TotalRequest Time", ""+totalRequestTime);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Fail to get the data", Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonArrayRequest);

    }




    private void alertTypesList(){
        alertTypeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Initialize dialog
                alertTypeDialog = new Dialog(getContext());
                //Set custom dialog
                alertTypeDialog.setContentView(R.layout.dialog_searchable_spinner2);
                //Set custom height and width
                alertTypeDialog.getWindow().setLayout(650, 800);
                //Set transparent background
                alertTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //Show dialog
                alertTypeDialog.show();

                //Initialize and assign variable
                //EditText editText = selectCoinDialog.findViewById(R.id.searchEditText);
                ListView listView = alertTypeDialog.findViewById(R.id.searchListView);

                //Alert Types List
                ArrayList<String> alertTypesList = new ArrayList<String>();
                alertTypesList.add("Price rises above");
                alertTypesList.add("Price drops to");
                alertTypesList.add("24H change is over (%)");
                alertTypesList.add("24H change is down (%)");


                //Initialize Array adapter for spinning list
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, alertTypesList);

                //Set Adapter to spinning list
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //When item selected from list
                        //Set selected item on text view
                        alertTypeTextView.setText(adapter.getItem(i));
                        //alertTypeCode = i;
                        prefAlertTypeCode.edit().putInt("alertTypeCode", i).apply();



                        //Dismiss dialog
                        alertTypeDialog.dismiss();
                    }
                });
            }
        });


    }




    //Set Alert Button Click Method
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setAlertButton){

            for(CurrencyRVModel element : currencyRVModelArrayList){

                if(element.symbol.equals(selectedSymbol)){
                    //userValue = Float.parseFloat(String.valueOf(valueEditText));
                    userValue = Float.parseFloat(valueEditText.getText().toString());
                    prefUserValue.edit().putFloat("userValue", userValue).apply();

                    setAlarm();



                }
            }
        }
    }


    public void setAlarm(){
        Toast.makeText(getContext(), "Reminder Set!", Toast.LENGTH_SHORT).show();
        int alertId = 0;

        intent = new Intent(getActivity(), NotificationBroadcast.class );
        intent.putExtra("alertId", alertId);
        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent,0);

        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        prefNotify.edit().putInt("isNotified", 0).apply();

        long interval = 5000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                1000, interval,
                pendingIntent);
    }




    private void getCurrencyDataBackup(){

        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        long mRequestStartTime = System.currentTimeMillis(); // set the request start time just before you send the request.

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);

                try {
                    JSONArray dataArray = response.getJSONArray("data");

                    for (int i=0; i<dataArray.length(); i++){
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String symbol = dataObj.getString("symbol");
                        String name = dataObj.getString("name");

                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");

                        double price = USD.getDouble("price");
                        double pc24h = USD.getDouble("percent_change_24h");

                        currencyRVModelArrayList.add(new CurrencyRVModel(symbol, name, price, pc24h));
                        //Add name to coinNameList Array fro AlertPage;
                        coinNameList.add(symbol);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    Toast.makeText(getContext(), "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                }

                // calculate the duration in milliseconds
                long totalRequestTime = System.currentTimeMillis() - mRequestStartTime;

                Log.i("TotalRequest Time", ""+totalRequestTime);
                Log.i("TotalRequest Time", ""+totalRequestTime);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getContext(), "Fail to get the data", Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-CMC_PRO_API_KEY", "cf22e625-7f13-4277-857c-6c60021e50dd");
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);


    }



}
