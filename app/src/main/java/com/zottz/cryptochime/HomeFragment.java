package com.zottz.cryptochime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zottz.cryptochime.FavoriteFragmentPkg.Favorite;
import com.zottz.cryptochime.SetAlertActivity.AlertDB.MainDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
    private WebSocketClient webSocketClient;

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
        connectWebSocket();

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

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://stream.binance.com:9443/ws/!ticker@arr");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                getActivity().runOnUiThread(() -> loadingPB.setVisibility(View.VISIBLE));
            }

            @Override
            public void onMessage(String message) {
                getActivity().runOnUiThread(() -> {
                    loadingPB.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(message);
                        LinkedHashMap<Object, Object> tempMap = new LinkedHashMap<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject dataObj = jsonArray.getJSONObject(i);
                            String symbol = dataObj.getString("s");
                            double price = dataObj.getDouble("c");
                            String name = symbol;
                            String urlString = "";

                            if (symbol.endsWith("USDT")) {
                                tempMap.put(symbol, new CurrencyRVModel(symbol, name, urlString, price, 0.0, 0.0, 0.0, 0.0, 0.0));
                            }
                        }

                        currencyRVModalArrayList.clear();
                        symbolArrayList.clear();

                        for (Map.Entry<Object, Object> entry : tempMap.entrySet()) {
                            currencyRVModalArrayList.add((CurrencyRVModel) entry.getValue());
                            symbolArrayList.add((String) entry.getKey());
                        }

                        currencyRVAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Failed to parse WebSocket data", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                getActivity().runOnUiThread(() -> loadingPB.setVisibility(View.GONE));
            }

            @Override
            public void onError(Exception ex) {
                getActivity().runOnUiThread(() -> {
                    loadingPB.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "WebSocket connection failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        };
        webSocketClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
