package com.example.mobilesecurityproject.Models;


public class WifiEstimate {
    private String bssid;
    private String ssid;
    private double estimated_lat;
    private double estimated_lon;
    private int scan_count;

    public WifiEstimate() {
        this.bssid = "";
        this.ssid = "";
        this.estimated_lat = 0.0;
        this.estimated_lon = 0.0;
        this.scan_count = 0;
    }



    public String getBssid() { return bssid; }
    public String getSsid() { return ssid; }
    public double getEstimatedLat() { return estimated_lat; }
    public double getEstimatedLon() { return estimated_lon; }
    public int getScanCount() { return scan_count; }

    public WifiEstimate setBssid(String bssid) {
        this.bssid = bssid;
        return this;
    }

    public WifiEstimate setSsid(String ssid) {
        this.ssid = ssid;
        return this;
    }

    public WifiEstimate setEstimated_lat(double estimated_lat) {
        this.estimated_lat = estimated_lat;
        return this;
    }

    public WifiEstimate setEstimated_lon(double estimated_lon) {
        this.estimated_lon = estimated_lon;
        return this;
    }

    public WifiEstimate setScan_count(int scan_count) {
        this.scan_count = scan_count;
        return this;
    }
}

