package com.example.mobilesecurityproject.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.Network.RetrofitClient;
import com.example.mobilesecurityproject.Network.WifiApiService;
import com.example.mobilesecurityproject.Utils.LocationManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WifiScanService extends Service {
    private static final String TAG = "WifiScanService";
    private static final int INTERVAL = 10000; // 10 seconds
    private static final String CHANNEL_ID = "WifiScanServiceChannel";
    private WifiManager wifiManager;
    private Handler handler;


    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        handler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, createNotification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(scanWifiTask);
        return START_STICKY;
    }

    private final Runnable scanWifiTask = new Runnable() {
        @Override
        public void run() {
            scanWifiNetworks();
            handler.postDelayed(this, INTERVAL);
        }
    };

    private void scanWifiNetworks() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("ddd", "Location permission not granted!");
            return;
        }

        boolean success = wifiManager.startScan();
        if (!success) {
            Log.e("ddd", "WiFi Scan failed!");
            return;
        }

        new LocationManager(getApplicationContext(), new LocationManager.LocationListener() {
            @Override
            public void onLocationReceived(double latitude, double longitude) {
                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult scanResult : wifiList) {
                    sendWifiToServer(new WifiNetwork(
                            scanResult.BSSID,
                            scanResult.SSID,
                            WifiNetwork.breakdownSecurity(scanResult),
                            scanResult.frequency,
                            "Unknown"
                    ));
                    LatLng location = new LatLng(latitude, longitude);
                    sendWifiScanToServer(new WifiScan(
                            scanResult.BSSID,
                            scanResult.level,
                            "device123",
                            location.latitude,
                            location.longitude
                    ));
                }
            }
        }).getLastKnownLocation();
    }

    private void sendWifiToServer(WifiNetwork wifiNetwork) {
        WifiApiService apiService = RetrofitClient.getService();
        Call<WifiNetwork> call = apiService.createWifi(wifiNetwork);

        call.enqueue(new Callback<WifiNetwork>() {
            @Override
            public void onResponse(Call<WifiNetwork> call, Response<WifiNetwork> response) {
                if (response.isSuccessful()) {
                    Log.d("ddd", "WiFi Network added: " + wifiNetwork.getBssid());
                }
            }

            @Override
            public void onFailure(Call<WifiNetwork> call, Throwable t) {
                Log.e("ddd", "API Error: " + t.getMessage());
            }
        });
    }

    private void sendWifiScanToServer(WifiScan wifiScan) {
        WifiApiService apiService = RetrofitClient.getService();
        Call<WifiScan> call = apiService.createWifiScan(wifiScan);

        call.enqueue(new Callback<WifiScan>() {
            @Override
            public void onResponse(Call<WifiScan> call, Response<WifiScan> response) {
                if (response.isSuccessful()) {
                    Log.d("ddd", "WiFi Scan added for: " + wifiScan.getBssid());
                }
            }

            @Override
            public void onFailure(Call<WifiScan> call, Throwable t) {
                Log.e("ddd", "API Error: " + t.getMessage());
            }
        });
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "WiFi Scanning Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("WiFi Scanning Running")
                .setContentText("Scanning for WiFi networks every 10 seconds...")
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(scanWifiTask);
        Log.d("ddd", "WiFiScanService Stopped.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
