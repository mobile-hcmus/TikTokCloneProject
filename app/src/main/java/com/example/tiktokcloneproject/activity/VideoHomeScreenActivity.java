package com.example.tiktokcloneproject.activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.VideoAdapter;
import com.example.tiktokcloneproject.fragment.InboxFragment;
import com.example.tiktokcloneproject.fragment.ProfileFragment;
import com.example.tiktokcloneproject.fragment.VideoFragment;
import com.example.tiktokcloneproject.helper.OnSwipeTouchListener;
import com.example.tiktokcloneproject.model.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class VideoHomeScreenActivity extends Activity implements View.OnClickListener{
    private String videoId;
    private FirebaseFirestore db;
    private ViewPager2 viewPager2;
    private ArrayList<Video> videos;
    private VideoAdapter videoAdapter;
    private FrameLayout mainFragment;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private long pressedTime;
    private String message = "";
    private Button btnHome, btnAddVideo, btnInbox, btnProfile, btnSearch;
    private static long pressedBackTime = 0;
    private Intent intentMain = null;
    boolean mainFragmentsCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_home_screen);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        btnHome = (Button)findViewById(R.id.btnHome);
        //  btnFriend = (Button) findViewById(R.id.btnFriend);
        btnAddVideo = (Button)findViewById(R.id.btnAddVideo);
        btnInbox = (Button)findViewById(R.id.btnInbox);
        btnProfile = (Button) findViewById(R.id.btnProfile);

        btnSearch= (Button)findViewById(R.id.btnSearch);
        mainFragment = (FrameLayout) findViewById(R.id.main_fragment);

        viewPager2 = findViewById(R.id.viewPager);
        videos = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videos);
        VideoAdapter.setUser(user);
        viewPager2.setAdapter(videoAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                videoAdapter.pauseVideo(videoAdapter.getCurrentPosition());
                videoAdapter.playVideo(position);
                Log.e("Selected_Page", String.valueOf(videoAdapter.getCurrentPosition()));
                Log.e("Selected_Page", videos.get(position).getAuthorId());

                videoAdapter.updateCurrentPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        viewPager2.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
//                Log.i("position", viewPager2.getVerticalScrollbarPosition() + "");
                videoAdapter.pauseVideo(videoAdapter.getCurrentPosition());

            }
        });


        loadVideos();
        intentMain = new Intent(this, HomeScreenActivity.class);


    }

    private void loadVideos() {
        db.collection("videos")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Video video = dc.getDocument().toObject(Video.class);
                                    videos.add(0, video);
                                    videoAdapter.notifyItemInserted(0);
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

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

        if (view.getId() == btnSearch.getId())
        {
            intentMain.putExtra("fragment_search", "");
            intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentMain);
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
        intentMain.putExtra("fragment_profile", "");
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentMain);
    }

    private void handleAddClick() {
        if(user == null) {
            showNiceDialogBox(this, null, null);
            return;
        }
        Intent intent = new Intent(this, CameraActivity.class);
        if (!mainFragmentsCreated) {
            intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainFragmentsCreated = true;
        }
        startActivity(intent);
    }

    private void handleInboxClick() {
        if(user == null) {
            showNiceDialogBox(this, null, null);
            return;
        }
        intentMain.putExtra("fragment_inbox", "");
        if (!mainFragmentsCreated) {
            intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mainFragmentsCreated = true;
        }
        startActivity(intentMain);
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
                            Intent intent = new Intent(context, VideoHomeScreenActivity.class);
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

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences currentPosPref = this.getSharedPreferences("position", Context.MODE_PRIVATE);
        SharedPreferences.Editor positionEditor = currentPosPref.edit();
        int currentPosition = videoAdapter.getCurrentPosition();
        positionEditor.putInt("position", currentPosition);
        videoAdapter.pauseVideo(currentPosition);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences currentPosPref = this.getSharedPreferences("position", Context.MODE_PRIVATE);
        int currentPosition = currentPosPref.getInt("position", -1);
        if (currentPosition != -1) {
            videoAdapter.playVideo(currentPosition);
        }
    }
}