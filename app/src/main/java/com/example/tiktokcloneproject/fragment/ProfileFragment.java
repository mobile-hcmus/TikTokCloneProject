package com.example.tiktokcloneproject.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.activity.EditProfileActivity;
import com.example.tiktokcloneproject.activity.FollowListActivity;
import com.example.tiktokcloneproject.activity.FullScreenAvatarActivity;
import com.example.tiktokcloneproject.activity.HomeScreenActivity;
import com.example.tiktokcloneproject.activity.MainActivity;
import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.activity.SettingsAndPrivacyActivity;
import com.example.tiktokcloneproject.activity.SigninChoiceActivity;
import com.example.tiktokcloneproject.activity.SignupChoiceActivity;
import com.example.tiktokcloneproject.adapters.VideoSummaryAdapter;
import com.example.tiktokcloneproject.helper.StaticVariable;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    final String USERNAME_LABEL = "username";
    private Context context = null;
    private TextView txvFollowing, txvFollowers, txvLikes, txvUserName, txvMenu;
    private EditText edtBio;
    private Button btn, btnEditProfile, btnUpdateBio, btnCancelUpdateBio;
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
    String oldBioText, currentUserID;
    String TAG="test";
    RecyclerView recVideoSummary;
    ArrayList<VideoSummary> videoSummaries;
    LinearLayout layout;
    int totalLikes = 0;
    public static ProfileFragment newInstance(String strArg,  String profileLinkId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("name", strArg);
        args.putString("id", profileLinkId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle idBundle = getArguments();
        userId = idBundle.getString("id");
        try {
            context = getActivity(); // use this reference to invoke main callbacks
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException();
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (userId == "") {
            if (user != null) {
                userId = user.getUid();
//                Toast.makeText(getActivity().getApplicationContext(), "id: " + userId, Toast.LENGTH_SHORT).show();
            } else {
//                Intent intent = new Intent(context, SignupChoiceActivity.class);
//                startActivity(intent);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// inflate res/layout_blue.xml to make GUI holding a TextView and a ListView
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_profile, null);

        txvFollowing = (TextView)layout.findViewById(R.id.text_following);
        txvFollowers = (TextView)layout.findViewById(R.id.text_followers);
        txvLikes = (TextView)layout.findViewById(R.id.text_likes);
        txvUserName = (TextView)layout.findViewById(R.id.txv_username);
        txvMenu = (TextView)layout.findViewById(R.id.text_menu);
        edtBio = (EditText)layout.findViewById(R.id.edt_bio);
        btnEditProfile =(Button)layout.findViewById(R.id.button_edit_profile);
        imvAvatarProfile = (ImageView) layout.findViewById(R.id.imvAvatarProfile);
        llFollowers = (LinearLayout) layout.findViewById(R.id.ll_followers);
        llFollowing = (LinearLayout) layout.findViewById(R.id.ll_following);
        recVideoSummary = (RecyclerView)layout.findViewById(R.id.recycle_view_video_summary);
        btnUpdateBio = (Button) layout.findViewById(R.id.btn_update_bio);
        btnCancelUpdateBio = (Button) layout.findViewById(R.id.btn_cancel_update_bio);

        btnUpdateBio.setOnClickListener(this);
        btnCancelUpdateBio.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
        txvMenu.setOnClickListener(this);
        imvAvatarProfile.setOnClickListener(this);
//        avatarUri = getIntent().getParcelableExtra("uri");

        imvAvatarProfile.setImageURI(avatarUri);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseFirestore.getInstance();
        setLikes(user.getUid());
        //set nút follow/edit profile
        if (user==null)
        {//chưa đăng nhập (vào profile thông qua search)
            handleFollow();
        }
        else
        {
            currentUserID=user.getUid();
            if (userId.equals(user.getUid()))
            {

                //vào profile của mình
                btn = (Button)layout.findViewById(R.id.button_edit_profile);
                edtBio.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);


                db  = FirebaseFirestore.getInstance();
                docRef = db.collection("profiles").document(userId);

                oldBioText = edtBio.getText().toString();
                edtBio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (b) {
                            layout.findViewById(R.id.layout_bio).setVisibility(View.VISIBLE);
                        } else {
                            layout.findViewById(R.id.layout_bio).setVisibility(View.GONE);
                        }
                    }
                });
                btnEditProfile.setOnClickListener(this);
            }
            else
            {//vào profile người khác
                handleFollow();

            }

        }



        videoSummaries = new ArrayList<VideoSummary>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recVideoSummary.setLayoutManager(gridLayoutManager);
        recVideoSummary.addItemDecoration(new ProfileFragment.GridSpacingItemDecoration(3, 10, true));
        setVideoSummaries();

        return layout;
    }



    private void handleFollow() {
        //bio cần set lại là text vỉew
        btn = (Button)layout.findViewById(R.id.button_follow);
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
                    txvUserName.setText("@" + document.getString(USERNAME_LABEL));
//                        oldBioText = document.getString("bio");
//                        edtBio.setText(oldBioText);

                } else { }
            } else { }
        });

        if (user !=null)
        {
            DocumentReference docRef = db.collection("profiles").document(currentUserID)
                    .collection("following").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            handleFollowed();


                        } else {
                            Log.d(TAG, "No such document");
                            handleUnfollowed();

                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }
        else
        {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentMain = new Intent(context, MainActivity.class);
                    startActivity(intentMain);
                }
            });
        }

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
                                        (Long)document.get("watchCount")));
                            }
                            if (videoSummaries.size() == 0) {
                                return;
                            }
                            VideoSummaryAdapter videoSummaryAdapter =new VideoSummaryAdapter(context, videoSummaries);
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

    @Override public void onStart() {

        super.onStart();
//        Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    txvFollowing.setText(((Long)document.get("following")).toString());
                    txvFollowers.setText(((Long)document.get("followers")).toString());
                    txvLikes.setText(((Long)document.get("likes")).toString());
                    txvUserName.setText("@" + document.getString(USERNAME_LABEL));
                    oldBioText = document.getString("bio");
                    edtBio.setText(oldBioText);

                } else { }
            } else { }
        });
    }

    void updateBio() {
        docRef.update("bio", edtBio.getText().toString());
        oldBioText = edtBio.getText().toString();
    }

    @Override
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
            Intent intent = new Intent(context, HomeScreenActivity.class);
            startActivity(intent);
            return;
        }
        if(v.getId() == btnEditProfile.getId()) {
//            Toast.makeText(this, "YYY", Toast.LENGTH_SHORT).show();
            moveToAnotherActivity(EditProfileActivity.class);

        }

        if(v.getId() == btnUpdateBio.getId()) {
            updateBio();
            getView().findViewById(R.id.layout_bio).setVisibility(View.GONE);
            View current = getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
            if (current != null) current.clearFocus();
        }
        if(v.getId() == btnCancelUpdateBio.getId()) {
            edtBio.setText(oldBioText);
            getView().findViewById(R.id.layout_bio).setVisibility(View.GONE);
            View current = getActivity().getCurrentFocus();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(current.getWindowToken(), 0);
            if (current != null) current.clearFocus();
        }
        if (v.getId() == llFollowers.getId()) {
            Intent intent = new Intent(context, FollowListActivity.class);
            intent.putExtra("pageIndex", 1);

            startActivity(intent);
        }
        if (v.getId() == llFollowing.getId()) {
            Intent intent = new Intent(context, FollowListActivity.class);
            intent.putExtra("pageIndex", 0);

            startActivity(intent);
        }

    }//on click
    private void showShareAccountDialog() {
        final Dialog dialog = new Dialog(context);
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
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("toptop-link", "http://toptoptoptop.com/" + user.getUid().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Profile link has been saved to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        imvAvatarInSharedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FullScreenAvatarActivity.class);
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
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout llSetting = dialog.findViewById(R.id.llSetting);
        LinearLayout llSignOut = dialog.findViewById(R.id.llSignOut);

        llSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SettingsAndPrivacyActivity.class);
                startActivity(intent);
            }
        });
        llSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut(view);

                getActivity().finish();
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

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            mGoogleSignInClient.signOut();
        }

        Intent intent = new Intent(context, HomeScreenActivity.class);
        startActivity(intent);

        getActivity().finish();
    }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(context, cls);

        startActivity(intent);


    }

    private void handleUnfollowed() {
        btn.setText("Follow");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "follow clicked");
                Map<String, Object> Data = new HashMap<>();
                Data.put("userID",userId);
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
                Data1.put("userID",currentUserID);
                Log.d(TAG,currentUserID);
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
                                Log.w(TAG, "follower fail",e);
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
    public void onResume() {
        super.onResume();

        //chinh lai avatar user.getUid().toString()
        StorageReference download = storageReference.child("/user_avatars").child(userId);


        download.getBytes(StaticVariable.MAX_BYTES_AVATAR)
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
        } catch(Exception exception) {
            Log.d("exception", exception.toString());
        }
    }

}
