package com.example.tiktokcloneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

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

    public void signOut(View v)
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
        startActivity(intent);

        finish();
    }
}