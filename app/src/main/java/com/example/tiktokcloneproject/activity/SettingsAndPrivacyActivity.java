package com.example.tiktokcloneproject.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tiktokcloneproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsAndPrivacyActivity extends AppCompatActivity {

    private ImageView imvBackToProfile;
    private FrameLayout flAccountOption, flPrivacyOption, flShareProfileOption;
    FirebaseAuth auth;
    FirebaseUser user;
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
        flShareProfileOption = (FrameLayout) findViewById(R.id.flShareProfileOption);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

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

        flShareProfileOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("toptop-link", "http://toptoptoptop.com/" + user.getUid().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SettingsAndPrivacyActivity.this, "Profile link has been saved to clipboard", Toast.LENGTH_SHORT).show();


            }
        });
    }
}