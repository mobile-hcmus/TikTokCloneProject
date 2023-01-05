package com.example.tiktokcloneproject.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.tiktokcloneproject.activity.CameraActivity;
import com.example.tiktokcloneproject.activity.HomeScreenActivity;
import com.example.tiktokcloneproject.activity.InboxActivity;
import com.example.tiktokcloneproject.activity.MainActivity;
import com.example.tiktokcloneproject.activity.ProfileActivity;
import com.example.tiktokcloneproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NavigationFragment extends Fragment implements View.OnClickListener {
    private Context context = null;
    private String message = "";
    private Button btnHome, btnFriend, btnAddVideo, btnInbox, btnProfile;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static long pressedBackTime = 0;
    private final static String TAG = "NavigationFragment";
    private String avatarUri;


    public static NavigationFragment newInstance(String strArg) {
        NavigationFragment fragment = new NavigationFragment();
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
      //  btnFriend = (Button) layout.findViewById(R.id.btnFriend);
        btnAddVideo = (Button) layout.findViewById(R.id.btnAddVideo);
        btnInbox = (Button) layout.findViewById(R.id.btnInbox);
        btnProfile = (Button) layout.findViewById(R.id.btnProfile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        layout.setFocusableInTouchMode(true);
        layout.requestFocus();
        layout.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int code, KeyEvent keyEvent) {
                if((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && code == keyEvent.KEYCODE_BACK) {
                    Log.d("keycode", context.getClass().toString());
                    Intent intent = new Intent(context, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

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
            handleHomeClick();
        }
        if(view.getId() == btnInbox.getId()) {
            handleInboxClick();
        }
        if(view.getId() == btnFriend.getId()) {

        }

    }

    private void handleProfileClick() {

        if(context instanceof ProfileActivity) {
            return;
        }

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
                showNiceDialogBox(context, null, null);
            }
    }

    private void handleAddClick() {
        if(user == null) {
            showNiceDialogBox(context, null, null);
            return;
        }
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

    private void handleHomeClick() {
        if(context instanceof HomeScreenActivity) {
            Intent intent = new Intent(context, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        Intent intent = new Intent(context, HomeScreenActivity.class);
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
                            if(context instanceof HomeScreenActivity) {
                                return;
                            }
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
