package com.zottz.cryptochime.SetAlertActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.zottz.cryptochime.R;

import java.util.ArrayList;
import java.util.Objects;


public class SelectAlertTypeFragment extends Fragment {

    ArrayList<String> alertTypeArrayList;
    ListView alertTypeListView;

    SharedPreferences prefAlertTypeCode;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_alert_type, container, false);

        alertTypeListView = view.findViewById(R.id.alert_type_listView);

        if (getActivity() != null) {
            prefAlertTypeCode = getActivity().getSharedPreferences("alertTypeCode", Context.MODE_PRIVATE);
        }


        //        prefAlertTypeCode = this.getActivity().getSharedPreferences("alertTypeCode", Context.MODE_PRIVATE); //Old Code


        alertTypeArrayList = new ArrayList<>();


        alertTypeArrayList.add("Price rises above");
        alertTypeArrayList.add("Price drops to");
        alertTypeArrayList.add("1H change is over (%)");
        alertTypeArrayList.add("1H change is down (%)");

        //Initialize Array adapter for spinning list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, alertTypeArrayList);

        //Set Adapter to spinning list
        alertTypeListView.setAdapter(adapter);


        alertTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //alertTypeCode = i;
                prefAlertTypeCode.edit().putInt("alertTypeCode", i).apply();

//                int code = prefAlertTypeCode.getInt("alertTypeCode", 5);
//                Log.i("AlertCode: ", code+"");

                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.add_alert_fragment_container,
                        new SetValueFragment()).commit();
            }
        });












        return view;
    }
}