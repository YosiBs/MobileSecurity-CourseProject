package com.example.mobilesecurityproject;

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
import com.example.mobilesecurityproject.Utils.LocationManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WifiScannerActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private WifiManager wifiManager;
    private RecyclerView recyclerView;
    private WifiAdapter wifiAdapter;
    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                Log.d("WiFiScanner", "WiFi scan successful!");
                processWifiScan();
            } else {
                Log.e("WiFiScanner", "WiFi Scan Failed!");
                Toast.makeText(context, "WiFi Scan Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wifi_scanner);


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        checkPermissionsAndScan();


    }

    private void checkPermissionsAndScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            scanWifi();
        }
    }

    private void scanWifi() {
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean success = wifiManager.startScan();
        if (!success) {
            Log.e("WiFiScanner", "WiFi Scan failed!");
            Toast.makeText(this, "WiFi Scan failed. Try again!", Toast.LENGTH_SHORT).show();
        }
    }


    private void processWifiScan() {
        new LocationManager(this, new LocationManager.LocationListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                Log.d("WiFiScanner", "Current location: " + latitude + ", " + longitude);
                @SuppressLint("MissingPermission") List<ScanResult> wifiList = wifiManager.getScanResults();

                for (ScanResult scanResult : wifiList) {
                    Log.d("WiFiScanner", "WiFi Found: " + scanResult.SSID);

                    WifiNetwork wifiNetwork = new WifiNetwork(
                            scanResult.BSSID,
                            scanResult.SSID,
                            WifiNetwork.breakdownSecurity(scanResult),
                            scanResult.frequency,
                            "Unknown"
                    );
                    Log.d("API", "WifiNetwork ObjectBoundary: " + wifiNetwork);

                    sendWifiToServer(wifiNetwork);

                    WifiScan wifiScan = new WifiScan(
                            scanResult.BSSID,
                            scanResult.level,
                            "device123",
                            latitude,
                            longitude
                    );
                    sendWifiScanToServer(wifiScan);
                }
                wifiAdapter = new WifiAdapter(wifiList);
                recyclerView.setAdapter(wifiAdapter);
            }
        }).getLastKnownLocation();
    }





    private void sendWifiScanToServer(WifiScan wifiScan) {

        WifiApiService apiService = RetrofitClient.getService();
        Call<WifiScan> call = apiService.createWifiScan(wifiScan);

        call.enqueue(new Callback<WifiScan>() {
            @Override
            public void onResponse(Call<WifiScan> call, Response<WifiScan> response) {
                if (response.isSuccessful()) {
                    Log.d("API", "WiFi Scan added successfully for: " + response.body().getBssid());
                } else {
                    Log.e("API", "Failed to add WiFi Scan: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<WifiScan> call, Throwable t) {
                Log.e("API", "API Error: " + t.getMessage());
            }
        });
    }
    private void sendWifiToServer(WifiNetwork wifiNetwork) {
        if (wifiNetwork.getSsid() == null || wifiNetwork.getSsid().isEmpty()) {
            Log.e("API", "Skipping WiFi Network: SSID is empty");
            return;
        }

        WifiApiService apiService = RetrofitClient.getService();
        Call<WifiNetwork> call = apiService.createWifi(wifiNetwork);

        call.enqueue(new Callback<WifiNetwork>() {
            @Override
            public void onResponse(Call<WifiNetwork> call, Response<WifiNetwork> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "WiFi Network added: " + response.body().getBssid());
                } else {
                    Log.e("API", "Failed to Add WiFi Network. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WifiNetwork> call, Throwable t) {
                Log.e("API", "API Error: " + t.getMessage());
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scanWifi();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}