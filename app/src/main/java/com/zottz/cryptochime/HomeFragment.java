package com.zottz.cryptochime;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zottz.cryptochime.FavoriteFragmentPkg.Favorite;
import com.zottz.cryptochime.SetAlertActivity.AlertDB.MainDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    RecyclerView currenciesRV;
    CurrencyRVAdapter currencyRVAdapter;
    public ArrayList<CurrencyRVModel> currencyRVModalArrayList;
    public static ArrayList<CurrencyRVModel> favRVModelArraylist;
    ProgressBar loadingPB;
    public ArrayList<String> symbolArrayList;

    MainDatabase db;
    List<Favorite> favoriteItemList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        db = MainDatabase.getInstance(getActivity().getApplicationContext());
        favoriteItemList = db.favoriteDao().getAllFavorites();

        symbolArrayList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        currenciesRV = view.findViewById(R.id.recyclerView);
        loadingPB = view.findViewById(R.id.idPBLoading);

        getRVData();

        // Fetch live data from CoinMarketCap and refresh periodically
        fetchLiveCryptoData();

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fetchLiveCryptoData();
                handler.postDelayed(this, 15000); // Refresh every 15 seconds
            }
        };
        handler.post(runnable);

        return view;
    }

    private void getRVData() {
        currenciesRV.setHasFixedSize(true);
        currencyRVModalArrayList = new ArrayList<>();
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, getContext());
        currenciesRV.setAdapter(currencyRVAdapter);
        currenciesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        favRVModelArraylist = new ArrayList<>();

        currencyRVAdapter.setOnItemClickListener(position -> {
            String adapterCurrencySymbol = currencyRVModalArrayList.get(position).getSymbol();
            String adapterCurrencyName = currencyRVModalArrayList.get(position).getName();
            String adapterCurrencyURL = currencyRVModalArrayList.get(position).getLogoURL();
            double adapterCurrencyPrice = currencyRVModalArrayList.get(position).getPrice();

            ArrayList<String> favCurrencyList = new ArrayList<>();
            Favorite favorite = new Favorite();
            List<Favorite> favoriteList = db.favoriteDao().getAllFavorites();

            for (Favorite item : favoriteList) {
                favCurrencyList.add(item.currencySymbol);
            }
            if (!favCurrencyList.contains(adapterCurrencySymbol)) {
                favorite.currencyName = adapterCurrencyName;
                favorite.currencySymbol = adapterCurrencySymbol;
                favorite.currencyIconURL = adapterCurrencyURL;
                favorite.currencyPrice = (float) adapterCurrencyPrice;

                db.favoriteDao().insertFavorite(favorite);
                Toast.makeText(getContext(), adapterCurrencySymbol + " added to Favorite List", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), adapterCurrencySymbol + " is already in Favorite List", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLiveCryptoData() {
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        String apiKey = "cf22e625-7f13-4277-857c-6c60021e50dd";  // Replace with your actual API key

        loadingPB.setVisibility(View.VISIBLE);

        // Create a new RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Create a JSON Object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("data");
                        LinkedHashMap<Object, Object> tempMap = new LinkedHashMap<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dataObj = jsonArray.getJSONObject(i);
                            String symbol = dataObj.getString("symbol");
                            String name = dataObj.getString("name");
                            JSONObject quote = dataObj.getJSONObject("quote");
                            JSONObject usdQuote = quote.getJSONObject("USD");
                            double price = usdQuote.getDouble("price");
                            double percentChange1h = usdQuote.optDouble("percent_change_1h", 0.0);
                            double percentChange24h = usdQuote.optDouble("percent_change_24h", 0.0);
                            double percentChange7d = usdQuote.optDouble("percent_change_7d", 0.0);

                            // Assuming there's a method to get the logo URL; adjust if necessary
                            String logoURL = "";  // Placeholder for logo URL, adjust if you have the logic to get it.

                            // Only add USD pairs
//                            if (symbol.endsWith("USD")) {
                                tempMap.put(symbol, new CurrencyRVModel(symbol, name, logoURL, price, percentChange1h, percentChange24h, percentChange7d, 0.0, 0.0));
//                            }
                        }

                        currencyRVModalArrayList.clear();
                        symbolArrayList.clear();

                        for (Map.Entry<Object, Object> entry : tempMap.entrySet()) {
                            currencyRVModalArrayList.add((CurrencyRVModel) entry.getValue());
                            symbolArrayList.add((String) entry.getKey());
                        }

                        currencyRVAdapter.notifyDataSetChanged();
                        loadingPB.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to parse CoinMarketCap data", Toast.LENGTH_SHORT).show();
                        loadingPB.setVisibility(View.GONE);
                    }
                }, error -> {
            Toast.makeText(getContext(), "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            loadingPB.setVisibility(View.GONE);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-CMC_PRO_API_KEY", apiKey);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
