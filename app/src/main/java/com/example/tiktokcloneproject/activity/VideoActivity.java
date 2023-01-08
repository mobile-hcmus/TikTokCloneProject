package com.example.tiktokcloneproject.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.VideoAdapter;
import com.example.tiktokcloneproject.model.Video;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends Activity {
    private String videoId;
    private FirebaseFirestore db;
    private ViewPager2 viewPager2;
    private ArrayList<Video> videos;
    private VideoAdapter videoAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (intent.hasExtra("videoId")) {
            videoId = bundle.getString("videoId");
        }else {
            String action = intent.getAction();
            Uri data = intent.getData();
            List<String> segmentsList = data.getPathSegments();
            videoId = segmentsList.get(segmentsList.size() - 1);
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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
                videoAdapter.updateWatchCount(position);
                Log.e("Selected_Page", String.valueOf(videoAdapter.getCurrentPosition()));
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
        db = FirebaseFirestore.getInstance();
        db.collection("videos").document(videoId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Video video = document.toObject(Video.class);
                                videos.add(video);
                                videoAdapter.notifyItemInserted(0);
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        pauseVideo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        continueVideo();
    }

    public void pauseVideo() {
        SharedPreferences currentPosPref = this.getSharedPreferences("position", Context.MODE_PRIVATE);
        SharedPreferences.Editor positionEditor = currentPosPref.edit();
        int currentPosition = videoAdapter.getCurrentPosition();
        positionEditor.putInt("position", currentPosition);
        videoAdapter.pauseVideo(currentPosition);
        positionEditor.apply();
    }

    public void continueVideo() {
        SharedPreferences currentPosPref = this.getSharedPreferences("position", Context.MODE_PRIVATE);
        int currentPosition = currentPosPref.getInt("position", -1);
        if (currentPosition != -1) {
            videoAdapter.playVideo(currentPosition);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnBackVideo) {
            this.finish();
            return;
        }
    }
}