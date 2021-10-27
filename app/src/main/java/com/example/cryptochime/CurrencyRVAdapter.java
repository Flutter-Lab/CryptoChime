package com.example.cryptochime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.MyViewHolder>{

    MainActivity mainActivity = new MainActivity();

    private OnItemClickListener mListener;


    //Mention data which I want to show in RV
    ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    Context context;
    //static DecimalFormat df2 = new DecimalFormat("#.##");

    //Generated Constructor for all Data
    public CurrencyRVAdapter(ArrayList<CurrencyRVModel> currencyRVModalArrayList, Context context) {
        this.currencyRVModelArrayList = currencyRVModalArrayList;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate sample layout
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout, parent, false);

        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Set all data into views // Which data we got from MyViewHolder
        CurrencyRVModel currencyRVModel = currencyRVModelArrayList.get(position);
        holder.symbolTextView.setText(currencyRVModel.getSymbol());
        holder.nameTextView.setText(currencyRVModel.getName());
        holder.priceTextView.setText("$"+mainActivity.dynDF(currencyRVModel.getPrice()).format(currencyRVModel.getPrice()));

        //Load Coin Logo Image to RecycleView
        String urlString = currencyRVModel.getLogoURL();
        Glide.with(context).load(urlString).into(holder.logoImage);


    }

    @Override
    public int getItemCount() {
        return currencyRVModelArrayList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        //Declare variable fro Views // Which we will see on sample_layout
        TextView symbolTextView, nameTextView, priceTextView;
        ImageView logoImage;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            // Find all views from given itemView / We got sample_layout as itemView from onCreateViewHolder using Inflater
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            logoImage = itemView.findViewById(R.id.logo_imageView);


            //Set Long Click Listener
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                    return true;
                }
            });

        }
    }

}
