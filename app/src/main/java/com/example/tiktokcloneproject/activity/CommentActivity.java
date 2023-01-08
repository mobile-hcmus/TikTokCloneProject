package com.example.tiktokcloneproject.activity;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.CommentAdapter;
import com.example.tiktokcloneproject.helper.StaticVariable;
import com.example.tiktokcloneproject.model.Comment;
import com.example.tiktokcloneproject.model.Notification;
import com.example.tiktokcloneproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class CommentActivity extends Activity implements View.OnClickListener{
    private ImageView imvBack, imvMyAvatarInComment;
    private LinearLayout llComment;
    private EditText edtComment;
    private ImageButton imbSendComment;
    private String videoId, userId, avatarName;
    private Uri avatarUri;
    private Bitmap bitmap;
    private ListView lvComment;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageRef, imagesRef;
    DocumentReference docRef;
    String username;
    String authorVideoId;
    int totalComments;
    CommentAdapter adapter;

    Handler handler = new Handler();

    ArrayList<Comment> comments;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        llComment = (LinearLayout) findViewById(R.id.llComment);
        imvBack = (ImageView) llComment.findViewById(R.id.imvBackToHomeScreen);
        imvMyAvatarInComment = (ImageView)llComment.findViewById(R.id.imvMyAvatarInComment);
        edtComment = (EditText) llComment.findViewById(R.id.edtComment);
        imbSendComment = (ImageButton) llComment.findViewById(R.id.imbSendComment);
        lvComment = (ListView) llComment.findViewById(R.id.listViewComment);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videoId = bundle.getString("videoId");
        authorVideoId = bundle.getString("authorId");
        totalComments = bundle.getInt("totalComments");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        comments = new ArrayList<>();
        adapter = new CommentAdapter(this, R.layout.layout_row_comment, comments);
        lvComment.setAdapter(adapter);




        imvBack.setOnClickListener(this);
        imbSendComment.setOnClickListener(this);



        db.collection("users").document(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                username = document.get("username", String.class);
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        db.collection("comments")
                .whereEqualTo("videoId", videoId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    comments.add(0, dc.getDocument().toObject(Comment.class));
                                    adapter.notifyDataSetChanged();
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified city: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });

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
            userId = user.getUid();
            StorageReference download = storageRef.child("/user_avatars").child(userId.toString());
//                        StorageReference download = storageRef.child(userId.toString());

            download.getBytes(StaticVariable.MAX_BYTES_AVATAR)
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
//            Toast.makeText(this, "You have to sign in to comment.", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(CommentActivity.this, HomeScreenActivity.class);
            startActivity(intent1);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View v){
        if (v.getId() == imvBack.getId()){
            onBackPressed();
        }
        if (v.getId() == imbSendComment.getId()){
            String cmt = edtComment.getText().toString().trim();
            if (TextUtils.isEmpty(cmt))
            {
//                Toast.makeText(this, "Comment is empty...", Toast.LENGTH_SHORT).show();
                return;
            }
            String timeStamp = String.valueOf(System.currentTimeMillis());
            Comment comment = new Comment(timeStamp, videoId, userId, cmt);
            postComment(comment);
            edtComment.setText("");
        }
    }

    private void postComment(Comment comment ) {
        Map<String, Object> values = comment.toMap();

        db.collection("comments").document(comment.getCommentId()).set(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Notification.pushNotification(username, authorVideoId, StaticVariable.COMMENT);
                    handler.post(CommentActivity.this::updateTotal);
//                    Toast.makeText(CommentActivity.this, "Comment successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(CommentActivity.this, "Comment fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateTotal() {
        db.collection("videos").document(videoId)
                .update("totalComments", totalComments + 1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_out_bottom);
    }
}
