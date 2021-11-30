package com.zottz.cryptochime.SettingsActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zottz.cryptochime.R;

import java.util.ArrayList;


public class SettingsFragment extends Fragment {

    private RecyclerView recyclerViewSetting;
    private SettingsRVAdapter settingsRVAdapter;
    private ArrayList<String> settingsList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        recyclerViewSetting = view.findViewById(R.id.settingsRV);
        recyclerViewSetting.setHasFixedSize(true);
        recyclerViewSetting.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsList = new ArrayList<>();


        settingsList.add("Set Alert Sound");
        settingsList.add("How to use?");
        //Set up adapter
        settingsRVAdapter = new SettingsRVAdapter(settingsList, getContext());
        recyclerViewSetting.setAdapter(settingsRVAdapter);



        //Set OnClick RV Item
        settingsRVAdapter.setOnItemClickListener(new SettingsRVAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Fragment selectedFragment = null;

                if (position == 0){
                    selectedFragment = new SelectSoundFragment();
                } else if (position == 1){
                    selectedFragment = new HowToUseFragment();
                }

                getFragmentManager().beginTransaction().replace(R.id.settingsContainer_atvt, selectedFragment).addToBackStack(null).commit();

                Log.i("TAG", "onItemClick: Setting "+position);
            }
        });






        return view;

    }
}