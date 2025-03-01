package com.example.mobilesecurityproject.Models;

import com.google.gson.annotations.SerializedName;

public class WifiScan {
    @SerializedName("bssid")
    private String bssid;

    @SerializedName("signal_strength")
    private int signalStrength;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("location_lat")
    private double locationLat;

    @SerializedName("location_lon")
    private double locationLon;

    public WifiScan(String bssid, int signalStrength, String deviceId, double locationLat, double locationLon) {
        this.bssid = bssid;
        this.signalStrength = signalStrength;
        this.deviceId = deviceId;
        this.locationLat = locationLat;
        this.locationLon = locationLon;
    }

    public String getBssid() {
        return bssid;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public double getLocationLat() {
        return locationLat;
    }

    public double getLocationLon() {
        return locationLon;
    }
}
