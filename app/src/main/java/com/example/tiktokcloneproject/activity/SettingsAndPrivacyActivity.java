package com.example.tiktokcloneproject.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.tiktokcloneproject.R;

public class SettingsAndPrivacyActivity extends AppCompatActivity {

    private ImageView imvBackToProfile;
    private FrameLayout flAccountOption, flPrivacyOption, flShareProfileOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_and_privacy);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        imvBackToProfile = (ImageView) findViewById(R.id.imvBackToProfile);
        flAccountOption = (FrameLayout) findViewById(R.id.flAccountOption);
        flPrivacyOption = (FrameLayout) findViewById(R.id.flPrivacyOption);
        flShareProfileOption = (FrameLayout) findViewById(R.id.flShareProfileOption);

        imvBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(SettingsAndPrivacyActivity.this, ProfileActivity.class);
//                startActivity(intent);
                onBackPressed();
                finish();
            }
        });

        flAccountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsAndPrivacyActivity.this, AccountSettingActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }
}