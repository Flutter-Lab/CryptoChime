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

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    HomeFragment homeFragment = new HomeFragment();

    RecyclerView favoriteRV;
    CurrencyRVAdapter favoriteRVAdapter;

    public ArrayList<CurrencyRVModel> favArrayList;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoriteRV = view.findViewById(R.id.favRecyclerView);
        //favArrayList = new ArrayList<>();
        favArrayList = HomeFragment.favRVModelArraylist;

        //Set RV Data
        favoriteRV.setHasFixedSize(true);
        //homeFragment.favRVModelArraylist = new ArrayList<CurrencyRVModel>();
        favoriteRVAdapter = new CurrencyRVAdapter(favArrayList, getContext());
        favoriteRV.setAdapter(favoriteRVAdapter);
        favoriteRV.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteRVAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "favArray Size: "+ favArrayList.size(), Toast.LENGTH_SHORT).show();







        return view;
    }
}
