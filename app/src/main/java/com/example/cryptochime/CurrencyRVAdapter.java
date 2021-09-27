package com.example.cryptochime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.MyViewHolder>{

    //Mention data which I want to show in RV
    ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    Context context;
    static DecimalFormat df2 = new DecimalFormat("#.##");

    //Generated Constructor for all Data
    public CurrencyRVAdapter(ArrayList<CurrencyRVModel> currencyRVModalArrayList, Context context) {
        this.currencyRVModelArrayList = currencyRVModalArrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate sample layout
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Set all data into views // Which data we got from MyViewHolder
        CurrencyRVModel currencyRVModel = currencyRVModelArrayList.get(position);
        holder.symbolTextView.setText(currencyRVModel.getSymbol());
        holder.nameTextView.setText(currencyRVModel.getName());
        holder.priceTextView.setText("$"+df2.format(currencyRVModel.getPrice()));
    }

    @Override
    public int getItemCount() {
        return currencyRVModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        //Declare variable fro Views // Which we will see on sample_layout
        TextView symbolTextView, nameTextView, priceTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Find all views from given itemView / We got sample_layout as itemView from onCreateViewHolder using Inflater
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);

        }
    }
}
