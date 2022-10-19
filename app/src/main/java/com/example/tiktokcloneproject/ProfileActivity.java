package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends Activity {
    String userId = "";
    TextView following, followers, likes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userId = getIntent().getStringExtra("id");

        setContentView(R.layout.activity_profile);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("profiles").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                following = (TextView)findViewById(R.id.following);
                followers = (TextView)findViewById(R.id.followers);
                likes = (TextView)findViewById(R.id.likes);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        following.setText(((Long)document.get("following")).toString());
                        followers.setText(((Long)document.get("followers")).toString());
                        likes.setText(((Long)document.get("totalLikes")).toString());
                    } else {
                    }
                } else {

                }
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
//    public void signOut(View v)
//    {
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(ProfileActivity.this, HomeScreenActivity.class);
//        startActivity(intent);
//
//        finish();
//    }
}