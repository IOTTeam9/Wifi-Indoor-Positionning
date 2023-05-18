package com.example.wifimanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WifiRVAdapter extends RecyclerView.Adapter<WifiRVAdapter.MyViewHolder> {

    private ArrayList<String[]> dataset;

    public WifiRVAdapter(ArrayList<String[]> arrayList) {
        dataset = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.wifi_rv, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.wifinametv.setText(dataset.get(position)[4]);
        holder.ssidtv.setText(dataset.get(position)[0]);
        holder.bssidtv.setText(dataset.get(position)[1]);
        holder.rssi.setText(dataset.get(position)[2]);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView wifinametv, ssidtv, bssidtv, rssi;

        public MyViewHolder(View v) {
            super(v);
            wifinametv = v.findViewById(R.id.placerv_tv);
            ssidtv = v.findViewById(R.id.ssidrv_tv);
            bssidtv = v.findViewById(R.id.bssidrv_tv);
            rssi = v.findViewById(R.id.rssi_tv);
        }
    }

}
