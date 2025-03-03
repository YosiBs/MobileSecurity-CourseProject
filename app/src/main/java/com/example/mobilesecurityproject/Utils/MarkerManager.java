package com.example.mobilesecurityproject.Utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.example.mobilesecurityproject.Models.WifiEstimate;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MarkerManager {
    private final GoogleMap googleMap;
    private final Set<Marker> wifiMarkers = new HashSet<>();
    private final Context context;
    public MarkerManager(Context context, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.context = context;
    }

    // Add WiFi Marker
    public void addWifiMarker(WifiEstimate estimatedWifi) {
        LatLng location = new LatLng(estimatedWifi.getEstimatedLat(), estimatedWifi.getEstimatedLon());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("WiFi: " + estimatedWifi.getSsid())
                .snippet("Scan Count: " + estimatedWifi.getScanCount())
                .icon(resizeMarkerIcon(R.drawable.wifinetwork, 100, 100)));

        if (marker != null) {
            wifiMarkers.add(marker);
        }
    }

    // Add Scan Marker
    public void addScanMarker(WifiScan wifiScan) {
        LatLng location = new LatLng(wifiScan.getLocationLat(), wifiScan.getLocationLon());

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Scan Location")
                .snippet("Signal Strength: " + wifiScan.getSignalStrength())
                .icon(resizeMarkerIcon(R.drawable.info, 70, 70)));
    }


    // Resize Marker Icon
    private BitmapDescriptor resizeMarkerIcon(int drawableRes, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }


}//class
