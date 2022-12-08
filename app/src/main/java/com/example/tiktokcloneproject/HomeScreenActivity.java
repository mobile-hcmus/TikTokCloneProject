package com.example.tiktokcloneproject;


import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeScreenActivity extends FragmentActivity implements View.OnClickListener{

    FragmentTransaction ft;
    VideoFragment videoFragment;
    ProfileFragment profileFragment;
    InboxFragment inboxFragment;

    private long pressedTime;
    private String message = "";
    private Button btnHome, btnFriend, btnAddVideo, btnInbox, btnProfile;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static long pressedBackTime = 0;
    private final static String TAG = "NavigationFragment";
    private String avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TikTokCloneProject);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Intent intent = getIntent();
        ft = getSupportFragmentManager().beginTransaction();
        videoFragment = VideoFragment.newInstance("video");
        ft.replace(R.id.main_fragment, videoFragment);
        ft.commit();

        btnHome = (Button)findViewById(R.id.btnHome);
        btnFriend = (Button) findViewById(R.id.btnFriend);
        btnAddVideo = (Button)findViewById(R.id.btnAddVideo);
        btnInbox = (Button)findViewById(R.id.btnInbox);
        btnProfile = (Button) findViewById(R.id.btnProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

    }//on Create

    @Override public void onStart() {
        super.onStart();
//        loadVideos();
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == btnProfile.getId()) {
            ft = getSupportFragmentManager().beginTransaction();
            profileFragment = ProfileFragment.newInstance("profile", "");
            ft.replace(R.id.main_fragment, profileFragment);
            ft.commit();
            return;
        }
        if (view.getId() == btnHome.getId()) {
            ft = getSupportFragmentManager().beginTransaction();
            videoFragment = VideoFragment.newInstance("video");
            ft.replace(R.id.main_fragment, videoFragment);
            ft.commit();
            return;
        }
        if (view.getId() == btnInbox.getId()) {
            ft = getSupportFragmentManager().beginTransaction();
            inboxFragment = InboxFragment.newInstance("inbox");
            ft.replace(R.id.main_fragment, inboxFragment);
            ft.commit();
            return;
        }
        if (view.getId() == btnAddVideo.getId()) {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }
    }//on click

}// activity