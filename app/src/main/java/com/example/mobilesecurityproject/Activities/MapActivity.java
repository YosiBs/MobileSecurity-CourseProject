package com.example.mobilesecurityproject.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.example.mobilesecurityproject.Models.WifiEstimate;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.Network.RetrofitClient;
import com.example.mobilesecurityproject.Network.WifiApiService;
import com.example.mobilesecurityproject.R;
import com.example.mobilesecurityproject.Utils.MarkerManager;
import com.example.mobilesecurityproject.Utils.WifiDataManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private WifiApiService apiService;
    private MarkerManager markerManager;
    private WifiDataManager wifiDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = RetrofitClient.getService();

        wifiDataManager = new WifiDataManager(apiService);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        WifiNetwork selectedWifi = getWifiNetworkFromIntent();

        Log.d("ddd", "~~ Selected WifiNetwork: " + selectedWifi);

        // Handle FAB Click
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);
        fabMyLocation.setOnClickListener(view -> moveToCurrentLocation());

        if (selectedWifi.getSsid() != null) {
            Log.d("ddd", "KNOWN WIFI'S BUTTON WAS PRESSED");
            fetchSpecificWifi(selectedWifi);
        }

    }

    private WifiNetwork getWifiNetworkFromIntent() {
        // Get Extras from Intent
        String selectedBssid = getIntent().getStringExtra("BSSID");
        String selectedSsid = getIntent().getStringExtra("SSID");
        int selectedFrequency = getIntent().getIntExtra("Frequency", -1);
        String selectedSecurity = getIntent().getStringExtra("Security");

        // Create WifiNetwork object
        WifiNetwork selectedWifi = new WifiNetwork(
                selectedBssid,
                selectedSsid,
                selectedSecurity,
                selectedFrequency,
                "Unknown"
        );
        return selectedWifi;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        markerManager = new MarkerManager(this, googleMap);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // Disable default location button
        // Enable 3D buildings & satellite view (optional)
        googleMap.setBuildingsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Hybrid includes satellite + labels

        // Check and request location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        googleMap.setMyLocationEnabled(true);
        // Only fetch all WiFi networks if no specific BSSID is selected
        if (getIntent().getStringExtra("BSSID") == null) {
            fetchAllWifiNetworks();
        }
    }
    @SuppressLint("MissingPermission")
    private void moveToCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
                Log.d("ddd", "Moved to Current Location: " + location.getLatitude() + ", " + location.getLongitude());
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                Log.e("ddd", "Current location is null.");
            }
        }).addOnFailureListener(e -> Log.e("MapActivity", "Failed to get location", e));
    }
    @SuppressLint("MissingPermission")
    private void moveToLocation(double lat, double lon) {
        LatLng currentLatLng = new LatLng(lat, lon);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
        Log.d("ddd", "Moved to Current Location: " + lat + ", " + lon);
    }

    private void fetchAllWifiNetworks() {
        wifiDataManager.fetchAllWifiNetworks(new WifiDataManager.WifiDataCallback() {
            @Override
            public void onWifiFetched(List<WifiNetwork> wifiNetworks) {
                for (WifiNetwork wifi : wifiNetworks) {
                    fetchSpecificWifi(wifi);
                }
            }

            @Override
            public void onWifiEstimateFetched(WifiEstimate estimate) {
                // Not needed here
            }

            @Override
            public void onWifiScansFetched(List<WifiScan> scans) {
                // Not needed here
            }

            @Override
            public void onError(String message) {
                Log.e("MapActivity", "Error fetching all WiFi: " + message);
            }
        });
    }

    private void fetchSpecificWifi(WifiNetwork wifi) {
        wifiDataManager.fetchEstimatedLocationForWifi(wifi, new WifiDataManager.WifiDataCallback() {
            @Override
            public void onWifiFetched(List<WifiNetwork> wifiNetworks) {
                // Not needed here
            }

            @Override
            public void onWifiEstimateFetched(WifiEstimate estimate) {
                Log.d("ddd", "Estimated Wifi: " + estimate);

                // Move to specific location if handling a selected WiFi
                Log.d("ddd", "estimate.getSsid(): " +estimate.getSsid());
                if (getIntent().getStringExtra("SSID") != null) {
                    moveToLocation(estimate.getEstimatedLat(), estimate.getEstimatedLon());
                    fetchWifiScansForBssid(estimate.getBssid());
                }else{
                    moveToCurrentLocation();
                }

                // Add marker
                markerManager.addWifiMarker(estimate);
            }

            @Override
            public void onWifiScansFetched(List<WifiScan> scans) {
                // Not needed here
            }

            @Override
            public void onError(String message) {
                Log.e("MapActivity", "Error fetching specific WiFi: " + message);
            }
        });
    }

    private void fetchWifiScansForBssid(String bssid) {
        wifiDataManager.fetchWifiScansForBssid(bssid, new WifiDataManager.WifiDataCallback() {
            @Override
            public void onWifiFetched(List<WifiNetwork> wifiNetworks) {
                // Not needed here
            }

            @Override
            public void onWifiEstimateFetched(WifiEstimate estimate) {
                // Not needed here
            }

            @Override
            public void onWifiScansFetched(List<WifiScan> scans) {
                for (WifiScan scan : scans) {
                    markerManager.addScanMarker(scan);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("MapActivity", "Error fetching scans: " + message);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

} //Class