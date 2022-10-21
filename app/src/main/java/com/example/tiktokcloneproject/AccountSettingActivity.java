package com.example.tiktokcloneproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AccountSettingActivity extends AppCompatActivity {

    private ImageView imvBackToSettings;
    private FrameLayout flDeleteAccountOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.hide();
        }

        imvBackToSettings = (ImageView) findViewById(R.id.imvBackToSettings);
        flDeleteAccountOption = (FrameLayout) findViewById(R.id.flDeleteAccountOption);

        imvBackToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingActivity.this, SettingsAndPrivacyActivity.class);
                startActivity(intent);

                finish();
            }
        });

        flDeleteAccountOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountSettingActivity.this, DeleteAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}