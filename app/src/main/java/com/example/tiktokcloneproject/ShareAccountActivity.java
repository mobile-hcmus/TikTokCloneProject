package com.example.tiktokcloneproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShareAccountActivity extends Activity {
    TextView txvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_account);
        String userId = getIntent().getStringExtra("id");
        txvUserName = (TextView)findViewById(R.id.txv_username);
        txvUserName.setText("@" + userId);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.txv_done || v.getId() == R.id.not_clickable_zone) {
            finish();
        }
    }
}
