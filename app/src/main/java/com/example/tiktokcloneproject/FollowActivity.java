package com.example.tiktokcloneproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.example.tiktokcloneproject.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        btnFollow  = (Button)findViewById(R.id.button_follow);
        btnUnfollow =(Button)findViewById(R.id.button_unfollow);






        //id nhận từ search Activity
        userId= user.getUserId();

        //check đã follow hay chưa
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //id đang đăng nhập
        currentUserID =   currentUser.getUid();

        boolean isFollowed=false;


        //query vào profile, check tồn tại


//













//        currentUser.getUid();

        if (isFollowed) {
            btn = (Button)findViewById(R.id.button_unfollow);
        } else {
            btn = (Button)findViewById(R.id.button_follow);
        }
        btn.setVisibility(View.VISIBLE);

    }}



