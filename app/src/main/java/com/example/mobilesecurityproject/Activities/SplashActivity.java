package com.example.mobilesecurityproject.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.mobilesecurityproject.R;

import android.animation.Animator;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView lottieSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieSplash = findViewById(R.id.lottieSplash);

        // Play the animation
        lottieSplash.playAnimation();

        // Transition to MainActivity when animation ends
        lottieSplash.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                // Optional: Add logic for animation start
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // Handle cancellation if needed
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // Not needed since loop is false
            }
        });
    }
}