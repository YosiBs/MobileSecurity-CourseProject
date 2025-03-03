package com.example.mobilesecurityproject.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilesecurityproject.Adapters.WifiAdapter;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.Network.RetrofitClient;
import com.example.mobilesecurityproject.Network.WifiApiService;
import com.example.mobilesecurityproject.R;
import com.example.mobilesecurityproject.Utils.LocationManager;
import com.example.mobilesecurityproject.Utils.ScanServiceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WifiScannerActivity extends AppCompatActivity {

    private ScanServiceManager scanServiceManager;
    private RecyclerView recyclerView;
    private WifiAdapter wifiAdapter;
    private WifiApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wifi_scanner);

        initViews();


//        scanServiceManager = new ScanServiceManager(this);
//        scanServiceManager.startWifiScan();


        fetchAllWifiNetworks(); // Fetch WiFi networks from the server

    }

    private void initViews(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getService();
    }

    private void fetchAllWifiNetworks() {
        apiService.getAllWifiNetworks().enqueue(new Callback<List<WifiNetwork>>() {
            @Override
            public void onResponse(Call<List<WifiNetwork>> call, Response<List<WifiNetwork>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<WifiNetwork> wifiNetworkList = response.body();



                    wifiAdapter = new WifiAdapter(wifiNetworkList);
                    recyclerView.setAdapter(wifiAdapter);
                } else {
                    // Handle API failure
                    System.out.println("Failed to fetch WiFi networks: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<WifiNetwork>> call, Throwable t) {
                System.err.println("API Error: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanServiceManager != null) {
            scanServiceManager.stopWifiScan();
        } else {
            Log.e("ddd", "scanServiceManager is null in onDestroy()");
        }
    }


}