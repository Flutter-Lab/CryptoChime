package com.example.cryptochime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptochime.FavoriteFragmentPkg.Favorite;
import com.example.cryptochime.SetAlertActivity.AlertDB.MainDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    RecyclerView favoriteRV;
    CurrencyRVAdapter favoriteRVAdapter;

    public ArrayList<CurrencyRVModel> favArrayList;

    MainDatabase db;
    List<Favorite> favoriteItemList;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        favArrayList = new ArrayList<>();

        db = MainDatabase.getInstance(getActivity().getApplicationContext());
        favoriteItemList = db.favoriteDao().getAllFavorites();

        favoriteRV = view.findViewById(R.id.favRecyclerView);

        //Ready Favorite currency model arraylist
        for (int i = 0; i < favoriteItemList.size(); i++){
            String symbol = favoriteItemList.get(i).currencySymbol;
            String name = favoriteItemList.get(i).currencyName;
            String urlString = favoriteItemList.get(i).currencyIconURL;
            float price = favoriteItemList.get(i).currencyPrice;

            favArrayList.add(new CurrencyRVModel(symbol, name, urlString, price));

        }

        //Set RV Data
        favoriteRV.setHasFixedSize(true);
        //homeFragment.favRVModelArraylist = new ArrayList<CurrencyRVModel>();
        favoriteRVAdapter = new CurrencyRVAdapter(favArrayList, getContext());
        favoriteRV.setAdapter(favoriteRVAdapter);
        favoriteRV.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteRVAdapter.notifyDataSetChanged();



        //Set On Click >> Delete
        favoriteRVAdapter.setOnItemClickListener(position -> {

            String symbol = favArrayList.get(position).getSymbol();
            db.favoriteDao().deleteBySymbol(symbol);
            favArrayList.remove(position);
            favoriteRVAdapter.notifyDataSetChanged();

            Toast.makeText(getContext(), symbol+" is Deleted From Favorite", Toast.LENGTH_SHORT).show();

        });




        return view;
    }
}
