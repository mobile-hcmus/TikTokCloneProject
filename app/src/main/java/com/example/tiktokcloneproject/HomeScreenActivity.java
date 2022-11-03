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
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenActivity extends Activity implements View.OnClickListener{

    private Button btnProfile;
    private Button btnSearch, btnSwipe, btnAddVideo;
    private TextView tvVideo; // DE TEST. Sau nay sua thanh clip de xem
    private ViewPager2 viewPager2;
    List<VideoObject> videoObjects;

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    StorageReference storageRef;
    Uri videoUri;


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
        db = FirebaseFirestore.getInstance();



/////////////////////////////////////////////////////////////////////////
        viewPager2 = findViewById(R.id.viewPager);
        videoObjects = new ArrayList<>();

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

    @Override public void onStart() {
        super.onStart();
        db.collection("videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String Id = document.get("Id", String.class);
                                String Url = document.get("Url", String.class);
                                String authorId = document.get("authorId", String.class);
                                String  description = document.get("description", String.class);

                                VideoObject videoObject = new VideoObject(Id, Url, authorId, description);
                                videoObjects.add(videoObject);

                            }
                            viewPager2.setAdapter(new VideoAdapter(videoObjects));
                        } else {
                            Log.d("ERROR", "Error getting documents: ", task.getException());
                        }
                    }
                });
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
        if(view.getId() == btnAddVideo.getId()) {

//            progressDialog = new ProgressDialog(MainActivity.this);
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 5);
        }
    }//on click


    // startActivityForResult is used to receive the result, which is the selected video.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            uploadVideo();
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
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
            String Id = String.valueOf(System.currentTimeMillis());
            final StorageReference reference = FirebaseStorage.getInstance().getReference("videos/" + Id + "." + getFileType(videoUri));
            reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();

                    VideoObject videoObject = new VideoObject(Id, downloadUri,user.getUid(),"");
                    writeNewVideo(videoObject);
//                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Video");
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("videolink", downloadUri);
//                    reference1.child("" + System.currentTimeMillis()).setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog
//                    progressDialog.dismiss();
                    Toast.makeText(HomeScreenActivity.this, "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
//                    progressDialog.dismiss();
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


    private void writeNewVideo(VideoObject video) {

        // Basic sign-in info:
        Map<String, Object> videoValues = video.toMap();
        final String TAG = "ADD";
        Map<String, Object> childUpdates = new HashMap<>();
        db.collection("videos").document(video.getId())
                .set(videoValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

}// activity