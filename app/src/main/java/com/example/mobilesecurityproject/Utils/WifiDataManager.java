package com.example.mobilesecurityproject.Utils;

import android.util.Log;
import com.example.mobilesecurityproject.Models.WifiEstimate;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.Network.WifiApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class WifiDataManager {

    private final WifiApiService apiService;

    public interface WifiDataCallback {
        void onWifiFetched(List<WifiNetwork> wifiNetworks);
        void onWifiEstimateFetched(WifiEstimate estimate);
        void onWifiScansFetched(List<WifiScan> scans);
        void onError(String message);
    }

    public WifiDataManager(WifiApiService apiService) {
        this.apiService = apiService;
    }

    // Fetch All WiFi Networks
    public void fetchAllWifiNetworks(WifiDataCallback callback) {
        apiService.getAllWifiNetworks().enqueue(new Callback<List<WifiNetwork>>() {
            @Override
            public void onResponse(Call<List<WifiNetwork>> call, Response<List<WifiNetwork>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onWifiFetched(response.body());
                } else {
                    callback.onError("Failed to load WiFi networks");
                }
            }

            @Override
            public void onFailure(Call<List<WifiNetwork>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Fetch Estimated Location for a Specific WiFi
    public void fetchEstimatedLocationForWifi(WifiNetwork wifi, WifiDataCallback callback) {
        apiService.getEstimatedWifiLocation(wifi.getBssid()).enqueue(new Callback<WifiEstimate>() {
            @Override
            public void onResponse(Call<WifiEstimate> call, Response<WifiEstimate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WifiEstimate estimate = response.body();
                    estimate.setSsid(wifi.getSsid());
                    callback.onWifiEstimateFetched(estimate);
                } else {
                    callback.onError("Failed to get estimated location");
                }
            }

            @Override
            public void onFailure(Call<WifiEstimate> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // Fetch WiFi Scans for a Specific BSSID
    public void fetchWifiScansForBssid(String bssid, WifiDataCallback callback) {
        apiService.getScansByBssid(bssid).enqueue(new Callback<List<WifiScan>>() {
            @Override
            public void onResponse(Call<List<WifiScan>> call, Response<List<WifiScan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onWifiScansFetched(response.body());
                } else {
                    callback.onError("Failed to fetch scans");
                }
            }

            @Override
            public void onFailure(Call<List<WifiScan>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
