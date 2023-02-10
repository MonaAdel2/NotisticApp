package com.example.notisticapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashscreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        Thread splashScreen = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                    startActivity(new Intent(SplashscreenActivity.this, LoginActivity.class));
                    finish();
                } catch (Exception e) {
                }
            }
        };
        splashScreen.start();
    }
}