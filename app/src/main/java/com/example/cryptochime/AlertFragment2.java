package com.example.cryptochime;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cryptochime.SetAlertActivity.AddAlertActivity;


public class AlertFragment2 extends Fragment {

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





        return view;


    }
    
    
}