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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

                                        deleteVideoIdOnHashTag(videoId);

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
                deleteVideoIdOnPublicVideos(videoId, authorVideoId);
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
                deleteVideoIdOnVideoSummaries(videoId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    void deleteVideoIdOnLikes(String videoId) {
        db.collection("likes").document(videoId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteVideoIdOnComment(videoId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    void deleteVideoIdOnVideoSummaries(String videoId) {
        db.collection("video_summaries").document(videoId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteVideoIdOnLikes(videoId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    void deleteVideoIdOnComment(String videoId) {
        db.collection("comments").document(videoId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(DeleteVideoSettingActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeleteVideoSettingActivity.this, HomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    List<String> getHashTagListFromDescription(String description) {
        String str=description;
        Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
        Matcher mat = MY_PATTERN.matcher(str);
        List<String> hashTagList =new ArrayList<String>();
        while (mat.find()) {
            hashTagList.add("#" + mat.group(1));
        }

        return hashTagList;
    }

    void deleteVideoIdOnHashTag(String videoId) {
        db.collection("videos").document(videoId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        String description = documentSnapshot.get("description").toString();
                        List<String> hashTagsList = getHashTagListFromDescription(description);

                        for (int i = 0; i < hashTagsList.size(); i++) {
                            db.collection("hashtags").document(hashTagsList.get(i)).collection("video_summaries").document(videoId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                        deleteVideoIdOnVideoCollection(videoId);
                    }
                }
            }
        });
    }
}