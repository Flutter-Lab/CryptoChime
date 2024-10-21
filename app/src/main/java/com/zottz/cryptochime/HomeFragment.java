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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class HomeFragment extends Fragment {

    // Declare RecyclerView
    RecyclerView currenciesRV;
    // Declare Adapter
    CurrencyRVAdapter currencyRVAdapter;
    // Declare data to show in RecyclerView
    public ArrayList<CurrencyRVModel> currencyRVModalArrayList;
    public static ArrayList<CurrencyRVModel> favRVModelArraylist;
    ProgressBar loadingPB;
    // Declare ArrayList for symbol search
    public ArrayList<String> symbolArrayList;

    MainDatabase db;
    List<Favorite> favoriteItemList;
    private WebSocket webSocket;
    private OkHttpClient client;

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
        // Set up Adapter
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, getContext());
        currenciesRV.setAdapter(currencyRVAdapter);
        currenciesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        favRVModelArraylist = new ArrayList<>();

        // Set OnClick RV Item
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
        client = new OkHttpClient();
        Request request = new Request.Builder().url("wss://stream.binance.com:9443/ws/!ticker@arr").build();
        webSocket = client.newWebSocket(request, new BinanceWebSocketListener());
        client.dispatcher().executorService().shutdown();
    }

    private final class BinanceWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
            getActivity().runOnUiThread(() -> loadingPB.setVisibility(View.VISIBLE));
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            getActivity().runOnUiThread(() -> {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = new JSONArray(text);
                    // Use a temporary map to ensure stable order by symbol
                    LinkedHashMap<Object, Object> tempMap = new LinkedHashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject dataObj = jsonArray.getJSONObject(i);
                        String symbol = dataObj.getString("s");
                        double price = dataObj.getDouble("c");
                        String name = symbol; // Binance does not provide the full name
                        String urlString = ""; // Binance stream does not provide an image URL

                        // Filter to include only USDT pairs
                        if (symbol.endsWith("USDT")) {
                            tempMap.put(symbol, new CurrencyRVModel(symbol, name, urlString, price, 0.0, 0.0, 0.0, 0.0, 0.0));
                        }
                    }

                    // Clear old data and maintain a stable serial order
                    currencyRVModalArrayList.clear();
                    symbolArrayList.clear();

                    // Add all items from the map in stable order
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
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            onMessage(webSocket, bytes.utf8());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            getActivity().runOnUiThread(() -> loadingPB.setVisibility(View.GONE));
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
            getActivity().runOnUiThread(() -> {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(getContext(), "WebSocket connection failed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, null);
        }
    }
}
