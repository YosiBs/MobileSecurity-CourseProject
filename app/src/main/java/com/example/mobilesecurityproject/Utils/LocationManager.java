package com.example.mobilesecurityproject.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LocationManager {
    private static final String TAG = "LocationManager";
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationListener locationListener;

    public interface LocationListener {
        void onLocationReceived(double latitude, double longitude);
    }

    public LocationManager(Context context, LocationListener listener) {
        this.context = context;
        this.locationListener = listener;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted!");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double preciseLat = roundToPrecision(location.getLatitude());
                        double preciseLon = roundToPrecision(location.getLongitude());

                        Log.d(TAG, String.format("Last known location: %.12f, %.12f", preciseLat, preciseLon));
                        locationListener.onLocationReceived(location.getLatitude(), location.getLongitude());
                    } else {
                        Log.w(TAG, "Last known location is null, requesting new location...");
                        requestNewLocation();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get last known location", e));
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocation() {
        Log.d(TAG, "Requesting precise GPS location...");

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000) // Increase interval for stability
                .setMinUpdateIntervalMillis(1000) // Faster updates
                .setWaitForAccurateLocation(true) // Ensure fresh GPS fix
                .setGranularity(Granularity.GRANULARITY_FINE) // Highest accuracy
                .setMaxUpdateDelayMillis(3000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    if (location.hasAccuracy() && location.getAccuracy() > 10) {
                        Log.w(TAG, "Location accuracy is too low: " + location.getAccuracy() + "m");
                        return; // Ignore inaccurate results
                    }

                    double preciseLat = location.getLatitude();
                    double preciseLon = location.getLongitude();

                    Log.d(TAG, String.format("New High Precision Location: %.12f, %.12f", preciseLat, preciseLon));
                    locationListener.onLocationReceived(preciseLat, preciseLon);
                } else {
                    Log.e(TAG, "Failed to get precise GPS location.");
                }
            }
        }, Looper.getMainLooper());
    }

    // ✅ Helper function to ensure maximum decimal precision
    private double roundToPrecision(double value) {
        return new BigDecimal(value).setScale(12, RoundingMode.HALF_UP).doubleValue();
    }

}
