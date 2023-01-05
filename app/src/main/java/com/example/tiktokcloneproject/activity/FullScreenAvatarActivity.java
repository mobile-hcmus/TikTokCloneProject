package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.tiktokcloneproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FullScreenAvatarActivity extends AppCompatActivity{

    ImageView imvFullScreen;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseUser user;
    Bitmap bitmap;
    String folderPath;
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_avatar);
        ActionBar actionBar = getSupportActionBar();
        user = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (actionBar!=null) {
            actionBar.hide();
        }

        imvFullScreen = (ImageView) findViewById(R.id.imvFullscreen);
//        imvFullScreen.setImageURI(((GlobalVariable) this.getApplication()).getAvatarUri());

        getImage();
        imvFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getImage() {
        folderPath = "/user_avatars";
        fileName = user.getUid();
        StorageReference download = storageReference.child(folderPath).child(fileName);

        long MAX_BYTE = 1024*1024;
        download.getBytes(MAX_BYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        imvFullScreen.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        imvFullScreen.setImageResource(R.drawable.default_avatar);
                    }
                });
    }

}