package com.example.tiktokcloneproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.tiktokcloneproject.R;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class SplashScreenActivity extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    static {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);
                startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_right_to_left, R.anim.fade_in);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}