package com.example.cryptochime;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptochime.SetAlertActivity.AddAlertActivity;
import com.example.cryptochime.SetAlertActivity.AlertDB.Alert;
import com.example.cryptochime.SetAlertActivity.AlertDB.AlertDatabase;
import com.example.cryptochime.SetAlertActivity.AlertListAdapter;

import java.util.List;


public class AlertFragment2 extends Fragment {

    private AlertListAdapter alertListAdapter;

    Button addAlert;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert2, container, false);

        addAlert = view.findViewById(R.id.addAlertButton);
        
        addAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddAlertActivity.class);
                startActivity(intent);
            }
        });



        initRecyclerView(view);
        loadAlertList();


        return view;


    }

    private void initRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.alertRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);


        alertListAdapter = new AlertListAdapter(getActivity());
        recyclerView.setAdapter(alertListAdapter);


    }

    private void loadAlertList(){
        AlertDatabase db = AlertDatabase.getInstance(getActivity().getApplicationContext());
        List<Alert> alertList = db.alertDao().getAllAlerts();
        alertListAdapter.setAlertList(alertList);
        alertListAdapter.notifyDataSetChanged();
    }
    
    
}