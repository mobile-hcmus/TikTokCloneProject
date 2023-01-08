package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.VideoSummaryAdapter;
import com.example.tiktokcloneproject.helper.StaticVariable;
import com.example.tiktokcloneproject.model.Notification;
import com.example.tiktokcloneproject.model.VideoSummary;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends FragmentActivity implements View.OnClickListener {
    final String USERNAME_LABEL = "username";
    private TextView txvFollowing, txvFollowers, txvLikes, txvUserName;
    private EditText edtBio;
    private Button btn, btnEditProfile, btnUpdateBio, btnCancelUpdateBio;
    private LinearLayout llFollowing, llFollowers, llInfor;
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
    String oldBioText, currentUserID;
    String TAG = "test";
    RecyclerView recVideoSummary;
    ArrayList<VideoSummary> videoSummaries;
    private int totalLikes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
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
            userId = user.getUid();

        }
        setContentView(R.layout.activity_profile);
        txvFollowing = (TextView) findViewById(R.id.text_following);
        txvFollowers = (TextView) findViewById(R.id.text_followers);
        txvLikes = (TextView) findViewById(R.id.text_likes);
        txvUserName = (TextView) findViewById(R.id.txv_username);
        edtBio = (EditText) findViewById(R.id.edt_bio);
        btnEditProfile = (Button) findViewById(R.id.button_edit_profile);
        imvAvatarProfile = (ImageView) findViewById(R.id.imvAvatarProfile);
        llFollowers = (LinearLayout) findViewById(R.id.ll_followers);
        llFollowing = (LinearLayout) findViewById(R.id.ll_following);
        llInfor = (LinearLayout) findViewById(R.id.info);

        recVideoSummary = (RecyclerView) findViewById(R.id.recycle_view_video_summary);
        btnUpdateBio = (Button) findViewById(R.id.btn_update_bio);
        btnCancelUpdateBio = (Button) findViewById(R.id.btn_cancel_update_bio);

        btnUpdateBio.setOnClickListener(this);
        btnCancelUpdateBio.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
