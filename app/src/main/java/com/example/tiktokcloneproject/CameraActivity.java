package com.example.tiktokcloneproject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class CameraActivity extends Activity implements View.OnClickListener {
    CameraManager manager;
    Button btnUploadVideo;
    ImageView imv;


    FirebaseFirestore db;
    Uri videoUri;
    FirebaseAuth mAuth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        manager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        try {
            Toast.makeText(this, "Id:" + manager.getCameraIdList().length, Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnUploadVideo = (Button) findViewById(R.id.btnUploadVideo);
        imv = (ImageView) findViewById(R.id.imv);
        btnUploadVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnUploadVideo.getId()) {
            //            progressDialog = new ProgressDialog(MainActivity.this);
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 5);
        }
    }

    // startActivityForResult is used to receive the result, which is the selected video.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();

            Intent i = new Intent(this,
                    DescriptionVideoActivity.class);
            i.putExtra("videoUri", videoUri.toString());
            startActivity(i);

//            try {
//                uploadVideo();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
        }
    }




}


