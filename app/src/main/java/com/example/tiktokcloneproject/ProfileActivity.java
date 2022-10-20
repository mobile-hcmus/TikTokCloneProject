package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends Activity {
    private TextView txvFollowing, txvFollowers, txvLikes, txvUserName;
    private Button btn;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userId = getIntent().getStringExtra("id");
        setContentView(R.layout.activity_profile);
        txvFollowing = (TextView)findViewById(R.id.text_following);
        txvFollowers = (TextView)findViewById(R.id.text_followers);
        txvLikes = (TextView)findViewById(R.id.text_likes);
        txvUserName = (TextView)findViewById(R.id.txv_username);

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
    }

    public void onClick(View v) {
        if (v.getId() == R.id.text_sign_out) {
            signOut(v);
        }
    }

    public void signOut(View v)
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
        startActivity(intent);

        finish();
    }


    // NOTE (Quang): These buttons below belong to Setting and Privacy activity
//    public void privacyPage(View view) {
//        Intent intent = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
}