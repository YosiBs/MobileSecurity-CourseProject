package com.example.mobilesecurityproject.Activities;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.mobilesecurityproject.R;
import com.example.mobilesecurityproject.Utils.ScanServiceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private MaterialSwitch switchWifiScan;
    private MaterialButton btnOpenScanner, btnOpenMap;
    private LottieAnimationView headerImage;
    private ProgressBar progressBar; // Added for visual feedback
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        // Load saved toggle state
        boolean isScanEnabled = ScanServiceManager.isScanEnabled(this);
        switchWifiScan.setChecked(isScanEnabled);

        // Start or stop service based on saved state
        ScanServiceManager.handleServiceState(this, isScanEnabled);

        // Toggle listener with feedback
        switchWifiScan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            progressBar.setVisibility(View.VISIBLE); // Show progress
            ScanServiceManager.setScanEnabled(this, isChecked);

            // Simulate a short delay for service response (optional)
            buttonView.postDelayed(() -> {
                progressBar.setVisibility(View.GONE); // Hide progress

                 Snackbar.make(buttonView, "WiFi Scan " + (isChecked ? "Enabled" : "Disabled"),
                         Snackbar.LENGTH_SHORT).show();
            }, 500); // 500ms delay for demo purposes
        });

    }

    protected void initViews() {
        switchWifiScan = findViewById(R.id.switchWifiScan);
        btnOpenScanner = findViewById(R.id.btnOpenScanner);
        btnOpenMap = findViewById(R.id.btnOpenMap);
        headerImage = findViewById(R.id.headerLottie);
        progressBar = findViewById(R.id.progressBar); // New ProgressBar


        btnOpenScanner.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WifiScannerActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        });

        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());
        });
    }

    // Optional: Handle back press with a confirmation
    @Override
    public void onBackPressed() {
        Snackbar.make(findViewById(android.R.id.content),
                        "Exit the app?", Snackbar.LENGTH_LONG)
                .setAction("Yes", v -> super.onBackPressed())
                .show();
    }
}