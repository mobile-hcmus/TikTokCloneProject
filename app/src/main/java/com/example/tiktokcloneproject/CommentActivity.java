package com.example.tiktokcloneproject;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

public class CommentActivity extends Activity implements View.OnClickListener{
    private ImageView imvBack, imvMyAvatarInComment;
    private LinearLayout llComment;
    private EditText edtComment;
    private ImageButton imbSendComment;
    private String videoId, userId, avatarName;
    private Uri avatarUri;
    private Bitmap bitmap;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef, imagesRef;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        llComment = (LinearLayout) findViewById(R.id.llComment);
        imvBack = (ImageView) llComment.findViewById(R.id.imvBackToHomeScreen);
        imvMyAvatarInComment = (ImageView)llComment.findViewById(R.id.imvMyAvatarInComment);
        edtComment = (EditText) llComment.findViewById(R.id.edtComment);
        imbSendComment = (ImageButton) llComment.findViewById(R.id.imbSendComment);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoId = bundle.getString("videoId");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        userId = user.getUid();


        if (user != null) {
//            docRef = db.collection("profiles").document(userId.toString());
//            docRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists())
//                    {
//                        try {
//                            avatarName = (String) document.get("avatarName");
//                        }catch(Exception e){
//                            //do nothing
//                        }
                        StorageReference download = storageRef.child("/user_avatars").child(userId.toString());
//                        StorageReference download = storageRef.child(userId.toString());
                        long MAX_BYTE = 1024*1024;
                        download.getBytes(MAX_BYTE)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                                        imvMyAvatarInComment.setImageBitmap(bitmap);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Do nothing
                                    }
                                });
//                    }
//                    else { }
//                }
//                else { }
//            });
        }
        else
        {
            Toast.makeText(this, "You have to sign in to comment.", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CommentActivity.this, HomeScreenActivity.class);
            startActivity(intent1);
        }



        imvBack.setOnClickListener(this);


    }


    @Override
    public void onClick(View v){
        if (v.getId() == imvBack.getId()){
            onBackPressed();
            finish();
        }
        if (v.getId() == imbSendComment.getId()){
            postComment();
        }
    }

    private void postComment() {
        String comment = edtComment.getText().toString().trim();
        if (TextUtils.isEmpty(comment))
        {
            Toast.makeText(this, "Comment is empty...", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());
    }


}
