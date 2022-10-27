package com.example.tiktokcloneproject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FullScreenAvatarActivity extends AppCompatActivity{

    ImageView imvFullScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_avatar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.hide();
        }

        imvFullScreen = (ImageView) findViewById(R.id.imvFullscreen);
//        imvFullScreen.setImageURI(((GlobalVariable) this.getApplication()).getAvatarUri());

        imvFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}