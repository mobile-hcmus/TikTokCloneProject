package com.example.tiktokcloneproject;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShareAccountActivity extends Activity {
    TextView txvUserName;
    String userId;

    ImageView imvCopyLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_account);
        userId = getIntent().getStringExtra("id");
        txvUserName = (TextView)findViewById(R.id.txv_username);
        txvUserName.setText("@" + userId);
        imvCopyLink = findViewById(R.id.imvCopyLink);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.txv_done || v.getId() == R.id.not_clickable_zone) {
            finish();
        }

        if (v.getId() == R.id.btn_copy_link) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("toptop-link", "http://toptoptoptop.com/" + userId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Profile link has been saved to clipboard", Toast.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.imvCopyLink) {
            Intent intent = new Intent(ShareAccountActivity.this, FullScreenAvatarActivity.class);
            startActivity(intent);
        }
    }
}
