package com.example.mobilesecurityproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.mobilesecurityproject.R;
import com.example.mobilesecurityproject.Utils.ScanServiceManager;
import com.google.android.material.materialswitch.MaterialSwitch;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_WIFI_SCAN = "wifi_scan_enabled";
    private MaterialSwitch switchWifiScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();

        // Load saved toggle state
        boolean isScanEnabled = ScanServiceManager.isScanEnabled(this);
        switchWifiScan.setChecked(isScanEnabled);

        // Start or stop service based on saved state
        ScanServiceManager.handleServiceState(this, isScanEnabled);

        // Toggle listener
        switchWifiScan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ScanServiceManager.setScanEnabled(this, isChecked);
        });

    }

    protected void initViews(){
        switchWifiScan = findViewById(R.id.switchWifiScan);

        findViewById(R.id.btnOpenScanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WifiScannerActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnOpenMap).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });



    }

}