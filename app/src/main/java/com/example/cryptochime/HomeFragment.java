package com.example.cryptochime;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment  {

    //Declare RecyclerView
    RecyclerView currenciesRV;
    //Declare Adapter
    CurrencyRVAdapter currencyRVAdapter;
    //ArrayList for Spinning list in AlertView
    //ArrayList<String> symbolArrayList;
    //Declare data which I want to show in RV
    public ArrayList<CurrencyRVModel> currencyRVModalArrayList;

    static DecimalFormat df2 = new DecimalFormat("#.##");
    ProgressBar loadingPB;

    //Declare ArrayList for Alert search
    ArrayList<String> symbolArrayList;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //Testing Purpose
        //myName = "Abir Hossain";

        symbolArrayList = new ArrayList<String>();

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        currenciesRV = view.findViewById(R.id.recyclerView);
        loadingPB = view.findViewById(R.id.idPBLoading);

        getRVData();

        getCurrencyData();









        return view;
    }



    private void getRVData(){
        //Find RecyclerView

        //currenciesRV = findViewById(R.id.recyclerView);
        currenciesRV.setHasFixedSize(true);

        //loadingPB = findViewById(R.id.idPBLoading);
        currencyRVModalArrayList = new ArrayList<>();

        //Send data to Adapter // After this create sample single view Layout
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, getContext());

        //Set the Adapter with RecyclerView
        currenciesRV.setAdapter(currencyRVAdapter);
        //Set Layout Manager to RecyclerView
        currenciesRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void getCurrencyData(){

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

                        currencyRVModalArrayList.add(new CurrencyRVModel(symbol, name, price, pc24h));
                        //Add name to coinNameList Array fro AlertPage;
                        symbolArrayList.add(symbol);

                    }

                    currencyRVAdapter.notifyDataSetChanged();




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


//        public void printText(){
//        Log.i("My Name Print: ", "Abir Hossain");
//        }
}
