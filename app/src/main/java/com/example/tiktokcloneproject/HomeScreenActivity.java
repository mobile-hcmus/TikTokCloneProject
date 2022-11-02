package com.example.tiktokcloneproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends Activity implements View.OnClickListener{

    private Button btnProfile;
    private Button btnSearch, btnSwipe;
    private TextView tvVideo; // DE TEST. Sau nay sua thanh clip de xem

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        final ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        List<VideoObject> videoObjects = new ArrayList<>();

        VideoObject videoObject1 = new VideoObject("https://firebasestorage.googleapis.com/v0/b/toptop-android.appspot.com/o/video_2022-11-01_10-13-57.mp4?alt=media&token=42fbd886-ec46-418b-aee1-368eafb7167a", "1", "1");
        videoObjects.add(videoObject1);

        VideoObject videoObject2 = new VideoObject("https://firebasestorage.googleapis.com/v0/b/toptop-android.appspot.com/o/video_2022-11-01_10-14-02.mp4?alt=media&token=0035980c-f74d-4b22-ae6e-b8c979cd1999", "2", "2");
        videoObjects.add(videoObject2);

        viewPager2.setAdapter(new VideoAdapter(videoObjects));
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
    }




}