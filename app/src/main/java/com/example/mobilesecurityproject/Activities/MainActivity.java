package com.example.mobilesecurityproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mobilesecurityproject.R;
import com.example.mobilesecurityproject.Services.WifiScanService;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);




        //Start the scanner service
        Intent serviceIntent = new Intent(this, WifiScanService.class);
        startService(serviceIntent);


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