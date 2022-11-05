package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class DescriptionVideoActivity extends FragmentActivity implements View.OnClickListener {
    EditText edtDescription;
    Button btnDescription;
    ImageView imvShortCutVideo;
    Fragment fragmentWaiting;
    private FragmentTransaction ft;
    private FragmentManager fm;

    Uri videoUri;
    final float maximumResolution = 1280 * 720; //720p
    final long maximumDuration = 15000; //miliseconds

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    Validator validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_video);

        edtDescription = (EditText) findViewById(R.id.edtDescription);
        btnDescription = (Button) findViewById(R.id.btnDescription);
        imvShortCutVideo = (ImageView) findViewById(R.id.imvShortCutVideo);
        fragmentWaiting = (Fragment) getSupportFragmentManager().findFragmentById(R.id.fragWaiting);

        validator = Validator.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        fm = getSupportFragmentManager();

        addShowHideListener(fragmentWaiting);

        Intent intent = getIntent();
        String videoPath= intent.getStringExtra("videoUri");
         videoUri = Uri.parse(videoPath);


        //get thumbnail video, duration
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource( getApplicationContext(), videoUri );
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        //time is microseconds
        Bitmap bm = mmr.getScaledFrameAtTime( 10000000, MediaMetadataRetriever.OPTION_NEXT_SYNC, 1000, 1000 );

        mmr.release();
        Log.i("Info", "Resolution"  + height + "x" + width + ". Time: " + timeInMillisec / 1000);
        if(!validator.isNumeric(height) || !validator.isNumeric(width)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_undefined), Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(CameraActivity.class);
        } else if(Float.parseFloat(height) * Float.parseFloat(width) > maximumResolution  || timeInMillisec > maximumDuration) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_upload_video), Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(CameraActivity.class);
        }

        imvShortCutVideo.setImageBitmap(bm);

        btnDescription.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnDescription.getId()) {
            addShowHideListener(fragmentWaiting);
            uploadVideo();
        }
    }

    private String getFileType(Uri videoUri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videoUri));
    }

    private void uploadVideo(){
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

                    Video video = new Video(Id, downloadUri,user.getUid(),edtDescription.getText().toString());
                    writeNewVideo(video);
//                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Video");
//                    HashMap<String, String> map = new HashMap<>();
//                    map.put("videolink", downloadUri);
//                    reference1.child("" + System.currentTimeMillis()).setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog
//                    progressDialog.dismiss();
                    addShowHideListener(fragmentWaiting);
                    moveToAnotherActivity(CameraActivity.class);
                    Toast.makeText(getApplicationContext(), "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
//                    progressDialog.dismiss();
                    addShowHideListener(fragmentWaiting);
                    moveToAnotherActivity(CameraActivity.class);
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void writeNewVideo(Video video) {

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

    void addShowHideListener(final Fragment fragment) {
        ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if (fragment.isHidden()) {
            ft.show(fragment);
        } else {
            ft.hide(fragment);
        }
        ft.commit();

    }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(DescriptionVideoActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}