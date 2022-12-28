package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.helper.Validator;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DescriptionVideoActivity extends FragmentActivity implements View.OnClickListener {
    EditText edtDescription;
    Button btnDescription;
    ImageView imvShortCutVideo;
    final String REGEX_HASHTAG = "#([A-Za-z0-9_-]+)";
    private FragmentTransaction ft;
    private FragmentManager fm;

    String username;

    Uri videoUri;
//    final float maximumResolution = 1280 * 720; //720p
    final long maximumDuration = 15000; //miliseconds

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;

    Validator validator;

    ArrayList<String> hashtags;
    String Id;
    VideoSummary videoSummary;
    final String TAG = "DescriptionVideoActivity";

    Handler handler = new Handler();
    Bitmap thumbnail;

    NotificationManagerCompat mNotifyManager;
    NotificationCompat.Builder mBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_video);

        edtDescription = (EditText) findViewById(R.id.edtDescription);
        btnDescription = (Button) findViewById(R.id.btnDescription);
        imvShortCutVideo = (ImageView) findViewById(R.id.imvShortCutVideo);

        validator = Validator.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        fm = getSupportFragmentManager();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String videoPath= bundle.getString("videoUri");
         videoUri = Uri.parse(videoPath);
         Log.d("URI", videoUri.toString());
         hashtags = new ArrayList<>();


        //get thumbnail video, duration
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource( getApplicationContext(), videoUri );
        String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String time = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );
        //time is microseconds
        thumbnail = mmr.getScaledFrameAtTime( 1000000, MediaMetadataRetriever.OPTION_NEXT_SYNC, 1000, 1000 );

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


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "please grant permission!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                    1);
        }

        createNotificationChannel();

        mNotifyManager =
                NotificationManagerCompat.from(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(this, "Video");
        mBuilder.setContentTitle("Video uploading")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_download);





        btnDescription.setOnClickListener(this);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Video";
            String description = "Video";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Video", name, importance);
            channel.setSound(null, null);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnDescription.getId()) {
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(0, mBuilder.build());

            Matcher matcher = Pattern.compile(REGEX_HASHTAG).matcher(edtDescription.getText().toString());
            while(matcher.find()) {
                hashtags.add(matcher.group(0));
            }
            Id = String.valueOf(System.currentTimeMillis());
//            writeHashtags(hashtags);
            uploadThumbnail();
            DocumentReference docRef = db.collection("profiles").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
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
        String ext =  mimeTypeMap.getExtensionFromMimeType(r.getType(videoUri));
        if(ext == null) {
            ext = mimeTypeMap.getFileExtensionFromUrl(videoUri.toString());
        }
        return ext;
    }

    private void uploadVideo(){
        moveToAnotherActivity(CameraActivity.class);
            FirebaseStorage.getInstance().getReference("videos/" + Id + "." + getFileType(videoUri))
                    .putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    String description = edtDescription.getText().toString().trim();
                    Video video = new Video(Id, downloadUri, user.getUid(), username, description);
                    writeNewVideo(video);
                    // When done, update the notification one more time to remove the progress bar
                    mBuilder.setContentText("Upload complete")
                            .setProgress(0,0,false);
                    mNotifyManager.notify(0, mBuilder.build());
                    Toast.makeText(DescriptionVideoActivity.this, "Upload successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    int progressInt = (int) Math.floor(progress);
                    Log.i(TAG, progressInt + "");
                    mBuilder.setProgress(100, progressInt, false);
                    mNotifyManager.notify(0, mBuilder.build());
                }
            });

    }

    private void uploadThumbnail() {
        // Get the data from an ImageView as bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        FirebaseStorage.getInstance().getReference("thumbnails/" + Id + ".jpg")
                .putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        // get the link of video
                        String downloadUri = uriTask.getResult().toString();
                        VideoSummary videoSummary = new VideoSummary(Id, downloadUri, new Long(0));
                        writeNewVideoSummary(videoSummary);
                        writeHashtags(hashtags, videoSummary);
                        Log.i(TAG, "Upload thumbnail successfully");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }
                });
    }


    private void writeNewVideo(Video video) {

        // Basic sign-in info:
        Map<String, Object> videoValues = video.toMap();
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
        db.collection("profiles").document(FirebaseAuth.getInstance().getUid()).
                collection("public_videos").document(video.getVideoId())
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

    private void writeHashtags(ArrayList<String> hashtags, VideoSummary video) {
        Map<String, Object> videoValues = video.toMap();
         hashtags.forEach((hashtag) -> {
            DocumentReference docRef = db.collection("hashtags").document(hashtag).collection("video_summaries").document(Id);
                    docRef.set(videoValues)
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

        });
    }

    private void getAuthorInfo() {

    }
}