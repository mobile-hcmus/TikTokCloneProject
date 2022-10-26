package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Set;

public class ProfileActivity extends Activity implements View.OnClickListener{
    private TextView txvFollowing, txvFollowers, txvLikes, txvUserName;
    private Button btn, btnEditProfile;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        String userId;
        if (intent.hasExtra("id")) {
            userId = intent.getStringExtra("id");
        } else {
            String action = intent.getAction();
            Uri data = intent.getData();
            List<String> segmentsList = data.getPathSegments();
            userId = segmentsList.get(segmentsList.size() - 1);
        }
        setContentView(R.layout.activity_profile);
        txvFollowing = (TextView)findViewById(R.id.text_following);
        txvFollowers = (TextView)findViewById(R.id.text_followers);
        txvLikes = (TextView)findViewById(R.id.text_likes);
        txvUserName = (TextView)findViewById(R.id.txv_username);
        btnEditProfile =(Button)findViewById(R.id.button_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (userId.equals(user.getUid())) {
            btn = (Button)findViewById(R.id.button_edit_profile);
        } else {
            btn = (Button)findViewById(R.id.button_follow);
        }
        btn.setVisibility(View.VISIBLE);

        db  = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("profiles").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    txvFollowing.setText(((Long)document.get("following")).toString());
                    txvFollowers.setText(((Long)document.get("followers")).toString());
                    txvLikes.setText(((Long)document.get("totalLikes")).toString());
                    txvUserName.setText("@" + document.getString("username"));
                } else { }
            } else { }
        });


        btnEditProfile.setOnClickListener(this);
    }//on create

    public void onClick(View v) {
        if (v.getId() == R.id.text_menu) {
            showDialog();
            return;
        }

        if (v.getId() == R.id.image_avatar) {
//            Bundle bundle = new Bundle();
//            bundle.putString("id", user.getUid());
//            Intent intent = new Intent(ProfileActivity.this, ShareAccountActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);

            showShareAccountDialog();
            return;
        }
        if(v.getId() == btnEditProfile.getId()) {
//            Toast.makeText(this, "YYY", Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(EditProfileActivity.class);
        }
    }

    private void showShareAccountDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_account_layout);

        TextView txvUsernameInSharedPlace = dialog.findViewById(R.id.txvUsernameInSharedPlace);
        ImageView imvAvatarInSharedPlace = dialog.findViewById(R.id.imvAvatarInSharedPlace);
        Button btnCopyURL = dialog.findViewById(R.id.btnCopyURL);
        TextView txvCancelInSharedPlace = dialog.findViewById(R.id.txvCancelInSharedPlace);

        txvUsernameInSharedPlace.setText("@" + user.getUid().toString());

        btnCopyURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("toptop-link", "http://toptoptoptop.com/" + user.getUid().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ProfileActivity.this, "Profile link has been saved to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        imvAvatarInSharedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FullScreenAvatarActivity.class);
                startActivity(intent);
            }
        });

        txvCancelInSharedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout llSetting = dialog.findViewById(R.id.llSetting);
        LinearLayout llSignOut = dialog.findViewById(R.id.llSignOut);

        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, SettingsAndPrivacyActivity.class);
                startActivity(intent);
            }
        });
        llSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut(view);

                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    public void signOut (View v)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
            startActivity(intent);

            finish();
        }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(ProfileActivity.this, cls);

        startActivity(intent);


    }
        // NOTE (Quang): These buttons below belong to Setting and Privacy activity
//    public void privacyPage(View view) {
//        Intent intent = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
}