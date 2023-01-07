package com.example.tiktokcloneproject.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tiktokcloneproject.R;

public class AccountSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imvBackToSettings;
    private FrameLayout flDeleteAccountOption;
    private FrameLayout flChangePasswordOption;
    private TextView txvChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        txvChangePassword = (TextView) findViewById(R.id.txvChangePassword);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.hide();
        }

        imvBackToSettings = (ImageView) findViewById(R.id.imvBackToSettings);
        flDeleteAccountOption = (FrameLayout) findViewById(R.id.flDeleteAccountOption);
        flChangePasswordOption = (FrameLayout) findViewById(R.id.flChangePasswordOption);

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

        flChangePasswordOption.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == flChangePasswordOption.getId()) {
            Intent intent = new Intent(AccountSettingActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        }
    }
}