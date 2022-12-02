package com.example.tiktokcloneproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentNavigation extends Fragment implements View.OnClickListener {
    Context context = null;
    String message = "";
    Button btnHome, btnFriend, btnAddVideo, btnInbox, btnProfile;

    public static FragmentNavigation newInstance(String strArg) {
        FragmentNavigation fragment = new FragmentNavigation();
        Bundle args = new Bundle();
        args.putString("name", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// inflate res/layout_blue.xml to make GUI holding a TextView and a ListView
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_navigation, null);
        btnHome = (Button) layout.findViewById(R.id.btnHome);
        btnFriend = (Button) layout.findViewById(R.id.btnFriend);
        btnAddVideo = (Button) layout.findViewById(R.id.btnAddVideo);
        btnInbox = (Button) layout.findViewById(R.id.btnInbox);
        btnProfile = (Button) layout.findViewById(R.id.btnProfile);

        btnHome.setOnClickListener(this);
        btnFriend.setOnClickListener(this);
        btnAddVideo.setOnClickListener(this);
        btnInbox.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        return layout;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnProfile.getId()) {
            handleProfileClick();
        }
        if(view.getId() == btnAddVideo.getId()) {
            handleAddClick();
        }
        if(view.getId() == btnHome.getId()) {

        }
        if(view.getId() == btnInbox.getId()) {
            Intent intent = new Intent(context,InboxActivity.class);
            startActivity(intent);
        }
        if(view.getId() == btnFriend.getId()) {

        }

    }

    private void handleProfileClick() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
            {
                Bundle bundle = new Bundle();
                bundle.putString("id", user.getUid());
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
    }

    private void handleAddClick() {
        Intent intent = new Intent(context, CameraActivity.class);
        startActivity(intent);
    }


}
