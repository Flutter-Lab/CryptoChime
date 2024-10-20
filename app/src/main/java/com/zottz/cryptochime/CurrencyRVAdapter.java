package com.zottz.cryptochime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.MyViewHolder> {

    private final ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    private final Context context;
    private OnItemClickListener mListener;

    public CurrencyRVAdapter(ArrayList<CurrencyRVModel> currencyRVModelArrayList, Context context) {
        this.currencyRVModelArrayList = currencyRVModelArrayList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_layout, parent, false);
        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CurrencyRVModel currencyRVModel = currencyRVModelArrayList.get(position);
        holder.symbolTextView.setText(currencyRVModel.getSymbol());
        holder.nameTextView.setText(currencyRVModel.getName());
        holder.priceTextView.setText(String.format("$%s", MainActivity.dynDF(currencyRVModel.getPrice()).format(currencyRVModel.getPrice())));

        holder.pc1hTextView.setText(String.format("%s%%", MainActivity.df2.format(currencyRVModel.getPc1h())));
        setTVcolor(holder.pc1hTextView, currencyRVModel.getPc1h());

        holder.pc1dTextView.setText(String.format("%s%%", MainActivity.df2.format(currencyRVModel.getPc24h())));
        setTVcolor(holder.pc1dTextView, currencyRVModel.getPc24h());

        holder.pc7dTextView.setText(String.format("%s%%", MainActivity.df2.format(currencyRVModel.getPc7d())));
        setTVcolor(holder.pc7dTextView, currencyRVModel.getPc7d());

        holder.capTextView.setText(MainActivity.bigValueCurrency(currencyRVModel.getCap()));
        holder.volumeTextView.setText(MainActivity.bigValueCurrency(currencyRVModel.getVol()));

        // Load Coin Logo Image to RecyclerView
        Glide.with(context).load(currencyRVModel.getLogoURL()).into(holder.logoImage);
    }

    @Override
    public int getItemCount() {
        return currencyRVModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView, nameTextView, priceTextView, pc1hTextView, pc1dTextView, pc7dTextView, capTextView, volumeTextView;
        ImageView logoImage;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            logoImage = itemView.findViewById(R.id.logo_imageView);
            pc1hTextView = itemView.findViewById(R.id.pc1HTextView);
            pc1dTextView = itemView.findViewById(R.id.pc1DTextView);
            pc7dTextView = itemView.findViewById(R.id.pc7dTextView);
            capTextView = itemView.findViewById(R.id.capTextView);
            volumeTextView = itemView.findViewById(R.id.volumeTextView);

            // Item click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    private void setTVcolor(TextView textView, double value) {
        if (value < 0) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
    }
}
