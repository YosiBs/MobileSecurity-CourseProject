package com.example.mobilesecurityproject;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {
    private final List<ScanResult> wifiList;

    public WifiAdapter(List<ScanResult> wifiList) {
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
        ScanResult scanResult = wifiList.get(position);
        holder.ssidTextView.setText("SSID: " + scanResult.SSID);
        holder.bssidTextView.setText("BSSID: " + scanResult.BSSID);
        holder.signalTextView.setText("Signal Strength: " + scanResult.level + " dBm");
        holder.securityTextView.setText("Security: " + getSecurity(scanResult));
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ssidTextView, bssidTextView, signalTextView, securityTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ssidTextView = itemView.findViewById(R.id.ssidTextView);
            bssidTextView = itemView.findViewById(R.id.bssidTextView);
            signalTextView = itemView.findViewById(R.id.signalTextView);
            securityTextView = itemView.findViewById(R.id.securityTextView);
        }
    }

    private String getSecurity(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA3")) return "WPA3";
        if (capabilities.contains("WPA2")) return "WPA2";
        if (capabilities.contains("WPA")) return "WPA";
        if (capabilities.contains("WEP")) return "WEP";
        return "Open";
    }
}