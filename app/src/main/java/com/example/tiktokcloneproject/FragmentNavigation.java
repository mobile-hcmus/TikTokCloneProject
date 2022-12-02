package com.example.tiktokcloneproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentNavigation extends Fragment implements View.OnClickListener {
    private Context context = null;
    private String message = "";
    private Button btnHome, btnFriend, btnAddVideo, btnInbox, btnProfile;
    private FirebaseUser user;

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

        user = FirebaseAuth.getInstance().getCurrentUser();

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
            handleInboxClick();
        }
        if(view.getId() == btnFriend.getId()) {

        }

    }

    private void handleProfileClick() {

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

    private void handleInboxClick() {
        if(user == null) {
            showNiceDialogBox(context, null, null);
            return;
        }
        if(context instanceof InboxActivity) {
            return;
        }
        Intent intent = new Intent(context,InboxActivity.class);
        startActivity(intent);
    }

    private void showNiceDialogBox(Context context, @Nullable String title, @Nullable String message) {
        if(title == null) {
            title = getString(R.string.request_account_title);
        }
        if(message == null) {
            message = getString(R.string.request_account_message);
        }
        try {
            //CAUTION: sometimes TITLE and DESCRIPTION include HTML markers
            AlertDialog.Builder myBuilder = new AlertDialog.Builder(context);
            myBuilder.setIcon(R.drawable.splash_background)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(context, HomeScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    })
                    .setPositiveButton("Sign up/Sign in", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichOne) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }}) //setNegativeButton
                    .show();
        }
        catch (Exception e) { Log.e("Error DialogBox", e.getMessage() ); }
    }


}
