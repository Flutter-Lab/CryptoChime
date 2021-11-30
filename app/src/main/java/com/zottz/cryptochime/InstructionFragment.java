package com.zottz.cryptochime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class InstructionFragment extends Fragment {
    ImageView imageView;
    Button buttonNext, buttonSkip;
    int counter;



       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instruction, container, false);

        imageView = view.findViewById(R.id.imageView);
        buttonNext = view.findViewById(R.id.buttonNext);
        buttonSkip = view.findViewById(R.id.buttonSkip);
        counter = 0;

        Fragment me = this;

        buttonNext.setOnClickListener(view1 -> {
            if (counter == 0){
                imageView.setImageResource(R.drawable.remove_fav_or_alert);
                counter++;
            } else {
                //getActivity().onBackPressed();
                getActivity().getSupportFragmentManager().beginTransaction().remove(me).commit();

            }
        });

        buttonSkip.setOnClickListener(view12 -> {
            //getActivity().onBackPressed();

            getActivity().getSupportFragmentManager().beginTransaction().remove(me).commit();

        });


        return view;
    }
}