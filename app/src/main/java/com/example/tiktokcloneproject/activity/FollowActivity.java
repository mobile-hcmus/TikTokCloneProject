package com.example.tiktokcloneproject.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.helper.StaticVariable;
import com.example.tiktokcloneproject.model.Notification;
import com.example.tiktokcloneproject.model.User;
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
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FollowActivity extends Activity {
    private TextView txvFollowing, txvFollowers, txvLikes, txvUserName;
    private Button btn, btnUnfollow, btnFollow;
    ImageView imvAvatarProfile;
    Uri avatarUri;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap;
    FirebaseUser user;
    String currentUserID,userId;
    String TAG="abcd";
    boolean isFollowed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);


        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        User user=(User) bundle.get("obj");
        //nhận object từ search activity






        //tải ảnh sau
        imvAvatarProfile = (ImageView) findViewById(R.id.imvAvatarProfile);
        txvUserName = (TextView)findViewById(R.id.txv_username);
        txvUserName.setText(user.getUserName());
        txvFollowing = (TextView)findViewById(R.id.text_following);
        txvFollowers = (TextView)findViewById(R.id.text_followers);
        txvLikes = (TextView)findViewById(R.id.text_likes);
        txvUserName = (TextView)findViewById(R.id.txv_username);
        btn = (Button)findViewById(R.id.button_follow);
        btn.setVisibility(View.VISIBLE);
        //btnFollow  = (Button)findViewById(R.id.button_follow);
        //btnUnfollow =(Button)findViewById(R.id.button_unfollow);




        //id nhận từ search Activity
        userId= user.getUserId();

        //check đã follow hay chưa
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //id đang đăng nhập
        currentUserID =   currentUser.getUid();

        db = FirebaseFirestore.getInstance();
        //query vào profile, check tồn tại


        Map<String, Object> Data = new HashMap<>();
        Data.put("userID","2zig2V6vM4bUBefquTud8jHsy6M2");



        DocumentReference docRef = db.collection("profiles").document(currentUserID)
                .collection("following").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        isFollowed=true;
                        handleFollowed();

                    } else {
                        Log.d(TAG, "No such document");
                        isFollowed=false;
                        handleUnfollowed();

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        //        db.collection("profiles").document(currentUserID)
//                .collection("following").document("2zig2V6vM4bUBefquTud8jHsy6M2")
//                .set(Data)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d(TAG, "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error writing document", e);
//                    }
//                });

//        currentUser.getUid();


//        if (isFollowed==true) {
//            btn = (Button)findViewById(R.id.button_unfollow);
//
//            Log.w(TAG, "true");
//        } else {
//            btn.setText("follow");
//            Log.w(TAG, "false");
//            btn = (Button)findViewById(R.id.button_follow);
//        }
//        btn.setVisibility(View.VISIBLE);

    }
    private void handleUnfollowed() {
        btn.setText("Follow");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "follow clicked");

                Map<String, Object> Data = new HashMap<>();
                Data.put("userID",userId);


                //thêm following
                db.collection("profiles").document(currentUserID)
                .collection("following").document(userId)
                .set(Data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        handleFollowed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

                //thêm follower

                Map<String, Object> Data1 = new HashMap<>();
                Data1.put("userID",currentUserID);
                db.collection("profiles").document(userId)
                        .collection("followers").document(currentUserID)
                        .set(Data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "follower added");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "follower fail",e);
                            }
                        });


            }
        });
    }

    private void handleFollowed() {
        btn.setText("Unfollow");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "unfollow clicked");

                //xóa following
                db.collection("profiles").document(currentUserID)
                        .collection("following").document(userId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                handleUnfollowed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                //xóa follower
                db.collection("profiles").document(userId)
                        .collection("followers").document(currentUserID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });



            }
        });
    }

    public void notifyFollow() {
            db.collection("users").document(user.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String username = document.get("username", String.class);
                                    Notification.pushNotification(username, userId , StaticVariable.LIKE);
                                    Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.getData());
                                } else {
                                    Log.d(ContentValues.TAG, "No such document");
                                }
                            } else {
                                Log.d(ContentValues.TAG, "get failed with ", task.getException());
                            }
                        }
                    });


        }
    }




