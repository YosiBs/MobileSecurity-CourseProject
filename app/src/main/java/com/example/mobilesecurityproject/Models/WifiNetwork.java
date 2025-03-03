package com.example.mobilesecurityproject.Models;

import android.net.wifi.ScanResult;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class WifiNetwork  {
    @SerializedName("bssid") // ✅ Must match API field
    private String bssid;

    @SerializedName("ssid")
    private String ssid;

    @SerializedName("security")
    private String security;

    @SerializedName("frequency")
    private int frequency;

    @SerializedName("standard")
    private String standard;

    public WifiNetwork(String bssid, String ssid, String security, int frequency, String standard) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.security = security;
        this.frequency = frequency;
        this.standard = standard;
    }

    public String getBssid() {
        return bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public String getSecurity() {
        return security;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getStandard() {
        return standard;
    }

    public static String breakdownSecurity(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;
        if (capabilities.contains("WPA3")) return "WPA3";
        if (capabilities.contains("WPA2")) return "WPA2";
        if (capabilities.contains("WPA")) return "WPA";
        if (capabilities.contains("WEP")) return "WEP";
        return "Open";
    }

    @Override
    public String toString() {
        return "WifiNetwork{" +
                "bssid='" + bssid + '\'' +
                ", ssid='" + ssid + '\'' +
                ", security='" + security + '\'' +
                ", frequency=" + frequency +
                ", standard='" + standard + '\'' +
                '}';
    }
}
