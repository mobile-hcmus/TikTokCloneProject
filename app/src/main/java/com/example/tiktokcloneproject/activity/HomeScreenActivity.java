package com.example.tiktokcloneproject.activity;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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
import com.example.tiktokcloneproject.fragment.SearchFragment;
import com.example.tiktokcloneproject.fragment.VideoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeScreenActivity extends FragmentActivity implements View.OnClickListener{

    FragmentTransaction ft;
    VideoFragment videoFragment;
    SearchFragment searchFragment;
    ProfileFragment profileFragment;
    InboxFragment inboxFragment;
    private long pressedTime;
    private String message = "";
    private Button btnHome, btnAddVideo, btnInbox, btnProfile, btnSearch;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static long pressedBackTime = 0;
    private final static String TAG = "NavigationFragment";
    Intent fragmentIntent = null;
    Boolean openAppFromLink = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TikTokCloneProject);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        fragmentIntent = getIntent();
        ft = getSupportFragmentManager().beginTransaction();


        if (fragmentIntent.getExtras() != null) {
            if (fragmentIntent.hasExtra("id")) {
                openAppFromLink = true;
            }
            if (fragmentIntent.hasExtra("fragment_inbox")) {
                inboxFragment = InboxFragment.newInstance("inbox");
                ft.add(R.id.main_fragment, inboxFragment);

            } else  if (fragmentIntent.hasExtra("fragment_profile")) {
                profileFragment = ProfileFragment.newInstance("profile", "");
                ft.add(R.id.main_fragment, profileFragment);
            } else if (fragmentIntent.hasExtra("fragment_search")) {
                searchFragment = SearchFragment.newInstance("search");
                ft.add(R.id.main_fragment, searchFragment);
            } else {
                videoFragment = VideoFragment.newInstance("fragment_video");
                ft.add(R.id.main_fragment, videoFragment);
            }
        } else {
            videoFragment = VideoFragment.newInstance("fragment_video");
            ft.add(R.id.main_fragment, videoFragment);
        }
        ft.commit();

        btnHome = (Button)findViewById(R.id.btnHome);
      //  btnFriend = (Button) findViewById(R.id.btnFriend);
        btnAddVideo = (Button)findViewById(R.id.btnAddVideo);
        btnInbox = (Button)findViewById(R.id.btnInbox);
        btnProfile = (Button) findViewById(R.id.btnProfile);

        btnSearch=(Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

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
    protected void onPause() {
        super.onPause();
        stopVideoFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

        if (view.getId() == btnSearch.getId())
        {
            ft = getSupportFragmentManager().beginTransaction();
            if (searchFragment == null)  {
                searchFragment = SearchFragment.newInstance("search");
                ft.add(R.id.main_fragment, searchFragment);
            }
            showFragments(1);
            ft.commit();
        }
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

    }//on click

    private void handleProfileClick() {

        if(user == null) {
            Intent intent = new Intent(this, SignupChoiceActivity.class);
            startActivity(intent);
            return;
        }

        if (openAppFromLink) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtras(fragmentIntent.getExtras());
            startActivity(intent);
        } else {
            ft = getSupportFragmentManager().beginTransaction();
            if (profileFragment == null) {
                profileFragment = ProfileFragment.newInstance("profile", "");
                ft.add(R.id.main_fragment, profileFragment);
            }
            showFragments(3);
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
        overridePendingTransition(R.anim.slide_right_to_left, R.anim.fade_in);
    }

    private void handleInboxClick() {
        if(user == null) {
            showNiceDialogBox(this, null, null);
            return;
        }

        ft = getSupportFragmentManager().beginTransaction();
        if (inboxFragment == null) {
            inboxFragment = InboxFragment.newInstance("inbox");
            ft.add(R.id.main_fragment, inboxFragment);
        }
        showFragments(2);
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
        if (videoFragment == null) {
            inboxFragment = InboxFragment.newInstance("video");
            ft.add(R.id.main_fragment, videoFragment);
        }
        showFragments(0);
        ft.commit();
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private void showFragments(int position) { // search, inbox, profile
        if (position == 0) {
            if (!videoFragment.isVisible()) {
                ft.show(videoFragment);
                continueVideoFragment();
            }
        }

        if (position == 1) {
            if (!searchFragment.isVisible()) {
                ft.show(searchFragment);
            }
        }

        if (position == 2) {
            if (!inboxFragment.isVisible()) {
                ft.show(inboxFragment);
            }
        }

        if (position == 3) {
            if (!profileFragment.isVisible()) {
                ft.show(profileFragment);
            }
        }

        if (videoFragment != null && position != 0) {
            if (videoFragment.isVisible()) {
                ft.hide(videoFragment);
                stopVideoFragment();
            }
        }

        if (searchFragment != null && position != 1) {
            if (searchFragment.isVisible()) {
                ft.hide(searchFragment);
            }
        }

        if (inboxFragment != null && position != 2) {
            if (inboxFragment.isVisible()) {
                ft.hide(inboxFragment);
            }
        }

        if (profileFragment != null && position != 3) {
            if (profileFragment.isVisible()) {
                ft.hide(profileFragment);
            }
        }

    }
    public void stopVideoFragment() {
        if (videoFragment != null) {
                videoFragment.pauseVideo();
        }
    }

    public void continueVideoFragment() {
        if (videoFragment != null) {
                videoFragment.continueVideo();
        } else {
            ft = getSupportFragmentManager().beginTransaction();
            videoFragment = VideoFragment.newInstance("fragment_video");
            ft.add(R.id.main_fragment, videoFragment);
            ft.commit();
        }
    }



}// activity