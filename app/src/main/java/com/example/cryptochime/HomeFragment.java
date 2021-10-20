package com.example.cryptochime;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.cryptochime.FavoriteFragmentPkg.Favorite;
import com.example.cryptochime.SetAlertActivity.AlertDB.MainDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment  {

    //Declare RecyclerView
    RecyclerView currenciesRV;
    //Declare Adapter
    CurrencyRVAdapter currencyRVAdapter;
    //Declare data which I want to show in RV
    public ArrayList<CurrencyRVModel> currencyRVModalArrayList;

    public static ArrayList<CurrencyRVModel> favRVModelArraylist;
    ProgressBar loadingPB;
    //Declare ArrayList for Alert search
    public ArrayList<String> symbolArrayList;

    MainDatabase db;
    List<Favorite> favoriteItemList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

         db = MainDatabase.getInstance(getActivity().getApplicationContext());
        favoriteItemList = db.favoriteDao().getAllFavorites();

        //PickUp Coin list for AlertPage2
        symbolArrayList = new ArrayList<String>();
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        currenciesRV = view.findViewById(R.id.recyclerView);
        loadingPB = view.findViewById(R.id.idPBLoading);

        getRVData();
        getCurrencyDataNomics();


        return view;
    }

    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void getRVData(){
        currenciesRV.setHasFixedSize(true);
        //loadingPB = findViewById(R.id.idPBLoading);
        currencyRVModalArrayList = new ArrayList<>();
        //Send data to Adapter // After this create sample single view Layout
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList, getContext());
        //Set the Adapter with RecyclerView
        currenciesRV.setAdapter(currencyRVAdapter);
        //Set Layout Manager to RecyclerView
        currenciesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        //FavRVModel Array List
        favRVModelArraylist = new ArrayList<>();


        //Set OnClick RV Item
        currencyRVAdapter.setOnItemClickListener(new CurrencyRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String favCurrencyName = currencyRVModalArrayList.get(position).getSymbol();
                ArrayList<String> favCurrencyList = new ArrayList<>();

                //Save coin Name to Favorite DB Table
                MainDatabase db = MainDatabase.getInstance(getActivity().getApplicationContext());
                Favorite favorite = new Favorite();
                List<Favorite> favoriteList = db.favoriteDao().getAllFavorites();

                for (int i = 0; i< favoriteList.size(); i++){
                    favCurrencyList.add(favoriteList.get(i).currencyName);
                    }
                if (!favCurrencyList.contains(favCurrencyName)){
                    favorite.currencyName = favCurrencyName;
                    db.favoriteDao().insertFavorite(favorite);
                    Toast.makeText(getContext(), favCurrencyName+" is added to Favorite List", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), favCurrencyName+" is already in Favorite List", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void getCurrencyDataNomics(){

        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.nomics.com/v1/currencies/ticker?key=ecae4f8ae82014deed75f16f14d03f2c21a819b1&per-page=100&page=1";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {

            loadingPB.setVisibility(View.GONE);
            try {
                for(int i=0; i<response.length();i++){
                    JSONObject dataObj = response.getJSONObject(i);
                    String name = dataObj.getString("name");
                    String symbol = dataObj.getString("symbol");
                    double price = dataObj.getDouble("price");
                    String urlString = dataObj.getString("logo_url");

                    currencyRVModalArrayList.add(new CurrencyRVModel(symbol,name, urlString, price));
                    symbolArrayList.add(symbol);


                    //Update currency information in Favorite Fragment


                    for (int j = 0; j < favoriteItemList.size(); j++){
                        if (symbol.equals(favoriteItemList.get(j).currencyName)){
                            favRVModelArraylist.add(new CurrencyRVModel(symbol, name, urlString, price));
                        }
                    }
                }


                currencyRVAdapter.notifyDataSetChanged();

            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(getContext(),"Fail to extract json data..",Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            loadingPB.setVisibility(View.GONE);
            Toast.makeText(getContext(),"Fail to get the data..", Toast.LENGTH_SHORT).show();
        });

        requestQueue.add(jsonArrayRequest);
    }
}
