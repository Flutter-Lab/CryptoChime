package com.zottz.cryptochime.SetAlertActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.zottz.cryptochime.CurrencyRVModel;
import com.zottz.cryptochime.MainActivity;
import com.zottz.cryptochime.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ChoseCurrencyFragment extends Fragment {
    MainActivity mainActivity = new MainActivity();

    ListView listView;
    ArrayList<String> coinArrayList;
    EditText coinListEditText;
    ArrayAdapter<String> adapter;

    ProgressBar loadingPBAlert;

    ArrayList<CurrencyRVModel> currencyRVModelArrayList;

    SharedPreferences selectedCoinPref, selectedCoinCurrPrice, selectedCoinPC24h;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chose_currency, container, false);


        coinArrayList = new ArrayList<String>();
        listView = view.findViewById(R.id.pickUpCoinListView);
        coinListEditText = view.findViewById(R.id.coinListEditText);
        currencyRVModelArrayList = new ArrayList<CurrencyRVModel>();
        loadingPBAlert = view.findViewById(R.id.idPBLoadingAlert);

        //Assign SharedPreferences
        selectedCoinPref = getContext().getSharedPreferences("selectedCoin", Context.MODE_PRIVATE);
        selectedCoinCurrPrice = getContext().getSharedPreferences("selectedCoinCurrPrice", Context.MODE_PRIVATE);
        selectedCoinPC24h = getContext().getSharedPreferences("selectedCoinPC24h", Context.MODE_PRIVATE);



        coinListEditText.addTextChangedListener(new TextWatcher() {
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

                selectedCoinPref.edit().putString("selectedCoin", adapter.getItem(i)).apply();
                String selectedSymbol = selectedCoinPref.getString("selectedCoin", null);

                for(CurrencyRVModel element : currencyRVModelArrayList){


                    if(element.getSymbol().equals(selectedSymbol)){

                        Log.i("CurrentPrice", element.getSymbol() + " Currenct Price is: "+ element.getPrice());
                        Log.i("Pct24", element.getSymbol() + " Pct is: "+ element.getPc1h());


                        selectedCoinCurrPrice.edit().putFloat("selectedCoinCurrPrice", (float) element.getPrice()).apply();
                        selectedCoinPC24h.edit().putFloat("selectedCoinPC24h", (float)element.getPc1h()).apply();

                        float currentPrice = selectedCoinCurrPrice.getFloat("selectedCoinCurrPrice", 0);
                        float pc24h = selectedCoinPC24h.getFloat("selectedCoinPC24h", 0);


                        Log.i("Current Price", element.getSymbol()+" Current Price is: "+ mainActivity.dynDF(currentPrice).format(currentPrice));
                        Log.i("24 PerChange", element.getSymbol()+" 24 Pct Cng is: "+ pc24h);

                    }
                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_alert_fragment_container,
                        new SelectAlertTypeFragment()).commit();

            }
        });

        getCurrencyDataNomics();




        return view;
    }



    private void getCurrencyDataNomics(){

        loadingPBAlert.setVisibility(View.VISIBLE);
        String url = "https://api.nomics.com/v1/currencies/ticker?key=ecae4f8ae82014deed75f16f14d03f2c21a819b1&interval=1h,1d&per-page=100&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

            loadingPBAlert.setVisibility(View.GONE);
            try {
                for(int i=0; i<response.length();i++){
                    JSONObject dataObj = response.getJSONObject(i);
                    String name = dataObj.getString("name");
                    String symbol = dataObj.getString("symbol");
                    double price = dataObj.getDouble("price");
                    double pc1h;

                    int size = dataObj.length();
                    //JSONObject oneDay = dataObj.getJSONObject("1d");
                    if(dataObj.has("1h")){
                        pc1h = dataObj.getJSONObject("1h").getDouble("price_change_pct") *100;
                    } else {
                        pc1h = 0;
                    }


                    currencyRVModelArrayList.add(new CurrencyRVModel(symbol, name, price, pc1h));
                    coinArrayList.add(symbol);

                    if(i==20){
                        Log.i("Array size: ", ""+coinArrayList.size());
                    }

                }

                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, coinArrayList);
                listView.setAdapter(adapter);

                //currencyRVAdapter.notifyDataSetChanged();

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getContext(),"Fail to extract json data..",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loadingPB.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Fail to get the data..", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);
    }
}