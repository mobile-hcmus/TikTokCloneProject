package com.example.tiktokcloneproject;


import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tiktokcloneproject.adapters.VideoAdapter;
import com.example.tiktokcloneproject.model.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends Activity implements View.OnClickListener{

    private Button btnProfile;
    private Button btnSearch, btnSwipe, btnAddVideo;
    private TextView tvVideo; // DE TEST. Sau nay sua thanh clip de xem
    private ViewPager2 viewPager2;
    List<Video> videos;
    VideoAdapter videoAdapter;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    StorageReference storageRef;
    Uri videoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_TikTokCloneProject);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tvVideo = (TextView) findViewById(R.id.tvVideo);
        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnSearch=(Button) findViewById(R.id.btnSearch);
        btnSwipe = (Button) findViewById(R.id.btnSwipe);


        btnProfile.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSwipe.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();



/////////////////////////////////////////////////////////////////////////
        viewPager2 = findViewById(R.id.viewPager);
        videos = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videos);



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
//        loadVideos();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == btnProfile.getId())
        {
            if (user!=null)
            {
                Bundle bundle = new Bundle();
                bundle.putString("id", user.getUid());
                Intent intent = new Intent(HomeScreenActivity.this, ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(HomeScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        if (view.getId() == btnSearch.getId())
        {
            Intent intent = new Intent(HomeScreenActivity.this, SearchActivity.class);
            startActivity(intent);
        }

        if (view.getId() == R.id.btn_add_video) {
            Intent intent = new Intent(HomeScreenActivity.this, CameraActivity.class);
            startActivity(intent);
        }

        if(view.getId() == btnSwipe.getId()) {
//            Intent intent = new Intent(HomeScreenActivity.this,SwipeVideo.class);
//            startActivity(intent);
        }
    }//on click


    private void loadVideos() {
        db.collection("videos")
                .get(Source.CACHE)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String videoId = document.get("videoId", String.class);
                                String authorId = document.get("authorId", String.class);
                                String Uri = document.get("videoUri", String.class);
                                String username = document.get("username", String.class);
                                String authorAvatarId = document.get("authorAvatarId", String.class);
                                String description = document.get("description", String.class);
                                int totalLikes = document.get("totalLikes", int.class);
                                int totalComments = document.get("totalComments", int.class);

                                Video video = new Video(videoId,Uri, authorId, authorAvatarId, description, username, totalLikes, totalComments );
                                videoAdapter.addVideoObject(video);

                            }
                            viewPager2.setAdapter(videoAdapter);
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}// activity