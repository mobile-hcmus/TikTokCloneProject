package com.example.tiktokcloneproject.activity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.fragment.InboxFragment;
import com.example.tiktokcloneproject.fragment.ProfileFragment;
import com.example.tiktokcloneproject.fragment.VideoFragment;
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
    Intent profileIdIntent = null;
    Boolean openAppFromLink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TikTokCloneProject);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        profileIdIntent = getIntent();
        if (profileIdIntent.getExtras() != null) {
            openAppFromLink = profileIdIntent.hasExtra("id");
        }

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
//        if (view.getId() == btnProfile.getId()) {
//            ft = getSupportFragmentManager().beginTransaction();
//            profileFragment = ProfileFragment.newInstance("profile", "");
//            ft.replace(R.id.main_fragment, profileFragment);
//            ft.commit();
//            return;
//        }
        if(view.getId() == btnProfile.getId()) {
            handleProfileClick();
        }
        if(view.getId() == btnAddVideo.getId()) {
            handleAddClick();
        }
        if(view.getId() == btnHome.getId()) {
            handleHomeClick();
        }
        if(view.getId() == btnInbox.getId()) {
            handleInboxClick();
        }
        if(view.getId() == btnFriend.getId()) {

        }
    }//on click

    private void handleProfileClick() {

        if(getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof ProfileFragment) {
            return;
        }

        if (openAppFromLink) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtras(profileIdIntent.getExtras());
            startActivity(intent);
        } else {
            ft = getSupportFragmentManager().beginTransaction();
            profileFragment = ProfileFragment.newInstance("profile", "");
            ft.replace(R.id.main_fragment, profileFragment);
            ft.commit();
        }
    }

    private void handleAddClick() {
        if(user == null) {
            showNiceDialogBox(this, null, null);
            return;
        }
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private void handleInboxClick() {
        if(user == null) {
            showNiceDialogBox(this, null, null);
            return;
        }
        if(getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof InboxFragment) {
            return;
        }
        ft = getSupportFragmentManager().beginTransaction();
        inboxFragment = InboxFragment.newInstance("inbox");
        ft.replace(R.id.main_fragment, inboxFragment);
        ft.commit();
    }

    private void handleHomeClick() {
//        if(getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof VideoFragment) {
//            Intent intent = new Intent(context, HomeScreenActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            return;
//        }
//        Intent intent = new Intent(context, HomeScreenActivity.class);
//        startActivity(intent);
        ft = getSupportFragmentManager().beginTransaction();
        videoFragment = VideoFragment.newInstance("inbox");
        ft.replace(R.id.main_fragment, videoFragment);
        ft.commit();
    }

    private void showNiceDialogBox(Context context, @Nullable String title, @Nullable String message) {
        if(title == null) {
            title = getString(R.string.request_account_title);
        }
        if(message == null) {
            message = getString(R.string.request_account_message);
        }
        try {
            //CAUTION: sometimes TITLE and DESCRIPTION include HTML markers
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            myBuilder.setIcon(R.drawable.splash_background)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(context instanceof HomeScreenActivity) {
                                return;
                            }
                            Intent intent = new Intent(context, HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("Sign up/Sign in", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichOne) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }}) //setNegativeButton
                    .show();
        }
        catch (Exception e) { Log.e("Error DialogBox", e.getMessage() ); }
    }


}// activity