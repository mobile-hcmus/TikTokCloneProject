package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

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

import java.util.List;
import java.util.Set;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);

        Bundle bundle = getIntent().getExtras();
        User user=(User) bundle.get("obj");
        //nhận object từ search activity, không cần querry lại!


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




        //check đã follow hay chưa

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

//        currentUser.getUid();

        if (true) {
            btn = (Button)findViewById(R.id.button_unfollow);
        } else {
            btn = (Button)findViewById(R.id.button_follow);
        }
        btn.setVisibility(View.VISIBLE);

    }}



