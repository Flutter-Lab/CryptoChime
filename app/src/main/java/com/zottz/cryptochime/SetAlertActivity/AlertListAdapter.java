package com.zottz.cryptochime.SetAlertActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zottz.cryptochime.MainActivity;
import com.zottz.cryptochime.R;
import com.zottz.cryptochime.SetAlertActivity.AlertDB.Alert;

import java.util.List;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.MyViewHolder> {

    private OnItemClickListener mListener;
    private final Context context;
    private List<Alert> alertList;

    public AlertListAdapter(Context context) {
        this.context = context;
    }

    public void setAlertList(List<Alert> alertList) {
        this.alertList = alertList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public Alert getAlertAt(int position) {
        return alertList.get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_rv_sample, parent, false);
        return new MyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Alert alert = alertList.get(position);
        holder.symbolTV.setText(alert.currencySymbol);
        double alertValueDouble = alert.alertValue;

        if (alert.alertTypeCode < 2) {
            holder.alertTypeNameTV.setText(alert.alertType + " $" + MainActivity.dynDF(alertValueDouble).format(alertValueDouble));
        } else {
            holder.alertTypeNameTV.setText(alert.alertType + " " + MainActivity.df2.format(alertValueDouble) + "%");
        }

        holder.alertIndicator.setImageResource(alert.alertTypeCode % 2 == 0 ? R.drawable.ic_alert_up : R.drawable.ic_alert_down);

        if (alert.isLoudAlert) {
            holder.loudAlertText.setText("Loud Alert");
        } else {
            holder.loudAlertText.setText(""); // Clear if not a loud alert
        }
    }

    @Override
    public int getItemCount() {
        return alertList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTV;
        TextView alertTypeNameTV;
        ImageView alertIndicator;
        TextView loudAlertText;

        public MyViewHolder(View view, OnItemClickListener listener) {
            super(view);
            symbolTV = view.findViewById(R.id.symbolTextView);
            alertTypeNameTV = view.findViewById(R.id.alertTypeNameTV);
            alertIndicator = view.findViewById(R.id.alertIndicator);
            loudAlertText = view.findViewById(R.id.loudAlertText);

            // Item click listener
            view.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
