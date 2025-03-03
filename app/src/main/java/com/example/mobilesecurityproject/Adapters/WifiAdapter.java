package com.example.mobilesecurityproject.Adapters;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilesecurityproject.Activities.MapActivity;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private final List<WifiNetwork> wifiList;

    public WifiAdapter(List<WifiNetwork> wifiList) {
        this.wifiList = wifiList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WifiNetwork wifiNetwork = wifiList.get(position);
        holder.ssidTextView.setText("SSID: " + wifiNetwork.getSsid());
        holder.bssidTextView.setText("BSSID: " + wifiNetwork.getBssid());
        holder.signalTextView.setText("Frequency: " + wifiNetwork.getFrequency());
        holder.securityTextView.setText("Security: " + wifiNetwork.getSecurity());

        Glide.with(holder.itemView.getContext())
                .asGif()
                .load(R.drawable.wifiglobaltransparent) // Replace with your actual GIF file in res/drawable
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.actionButton);

        // Handle click event to open MapActivity
        holder.actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MapActivity.class);
            // Pass multiple values
            intent.putExtra("BSSID", wifiNetwork.getBssid());
            intent.putExtra("SSID", wifiNetwork.getSsid());
            intent.putExtra("Frequency", wifiNetwork.getFrequency());
            intent.putExtra("Security", wifiNetwork.getSecurity());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ssidTextView, bssidTextView, signalTextView, securityTextView;
        ImageButton actionButton;

        ViewHolder(View itemView) {
            super(itemView);
            ssidTextView = itemView.findViewById(R.id.ssidTextView);
            bssidTextView = itemView.findViewById(R.id.bssidTextView);
            signalTextView = itemView.findViewById(R.id.signalTextView);
            securityTextView = itemView.findViewById(R.id.securityTextView);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }

}