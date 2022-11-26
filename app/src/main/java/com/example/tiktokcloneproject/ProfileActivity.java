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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.identity.zbn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity implements View.OnClickListener{
    private TextView txvFollowing, txvFollowers, txvLikes, txvUserName;
    private EditText edtBio;
    private Button btn, btnEditProfile;
    private LinearLayout llFollowing, llFollowers;
    ImageView imvAvatarProfile;
    Uri avatarUri;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap;
    String userId;
    DocumentReference docRef;
    String oldBioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (intent.getExtras() != null) {
            if (intent.hasExtra("id")) {
                userId = intent.getStringExtra("id");
            } else {
                String action = intent.getAction();
                Uri data = intent.getData();
                List<String> segmentsList = data.getPathSegments();
                userId = segmentsList.get(segmentsList.size() - 1);
            }
        } else {
            userId =  user.getUid();

        }
        setContentView(R.layout.activity_profile);
        txvFollowing = (TextView)findViewById(R.id.text_following);
        txvFollowers = (TextView)findViewById(R.id.text_followers);
        txvLikes = (TextView)findViewById(R.id.text_likes);
        txvUserName = (TextView)findViewById(R.id.txv_username);
        edtBio = (EditText)findViewById(R.id.edt_bio);
        btnEditProfile =(Button)findViewById(R.id.button_edit_profile);
        imvAvatarProfile = (ImageView) findViewById(R.id.imvAvatarProfile);
        llFollowers = (LinearLayout) findViewById(R.id.ll_followers);
        llFollowing = (LinearLayout) findViewById(R.id.ll_following);

        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
//        avatarUri = getIntent().getParcelableExtra("uri");

        imvAvatarProfile.setImageURI(avatarUri);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (userId.equals(user.getUid())) {
            btn = (Button)findViewById(R.id.button_edit_profile);
        } else {
            btn = (Button)findViewById(R.id.button_follow);
        }
        btn.setVisibility(View.VISIBLE);

        db  = FirebaseFirestore.getInstance();
        docRef = db.collection("profiles").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    txvFollowing.setText(((Long)document.get("following")).toString());
                    txvFollowers.setText(((Long)document.get("followers")).toString());
                    txvLikes.setText(((Long)document.get("likes")).toString());
                    txvUserName.setText("@" + document.getString("userName"));
                    oldBioText = document.getString("bio");
                    edtBio.setText(oldBioText);

                } else { }
            } else { }
        });
        oldBioText = edtBio.getText().toString();
        edtBio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    findViewById(R.id.layout_bio).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.layout_bio).setVisibility(View.GONE);
                }
            }
        });
        btnEditProfile.setOnClickListener(this);

    }//on create

    @Override
    protected void onPause() {
        super.onPause();
    }

    void updateBio() {
        docRef.update("bio", edtBio.getText().toString());
        oldBioText = edtBio.getText().toString();
    }


    public void onClick(View v) {
        if (v.getId() == R.id.text_menu) {
            showDialog();
            return;
        }

        if (v.getId() == R.id.imvAvatarProfile) {
//            Bundle bundle = new Bundle();
//            bundle.putString("id", user.getUid());
//            Intent intent = new Intent(ProfileActivity.this, ShareAccountActivity.class);
//            intent.putExtras(bundle);
//            startActivity(intent);

            showShareAccountDialog();
            return;
        }
        if (v.getId() == R.id.btn_temporary) {
            Intent intent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
            startActivity(intent);
            return;
        }
        if(v.getId() == btnEditProfile.getId()) {
//            Toast.makeText(this, "YYY", Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(EditProfileActivity.class);

        }

        if(v.getId() == R.id.btn_update_bio) {
            updateBio();
            findViewById(R.id.layout_bio).setVisibility(View.GONE);
            View current = getCurrentFocus();
            if (current != null) current.clearFocus();
        }
        if(v.getId() == R.id.btn_cancel_update_bio) {
            edtBio.setText(oldBioText);
            findViewById(R.id.layout_bio).setVisibility(View.GONE);
            View current = getCurrentFocus();
            if (current != null) current.clearFocus();
        }
        if (v.getId() == llFollowers.getId()) {
            Intent intent = new Intent(ProfileActivity.this, FollowListActivity.class);
            intent.putExtra("pageIndex", 1);

            startActivity(intent);
        }
        if (v.getId() == llFollowing.getId()) {
            Intent intent = new Intent(ProfileActivity.this, FollowListActivity.class);
            intent.putExtra("pageIndex", 0);

            startActivity(intent);
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

        imvAvatarInSharedPlace.setImageBitmap(bitmap);

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
            if(user.getPhoneNumber() == null)
            {
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mGoogleSignInClient.signOut();
            }

            Intent intent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
            startActivity(intent);

            finish();
        }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(ProfileActivity.this, cls);

        startActivity(intent);


    }

    @Override
    protected void onResume() {
        super.onResume();

        StorageReference download = storageReference.child("/user_avatars").child(user.getUid().toString());

        Log.d("abc",download.toString());

        long MAX_BYTE = 1024*1024;
        download.getBytes(MAX_BYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        imvAvatarProfile.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Do nothing
                    }
                });
    }

    // NOTE (Quang): These buttons below belong to Setting and Privacy activity
//    public void privacyPage(View view) {
//        Intent intent = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
}