//        avatarUri = getIntent().getParcelableExtra("uri");

        imvAvatarProfile.setImageURI(avatarUri);

        db = FirebaseFirestore.getInstance();
        setLikes(userId);
        docRef = db.collection("profiles").document(userId);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //set nút follow/edit profile
        if (user == null) {//chưa đăng nhập (vào profile thông qua search)
            handleFollow();
        } else {
            currentUserID = user.getUid();
            if (userId.equals(user.getUid())) {

                //vào profile của mình
                btn = (Button) findViewById(R.id.button_edit_profile);
                edtBio.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);


//                db  = FirebaseFirestore.getInstance();
//                docRef = db.collection("profiles").document(userId);
//                docRef.get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            txvFollowing.setText(((Long)document.get("following")).toString());
//                            txvFollowers.setText(((Long)document.get("followers")).toString());
//                            txvLikes.setText(((Long)document.get("likes")).toString());
//                            txvUserName.setText("@" + document.getString(USERNAME_LABEL));
//                            Log.d("name123","vao1");
//
//                            oldBioText = document.getString("bio");
//                            edtBio.setText(oldBioText);
//
//                        } else { }
//                    } else { }
//                });
                oldBioText = edtBio.getText().toString();

                edtBio.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence == "⠀") {
                            edtBio.setText("");
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence == "") {
                            edtBio.setText("⠀");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
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
            } else {//vào profile người khác
                handleFollow();

            }

        }


        videoSummaries = new ArrayList<VideoSummary>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recVideoSummary.setLayoutManager(gridLayoutManager);
        recVideoSummary.addItemDecoration(new GridSpacingItemDecoration(3, 10, true));
        setVideoSummaries();
    }//on create

    boolean isFollowed = false;

    @Override
    public void onStart() {
        super.onStart();
        Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    txvFollowing.setText(((Long)document.get("following")).toString());
                    txvFollowers.setText(((Long)document.get("followers")).toString());
                    txvLikes.setText(((Long)document.get("likes")).toString());
                    txvUserName.setText("@" + document.getString(USERNAME_LABEL));
//                        oldBioText = document.getString("bio");
//                        edtBio.setText(oldBioText);

                } else { }
            } else { }
        });
    }

    private void handleFollow() {
        //bio cần set lại là text vỉew
        btn = (Button) findViewById(R.id.button_follow);
        btn.setVisibility(View.VISIBLE);

        if (user != null) {
            DocumentReference docRef = db.collection("profiles").document(currentUserID)
                    .collection("following").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            isFollowed = true;
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            handleFollowed();
                            notifyFollow();

                        } else {
                            Log.d(TAG, "No such document");
                            isFollowed = false;
                            handleUnfollowed();


                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        } else {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentMain = new Intent(ProfileActivity.this, MainActivity.class);

                    startActivity(intentMain);
                }
            });
        }

    }

    public void notifyFollow() {
        db.collection("users").document(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.get("username", String.class);
                                Notification.pushNotification(username, userId, StaticVariable.FOLLOW);
                                Log.d(ContentValues.TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(ContentValues.TAG, "No such document");
                            }
                        } else {
                            Log.d(ContentValues.TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    protected void setVideoSummaries() {
        db.collection("profiles").document(userId).collection("public_videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                videoSummaries.add(new VideoSummary(document.getString("videoId"),
                                        document.getString("thumbnailUri"),
                                        (Long) document.get("watchCount")));
                            }
                            if (videoSummaries.size() == 0) {
                                return;
                            }
                            VideoSummaryAdapter videoSummaryAdapter = new VideoSummaryAdapter(getApplicationContext(), videoSummaries);
                            recVideoSummary.setAdapter(videoSummaryAdapter);
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

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
        if (v.getId() == btnEditProfile.getId()) {
//            Toast.makeText(this, "YYY", Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(EditProfileActivity.class);
            finish();

        }
        if (v.getId() == R.id.btnBackProfile) {

            finish();

        }

        if (v.getId() == btnUpdateBio.getId()) {
            updateBio();
            findViewById(R.id.layout_bio).setVisibility(View.GONE);
            View current = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
            if (current != null) current.clearFocus();
        }
        if (v.getId() == btnCancelUpdateBio.getId()) {
            edtBio.setText(oldBioText);
            findViewById(R.id.layout_bio).setVisibility(View.GONE);
            View current = getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
            if (current != null) current.clearFocus();
        }
        if (v.getId() == llFollowers.getId()) {
            if (currentUserID == userId) {
                Intent intent = new Intent(ProfileActivity.this, FollowListActivity.class);
                intent.putExtra("pageIndex", 1);
                startActivity(intent);
            }
        }
        if (v.getId() == llFollowing.getId()) {

            if (currentUserID == userId) {
                Intent intent = new Intent(ProfileActivity.this, FollowListActivity.class);
                intent.putExtra("pageIndex", 0);
                startActivity(intent);
            }

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

        txvUsernameInSharedPlace.setText(txvUserName.getText());

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

    public void signOut(View v) {
        FirebaseAuth.getInstance().signOut();
        if (user.getPhoneNumber() == null) {
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


//    public Integer val;

//    private int readFollow(String id,String type)
//    {
//
//        DocumentReference docRef = db.collection("profiles").document(id);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        val= (Integer) document.get(type);
//
//                    } else {
//                        Log.d(TAG, "No such document");
//                        val=0;
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                    val=0;
//                }
//            }
//        });
//
//        return val;
//    }
//
//    private void writeFollow()
//    {
//
//    }

    private void handleUnfollowed() {
        btn.setText("Follow");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFollowed) return;
                isFollowed = true;

                Log.d(TAG, "follow clicked");
                Map<String, Object> Data = new HashMap<>();
                Data.put("userID", userId);
                //thêm following
                db.collection("profiles").document(currentUserID)
                        .collection("following").document(userId)
                        .set(Data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                db.collection("profiles").document(currentUserID)
                                        .update("following", FieldValue.increment(1));
                                docRef = db.collection("profiles").document(userId);
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();


                                            txvFollowing.setText(((Long) document.get("following")).toString());
                                            txvFollowers.setText(((Long) document.get("followers")).toString());
                                        if (document.exists()) {

                                        } else {
                                        }
                                    } else {
                                    }
                                });

                                handleFollowed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

                //thêm follower

                Map<String, Object> Data1 = new HashMap<>();
                Data1.put("userID", currentUserID);
                Log.d(TAG, currentUserID);
                db.collection("profiles").document(userId)
                        .collection("followers").document(currentUserID)
                        .set(Data1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                db.collection("profiles").document(userId)
                                        .update("followers", FieldValue.increment(1));
                                Log.d(TAG, "follower added");

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "follower fail", e);
                            }
                        });


            }
        });
    }

    private void handleFollowed() {
        btn.setText("Unfollow");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFollowed) return;
                isFollowed = false;
                Log.d(TAG, "unfollow clicked");


                //xóa following
                db.collection("profiles").document(currentUserID)
                        .collection("following").document(userId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                db.collection("profiles").document(currentUserID)
                                        .update("following", FieldValue.increment(-1));

                                docRef = db.collection("profiles").document(userId);
                                docRef.get().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();


                                            txvFollowing.setText(((Long) document.get("following")).toString());
                                            txvFollowers.setText(((Long) document.get("followers")).toString());
                                        if (document.exists()) {

                                        } else {
                                        }
                                    } else {
                                    }
                                });


                                handleUnfollowed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                //xóa follower
                db.collection("profiles").document(userId)
                        .collection("followers").document(currentUserID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("profiles").document(userId)
                                        .update("followers", FieldValue.increment(-1));
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //chinh lai avatar user.getUid().toString()
        StorageReference download = storageReference.child("/user_avatars").child(userId);


        download.getBytes(StaticVariable.MAX_BYTES_AVATAR)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
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

    public void setLikes(String userId) {
        try {
            db.collection("profiles").document(userId).collection("public_videos").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> userVideos = new ArrayList<String>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userVideos.add(document.getData().get("videoId").toString());
                        }
                        Log.d("Uservideo", userVideos.toString());

                        db.collection("likes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("Use", document.getId());

                                        if (userVideos.contains(document.getId())) {
                                            totalLikes += document.getData().size();
                                        }
                                    }
                                    txvLikes.setText("" + totalLikes);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        } catch (Exception exception) {
            Log.d("exception", exception.toString());
        }
    }
}