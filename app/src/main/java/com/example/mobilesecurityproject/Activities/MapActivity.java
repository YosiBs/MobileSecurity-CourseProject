package com.example.mobilesecurityproject.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.mobilesecurityproject.Models.WifiEstimate;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Network.RetrofitClient;
import com.example.mobilesecurityproject.Network.WifiApiService;
import com.example.mobilesecurityproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private WifiApiService apiService;
    private Marker currentLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        apiService = RetrofitClient.getService();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle FAB Click
        FloatingActionButton fabMyLocation = findViewById(R.id.fabMyLocation);
        fabMyLocation.setOnClickListener(view -> moveToCurrentLocation());





    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
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
        fetchAllWifiNetworks();
    }
    @SuppressLint("MissingPermission")
    private void moveToCurrentLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                // Check if marker exists, update it instead of creating a new one
                if (currentLocationMarker == null) {
                    currentLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(currentLatLng)
                            .title("You are here"));
                } else {
                    currentLocationMarker.setPosition(currentLatLng);
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
                Log.d("ddd", "Moved to Current Location: " + location.getLatitude() + ", " + location.getLongitude());
            } else {
                Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                Log.e("ddd", "Current location is null.");
            }
        }).addOnFailureListener(e -> Log.e("MapActivity", "Failed to get location", e));
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

    //-------------------------------------------------------------------------------------------------------------

    // ✅ Step 1: Fetch all WiFi networks
    private void fetchAllWifiNetworks() {
        apiService.getAllWifiNetworks().enqueue(new Callback<List<WifiNetwork>>() {
            @Override
            public void onResponse(@NonNull Call<List<WifiNetwork>> call, @NonNull Response<List<WifiNetwork>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (WifiNetwork wifi : response.body()) {
                        //Log.d("ddd", "WiFi Network: " + wifi);
                        fetchEstimatedLocationForWifi(wifi);
                        break;
                    }
                } else {
                    Log.e("MapActivity", "Failed to load WiFi networks: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WifiNetwork>> call, @NonNull Throwable t) {
                Log.e("MapActivity", "API Error: " + t.getMessage());
            }
        });
    }

    // ✅ Step 2: Fetch estimated location for each WiFi network
    private void fetchEstimatedLocationForWifi(WifiNetwork wifi) {
        WifiEstimate wifiEstimate = new WifiEstimate();
        wifiEstimate.setSsid(wifi.getSsid());
        apiService.getEstimatedWifiLocation(wifi.getBssid()).enqueue(new Callback<WifiEstimate>() {
            @Override
            public void onResponse(@NonNull Call<WifiEstimate> call, @NonNull Response<WifiEstimate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WifiEstimate estimatedWifi = response.body();
                    addWifiMarker(estimatedWifi);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WifiEstimate> call, @NonNull Throwable t) {
                Log.e("MapActivity", "API Error (Fetching Estimate): " + t.getMessage());
            }
        });
    }

    // ✅ Step 3: Add a marker for each WiFi network
    private void addWifiMarker(WifiEstimate estimatedWifi) {
        double lat = estimatedWifi.getEstimatedLat();
        double lon = estimatedWifi.getEstimatedLon();

        LatLng location = new LatLng(lat, lon);
        Log.d("ddd", "estimated location: " + location);

        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("WiFi: " + estimatedWifi.getBssid())
                .snippet("Scans: " + estimatedWifi.getScanCount()));

        googleMap.setOnMarkerClickListener(clickedMarker -> {
            showWifiDetailsBottomSheet(estimatedWifi);
            return true;
        });
    }

    // ✅ Step 4: Show details when clicking a marker
    private void showWifiDetailsBottomSheet(WifiEstimate wifi) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_wifi_details);

        // Set details (TODO: Replace with actual TextView IDs from your layout)
        bottomSheetDialog.findViewById(R.id.tvBssid).setTag(wifi.getBssid());
        bottomSheetDialog.findViewById(R.id.tvSsid).setTag(wifi.getSsid());
        bottomSheetDialog.findViewById(R.id.tvScanCount).setTag("Scans: " + wifi.getScanCount());

        bottomSheetDialog.show();
    }


} //Class