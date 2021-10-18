package com.example.cryptochime.SetAlertActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptochime.R;
import com.example.cryptochime.SetAlertActivity.AlertDB.Alert;

import java.util.List;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.MyViewHolder> {

    private Context context;
    private List<Alert> alertList;
    public AlertListAdapter(Context context){
        this.context = context;
    }
    public void setAlertList(List<Alert> alertList){
        this.alertList = alertList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public AlertListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.alert_rv_sample, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertListAdapter.MyViewHolder holder, int position) {

        holder.symbolTV.setText(this.alertList.get(position).currencyName);
        holder.alertTypeNameTV.setText(this.alertList.get(position).alertType + " " + this.alertList.get(position).alertValue);
        if (this.alertList.get(position).alertTypeCode %2 == 0){
            holder.alertIndicator.setImageResource(R.drawable.ic_alert_up);
        } else {
            holder.alertIndicator.setImageResource(R.drawable.ic_alert_down);
        }




    }

    @Override
    public int getItemCount() {
        return this.alertList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView symbolTV;
        TextView alertTypeNameTV;
        ImageView alertIndicator;

        public MyViewHolder (View view){
            super(view);
            symbolTV = view.findViewById(R.id.symbolTextView);
            alertTypeNameTV = view.findViewById(R.id.alertTypeNameTV);
            alertIndicator = view.findViewById(R.id.alertIndicator);
        }
    }
}