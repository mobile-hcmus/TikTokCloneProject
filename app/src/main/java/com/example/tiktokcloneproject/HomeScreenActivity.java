package com.example.tiktokcloneproject;


import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktokcloneproject.adapters.VideoAdapter;
import com.example.tiktokcloneproject.model.Video;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends FragmentActivity implements View.OnClickListener{

    private ImageButton btnSearch;
    private TextView tvVideo; // DE TEST. Sau nay sua thanh clip de xem
    private ViewPager2 viewPager2;
    ArrayList<Video> videos;
    VideoAdapter videoAdapter;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    StorageReference storageRef;
    Uri videoUri;

    FragmentTransaction ft;
    NavigationFragment navigation;
    private long pressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TikTokCloneProject);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tvVideo = (TextView) findViewById(R.id.tvVideo);

        btnSearch=(ImageButton) findViewById(R.id.btnSearch);

        ft = getSupportFragmentManager().beginTransaction();
        navigation = NavigationFragment.newInstance("navigation");
        ft.replace(R.id.flNavigation, navigation);
        ft.commit();

        btnSearch.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();



/////////////////////////////////////////////////////////////////////////
        viewPager2 = findViewById(R.id.viewPager);
        videos = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videos);
        viewPager2.setAdapter(videoAdapter);





//        VideoObject videoObject1 = new VideoObject("https://firebasestorage.googleapis.com/v0/b/toptop-android.appspot.com/o/video_2022-11-01_10-13-57.mp4?alt=media&token=42fbd886-ec46-418b-aee1-368eafb7167a", "1", "1");
//        videoObjects.add(videoObject1);
//
//        VideoObject videoObject2 = new VideoObject("https://firebasestorage.googleapis.com/v0/b/toptop-android.appspot.com/o/video_2022-11-01_10-14-02.mp4?alt=media&token=0035980c-f74d-4b22-ae6e-b8c979cd1999", "2", "2");
//        videoObjects.add(videoObject2);
//
//        viewPager2.setAdapter(new VideoAdapter(videoObjects));
        ///////////////////////////////////////////////////////////////


//        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    tvVideo.setText("Đã Đăng Nhập");
//                }
//                else
//                {
//                    tvVideo.setText("Chưa Đăng Nhập");
//                }
//            }
//        });

        if (user != null)
        {
            tvVideo.setText("Đã Đăng Nhập");
        }
        else
        {
            tvVideo.setText("Chưa Đăng Nhập");
        }


    }//on Create

    @Override public void onStart() {
        super.onStart();
        loadVideos();
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
        if (view.getId() == btnSearch.getId())
        {
            Intent intent = new Intent(HomeScreenActivity.this, SearchActivity.class);
            startActivity(intent);
        }

    }//on click


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

}// activity