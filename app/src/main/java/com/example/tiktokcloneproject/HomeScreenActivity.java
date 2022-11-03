package com.example.tiktokcloneproject;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeScreenActivity extends Activity implements View.OnClickListener{

    private Button btnProfile;
    private Button btnSearch, btnSwipe, btnAddVideo;
    private TextView tvVideo; // DE TEST. Sau nay sua thanh clip de xem

    FirebaseAuth mAuth;
    FirebaseUser user;

    StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tvVideo = (TextView) findViewById(R.id.tvVideo);
        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnSearch=(Button) findViewById(R.id.btnSearch);
        btnSwipe = (Button) findViewById(R.id.btnSwipe);
        btnAddVideo = (Button) findViewById(R.id.btnAddVideo);

        btnProfile.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnSwipe.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



/////////////////////////////////////////////////////////////////////////
//        final ViewPager2 viewPager2 = findViewById(R.id.viewPager);
//        List<VideoObject> videoObjects = new ArrayList<>();
//
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

        btnAddVideo.setOnClickListener(this);
    }//on Create


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
        if(view.getId() == btnAddVideo.getId()) {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 5);
        }
    }//on click

    Uri videoUri;

    // startActivityForResult is used to receive the result, which is the selected video.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            uploadVideo();
        }
    }

    private String getFileType(Uri videoUri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videoUri));
    }

    private void uploadVideo() {
        if (videoUri != null) {
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Files/" + System.currentTimeMillis() + "." + getFileType(videoUri));
            reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Video");
                    HashMap<String, String> map = new HashMap<>();
                    map.put("videolink", downloadUri);
                    reference1.child("" + System.currentTimeMillis()).setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog

                    Toast.makeText(HomeScreenActivity.this, "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded

                    Toast.makeText(HomeScreenActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    //progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }


}// activity