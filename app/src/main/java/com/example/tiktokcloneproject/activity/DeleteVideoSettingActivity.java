package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tiktokcloneproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteVideoSettingActivity extends AppCompatActivity {

    private ImageView imvBackToVideo;
    private FrameLayout flDeleteVideo;

    private String videoId;
    private String authorVideoId;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_video_setting);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        imvBackToVideo = (ImageView) findViewById(R.id.imvBackToVideo);
        flDeleteVideo = (FrameLayout) findViewById(R.id.flDeleteVideo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoId = bundle.getString("videoId");
        authorVideoId = bundle.getString("authorId");


        imvBackToVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(SettingsAndPrivacyActivity.this, ProfileActivity.class);
//                startActivity(intent);
                onBackPressed();
                finish();
            }
        });

        flDeleteVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(DeleteVideoSettingActivity.this);
                builder1.setMessage("Are you sure you want to delete this video?");
                builder1.setCancelable(true);
                builder1.setInverseBackgroundForced(true);

                builder1.setPositiveButton(
                        "Delete",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String url = (new StringBuilder()).append("videos/").append(videoId).append(".mp4").toString();
                                Log.d("URL", url);
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();

                                StorageReference desertRef = storageRef.child(url);

                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        deleteVideoIdOnVideoCollection(videoId);
                                        deleteVideoIdOnPublicVideos(videoId, authorVideoId);
                                        Toast.makeText(DeleteVideoSettingActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DeleteVideoSettingActivity.this, HomeScreenActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(DeleteVideoSettingActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                });;
                                dialog.cancel();
                            }
                        }).show();

                AlertDialog alert11 = builder1.create();
                alert11.show();
                Button buttonBackground = alert11.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonBackground.setBackgroundColor(Color.RED);

            }
        });
    }

    void deleteVideoIdOnVideoCollection(String videoId) {
        db.collection("videos").document(videoId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    void deleteVideoIdOnPublicVideos(String videoId, String userId) {
        db.collection("profiles").document(userId).collection("public_videos").document(videoId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}