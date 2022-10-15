package com.example.tiktokcloneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void privacyPage(View view) {
        Intent intent = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
        startActivity(intent);
        finish();
    }
}