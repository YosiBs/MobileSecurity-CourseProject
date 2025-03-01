package com.example.mobilesecurityproject.Network;

import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WifiApiService {

    @POST("/api/wifi")
    Call<WifiNetwork> createWifi(@Body WifiNetwork wifiNetwork);

    // ✅ Get all WiFi networks
    @GET("/api/wifi")
    Call<List<WifiNetwork>> getAllWifiNetworks();

    // ✅ Get a WiFi network by BSSID
    @GET("/api/wifi/{bssid}")
    Call<WifiNetwork> getWifiByBssid(@Path("bssid") String bssid);

    // ✅ Delete a WiFi network
    @DELETE("/api/wifi/{bssid}")
    Call<Void> deleteWifi(@Path("bssid") String bssid);

    // ✅ Get estimated WiFi location
    @GET("/api/wifi/{bssid}/estimate")
    Call<WifiNetwork> getEstimatedWifiLocation(@Path("bssid") String bssid);


    //-------------------------------------------------------WIFI SCAN-------------------------------------------------------------
    // ✅ Add a new WiFi scan
    @POST("/api/wifi-scans")
    Call<WifiScan> createWifiScan(@Body WifiScan wifiScan);

    // ✅ Get all WiFi scans
    @GET("/api/wifi-scans")
    Call<List<WifiScan>> getAllWifiScans();

    // ✅ Get scans for a specific BSSID
    @GET("/api/wifi-scans/{bssid}")
    Call<List<WifiScan>> getScansByBssid(@Path("bssid") String bssid);

    // ✅ Delete scans for a specific BSSID
    @DELETE("/api/wifi-scans/{bssid}")
    Call<Void> deleteScansByBssid(@Path("bssid") String bssid);
}
