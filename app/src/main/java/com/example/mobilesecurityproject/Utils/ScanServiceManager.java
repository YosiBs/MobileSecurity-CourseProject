package com.example.mobilesecurityproject.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.Network.RetrofitClient;
import com.example.mobilesecurityproject.Network.WifiApiService;
import com.example.mobilesecurityproject.Services.WifiScanService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanServiceManager {
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_WIFI_SCAN = "wifi_scan_enabled";


    private WifiManager wifiManager;
    private Context context;
    private WifiApiService apiService;
    private BroadcastReceiver wifiReceiver;



    public ScanServiceManager(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.apiService = RetrofitClient.getService();
    }

    public static boolean isScanEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(KEY_WIFI_SCAN, false);
        Log.d("ddd", "Checking if WiFi scan is enabled: " + enabled);
        return enabled;
    }

    public static void setScanEnabled(Context context, boolean enable) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_WIFI_SCAN, enable);
        editor.apply();
        Log.d("ddd", "Setting WiFi scan enabled to: " + enable);
        handleServiceState(context, enable);
    }

    public static void handleServiceState(Context context, boolean enable) {
        Intent serviceIntent = new Intent(context, WifiScanService.class);
        if (enable) {
            Log.d("ddd", "Starting WifiScanService...");
            context.startService(serviceIntent);
        } else {
            Log.d("ddd", "Stopping WifiScanService...");
            context.stopService(serviceIntent);
        }
    }
    
    
    
    
    //============================================================================================================
    // ✅ Start the WiFi Scan
    public void startWifiScan() {
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    Log.d("ddd", "WiFi scan successful!");
                    processWifiScan();
                } else {
                    Log.e("ddd", "WiFi Scan Failed!");
                }
            }
        };

        context.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        scanWifi();
    }

    // ✅ Initiate WiFi Scan
    public void scanWifi() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("ddd", "Location permission not granted! Skipping scan.");
            return;
        }

        boolean success = wifiManager.startScan();
        if (!success) {
            Log.e("ddd", "WiFi Scan failed!");
        }
    }

    // ✅ Process WiFi Scan Results
    @SuppressLint("MissingPermission")
    private void processWifiScan() {
        new LocationManager(context, (latitude, longitude) -> {
            Log.d("ddd", "Current location: " + latitude + ", " + longitude);
            List<ScanResult> wifiList = wifiManager.getScanResults();

            for (ScanResult scanResult : wifiList) {
                Log.d("ddd", "WiFi Found: " + scanResult.SSID);

                WifiNetwork wifiNetwork = new WifiNetwork(
                        scanResult.BSSID,
                        scanResult.SSID,
                        WifiNetwork.breakdownSecurity(scanResult),
                        scanResult.frequency,
                        "Unknown"
                );

                Log.d("ddd", "WifiNetwork ObjectBoundary: " + wifiNetwork);
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
        }).getLastKnownLocation();
    }

    // ✅ Send WiFi Scan to Server
    private void sendWifiScanToServer(WifiScan wifiScan) {
        apiService.createWifiScan(wifiScan).enqueue(new Callback<WifiScan>() {
            @Override
            public void onResponse(Call<WifiScan> call, Response<WifiScan> response) {
                if (response.isSuccessful()) {
                    Log.d("ddd", "WiFi Scan added successfully for: " + response.body().getBssid());
                } else {
                    Log.e("ddd", "Failed to add WiFi Scan: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<WifiScan> call, Throwable t) {
                Log.e("ddd", "API Error: " + t.getMessage());
            }
        });
    }

    // ✅ Send WiFi Network to Server
    private void sendWifiToServer(WifiNetwork wifiNetwork) {
        if (wifiNetwork.getSsid() == null || wifiNetwork.getSsid().isEmpty()) {
            Log.e("ddd", "Skipping WiFi Network: SSID is empty");
            return;
        }

        apiService.createWifi(wifiNetwork).enqueue(new Callback<WifiNetwork>() {
            @Override
            public void onResponse(Call<WifiNetwork> call, Response<WifiNetwork> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ddd", "WiFi Network added: " + response.body().getBssid());
                } else {
                    Log.e("ddd", "Failed to Add WiFi Network. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WifiNetwork> call, Throwable t) {
                Log.e("ddd", "API Error: " + t.getMessage());
            }
        });
    }

    // ✅ Unregister Receiver to prevent leaks
    public void stopWifiScan() {
        if (wifiReceiver != null) {
            context.unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
    }
}
