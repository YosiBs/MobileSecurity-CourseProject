package com.example.mobilesecurityproject.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.mobilesecurityproject.Models.WifiEstimate;
import com.example.mobilesecurityproject.Models.WifiNetwork;
import com.example.mobilesecurityproject.Models.WifiScan;
import com.example.mobilesecurityproject.Network.RetrofitClient;
import com.example.mobilesecurityproject.Network.WifiApiService;
import com.example.mobilesecurityproject.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

        // Use the selected WifiNetwork
        if (selectedBssid != null) {
            fetchEstimatedLocationForWifi(selectedWifi);
        } else {
            fetchAllWifiNetworks(); // Default behavior if no specific BSSID was passed
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
        apiService.getEstimatedWifiLocation(wifi.getBssid()).enqueue(new Callback<WifiEstimate>() {
            @Override
            public void onResponse(@NonNull Call<WifiEstimate> call, @NonNull Response<WifiEstimate> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WifiEstimate estimatedWifi = response.body();
                    estimatedWifi.setSsid(wifi.getSsid());
                    if(wifi.getBssid() != null){
                        moveToLocation(estimatedWifi.getEstimatedLat(), estimatedWifi.getEstimatedLon());
                        fetchWifiScansForBssid(estimatedWifi.getBssid());
                    }
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

        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title("WiFi: " + estimatedWifi.getBssid())
                .snippet("Scans: " + estimatedWifi.getScanCount()))
                .setIcon(resizeMarkerIcon(R.drawable.wifinetwork, 100,100));

        googleMap.setOnMarkerClickListener(clickedMarker -> {
            showWifiDetailsBottomSheet(estimatedWifi);
            return true;
        });
    }

    // ✅ Step 4: Show details when clicking a marker
    private void showWifiDetailsBottomSheet(WifiEstimate wifiEstimate) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_wifi_details, null);
        bottomSheetDialog.setContentView(view);

        // Find views inside the bottom sheet
        TextView tvBssid = view.findViewById(R.id.tvBssid);
        TextView tvSsid = view.findViewById(R.id.tvSsid);
        TextView tvScanCount = view.findViewById(R.id.tvScanCount);

        // Set actual details
        tvBssid.setText("BSSID: " + wifiEstimate.getBssid());
        tvSsid.setText("SSID: " + wifiEstimate.getSsid());
        tvScanCount.setText("Scans: " + wifiEstimate.getScanCount());

        // Show the bottom sheet
        bottomSheetDialog.show();
    }

//===============================================================================================================
//===============================================================================================================




    private void fetchWifiScansForBssid(String bssid) {
        apiService.getScansByBssid(bssid).enqueue(new Callback<List<WifiScan>>() {
            @Override
            public void onResponse(@NonNull Call<List<WifiScan>> call, @NonNull Response<List<WifiScan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (WifiScan scan : response.body()) {
                        LatLng scanLocation = new LatLng(scan.getLocationLat(), scan.getLocationLon());
                        googleMap.addMarker(new MarkerOptions()
                                .position(scanLocation)
                                .title("Scan Location")
                                .snippet("Signal Level: " + scan.getSignalStrength()))
                                .setIcon(resizeMarkerIcon(R.drawable.info, 70,70));;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WifiScan>> call, @NonNull Throwable t) {
                Log.e("MapActivity", "API Error (Fetching WiFi Scans): " + t.getMessage());
            }
        });
    }

    private BitmapDescriptor resizeMarkerIcon(int drawableRes, int width, int height) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableRes);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

} //Class