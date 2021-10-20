package com.example.cryptochime;

import static android.app.Activity.RESULT_OK;

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
import com.example.cryptochime.SetAlertActivity.AlertDB.MainDatabase;
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
                startActivityForResult(intent, 1);
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

    public void loadAlertList(){
        MainDatabase db = MainDatabase.getInstance(getActivity().getApplicationContext());
        List<Alert> alertList = db.alertDao().getAllAlerts();
        alertListAdapter.setAlertList(alertList);
        alertListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadAlertList();
        }
    }
    
    
}