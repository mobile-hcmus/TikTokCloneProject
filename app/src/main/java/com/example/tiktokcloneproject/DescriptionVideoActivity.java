package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiktokcloneproject.model.Video;
import com.example.tiktokcloneproject.model.VideoSummary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionVideoActivity extends FragmentActivity implements View.OnClickListener {
    EditText edtDescription;
    Button btnDescription;
    ImageView imvShortCutVideo;
    TextView txvPercent;
    ProgressBar pgbPercent;
    LinearLayout llProgress;
    final String REGEX_HASHTAG = "#([A-Za-z0-9_-]+)";
    private FragmentTransaction ft;
    private FragmentManager fm;

    String username, authorAvatarId;

    Uri videoUri;
//    final float maximumResolution = 1280 * 720; //720p
    final long maximumDuration = 15000; //miliseconds

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    Validator validator;

    ArrayList<String> hashtags;
    String Id;
    final String TAG = "DescriptionVideoActivity";

    Handler handler = new Handler();
    Bitmap thumbnail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_video);

        edtDescription = (EditText) findViewById(R.id.edtDescription);
        btnDescription = (Button) findViewById(R.id.btnDescription);
        imvShortCutVideo = (ImageView) findViewById(R.id.imvShortCutVideo);
        txvPercent = (TextView) findViewById(R.id.txvPercent);
        pgbPercent = (ProgressBar) findViewById(R.id.pgbPercent);
        llProgress = (LinearLayout) findViewById(R.id.llProgress);

        llProgress.setVisibility(View.GONE);

        txvPercent.setText("0%");
        pgbPercent.setProgress(0);

        validator = Validator.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        fm = getSupportFragmentManager();


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String videoPath= bundle.getString("videoUri");
         videoUri = Uri.parse(videoPath);

         hashtags = new ArrayList<>();


        //get thumbnail video, duration
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource( getApplicationContext(), videoUri );
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        //time is microseconds
        thumbnail = mmr.getScaledFrameAtTime( 10000000, MediaMetadataRetriever.OPTION_NEXT_SYNC, 1000, 1000 );

        mmr.release();
        Log.i("Info", "Resolution"  + height + "x" + width + ". Time: " + timeInMillisec / 1000);
        if(!validator.isNumeric(height) || !validator.isNumeric(width)) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_undefined), Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(CameraActivity.class);
        } else if(timeInMillisec > maximumDuration) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_upload_video), Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(CameraActivity.class);
        }

        imvShortCutVideo.setImageBitmap(thumbnail);



        btnDescription.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnDescription.getId()) {
            llProgress.setVisibility(View.VISIBLE);
            Matcher matcher = Pattern.compile(REGEX_HASHTAG).matcher(edtDescription.getText().toString());
            while(matcher.find()) {
                hashtags.add(matcher.group(0));
            }
            Id = String.valueOf(System.currentTimeMillis());
            writeHashtags(hashtags);
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            authorAvatarId = document.get("authorAvatarId", String.class);
                            username = document.get("username", String.class);
                            if(videoUri != null) {
                                handler.post(DescriptionVideoActivity.this::uploadVideo);
                            }
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
    }

    private String getFileType(Uri videoUri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videoUri));
    }

    private void uploadVideo(){

            FirebaseStorage.getInstance().getReference("videos/" + Id + "." + getFileType(videoUri))
                    .putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    String description = edtDescription.getText().toString();
                    Video video = new Video(Id, downloadUri,user.getUid(), username, authorAvatarId, description, hashtags);
                    VideoSummary videoSummary = new VideoSummary(Id, thumbnail);
                    writeNewVideoSummary(videoSummary);
                    writeNewVideo(video);
                    moveToAnotherActivity(CameraActivity.class);
                    Toast.makeText(getApplicationContext(), "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    moveToAnotherActivity(CameraActivity.class);
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    int progressInt = (int) Math.floor(progress);
                    txvPercent.setText(progressInt + "%");
                    Log.i(TAG, progressInt + "");
                    pgbPercent.setProgress(progressInt);
                }
            });

    }


    private void writeNewVideo(Video video) {

        // Basic sign-in info:
        Map<String, Object> videoValues = video.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        db.collection("videos").document(video.getVideoId())
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

    private void writeNewVideoSummary(VideoSummary video) {
        Map<String, Object> videoValues = video.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        db.collection("video_summaries").document(video.getVideoId())
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


    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(DescriptionVideoActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void writeHashtags(ArrayList<String> hashtags) {
        ArrayList<String> videoIds = new ArrayList<>();
        videoIds.add(Id);
        Map<String, Object> docData = new HashMap<>();
        docData.put("videoIds", videoIds);
         hashtags.forEach((hashtag) -> {
            DocumentReference docRef = db.collection("hashtags").document(hashtag);
                    docRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                        docRef.update("videoIds", FieldValue.arrayUnion(Id));
                                } else {
                                    docRef.set(docData);
                                }
                            } else {
                                Log.d(TAG, "Failed with: ", task.getException());
                            }
                        }
                    });

        });
    }

    private void getAuthorInfo() {

    }
}