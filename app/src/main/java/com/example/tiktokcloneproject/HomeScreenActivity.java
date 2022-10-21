package com.example.tiktokcloneproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreenActivity extends Activity implements View.OnClickListener{

    private Button btnProfile;
    private Button btnSearch;
    private TextView tvVideo; // DE TEST. Sau nay sua thanh clip de xem

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        tvVideo = (TextView) findViewById(R.id.tvVideo);
        btnProfile = (Button) findViewById(R.id.btnProfile);
        btnSearch=(Button) findViewById(R.id.btnSearch);

        btnProfile.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

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
    }




}