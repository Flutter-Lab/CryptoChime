package com.zottz.cryptochime.SettingsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zottz.cryptochime.R;

import java.util.ArrayList;

public class SettingsRVAdapter extends RecyclerView.Adapter<SettingsRVAdapter.ViewHolder>{

    private OnItemClickListener sListener;

    private ArrayList<String> settingsList;
    private Context context;

    public SettingsRVAdapter(ArrayList<String> settingsList, Context context) {
        this.settingsList = settingsList;
        this.context = context;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        sListener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.settings_item, parent, false);

        return new ViewHolder(view, sListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String settingName = settingsList.get(position);

        holder.optionName.setText(settingName);

    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView optionName;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            optionName = itemView.findViewById(R.id.optionNameId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
