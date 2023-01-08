package com.example.tiktokcloneproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.WrapContentLinearLayoutManager;
import com.example.tiktokcloneproject.adapters.UserAdapter;
import com.example.tiktokcloneproject.adapters.VideoSummaryAdapter;
import com.example.tiktokcloneproject.model.User;
import com.example.tiktokcloneproject.model.VideoSummary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class SearchActivity extends Activity implements View.OnClickListener {

    final String USERNAME_LABEL = "username";
     RecyclerView rcv_users;


     UserAdapter userAdapter;
     SearchView searchView;

     ArrayList<VideoSummary> videoSummaries;
     ArrayList<String> videoIds;
     VideoSummaryAdapter videoSummaryAdapter;
     RecyclerView rcvVideoSummary;
     Handler handler = new Handler();

     ImageButton imbBackToHome;
     TextView tvSubmitSearch;

    final String TAG = "SearchActivity";
     ArrayList <User> userArrayList=new ArrayList<User>();;


     FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        userArrayList.clear();
        db = FirebaseFirestore.getInstance();

        tvSubmitSearch = (TextView) findViewById(R.id.tvSubmitSearch);


        rcv_users=(RecyclerView) findViewById(R.id.rcv_users);
        rcv_users.setLayoutManager(new WrapContentLinearLayoutManager(SearchActivity.this, LinearLayoutManager.VERTICAL, false));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        String key="";

//        getData(key);
        userAdapter=new UserAdapter(this,userArrayList);
        rcv_users.setAdapter(userAdapter);
        RecyclerView.ItemDecoration itemDecoration= new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        rcv_users.addItemDecoration(itemDecoration);

        videoIds = new ArrayList<>();
        videoSummaries = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rcvVideoSummary = (RecyclerView) findViewById(R.id.rcvVideoSummary);
        videoSummaryAdapter = new VideoSummaryAdapter(getApplicationContext(), videoSummaries);
        rcvVideoSummary.setLayoutManager(gridLayoutManager);
        rcvVideoSummary.addItemDecoration(new SearchActivity.GridSpacingItemDecoration(3, 10, true));
        rcvVideoSummary.setAdapter(videoSummaryAdapter);

        imbBackToHome.setOnClickListener(this);
        tvSubmitSearch.setOnClickListener(this);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                userAdapter.getFilter().filter(query);

//                userArrayList.clear();
//                if(query.startsWith("#"))
//                {
//                    setVideoSummaries(query);
//                }
//                else {
//                    getData(query);
//                    userAdapter.notifyDataSetChanged();
//                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(SearchActivity.this,newText,
//                        Toast.LENGTH_LONG).show();
//                userAdapter.getFilter().filter(newText);
//                userArrayList.clear();
                if(!newText.isEmpty()) {
                    if(newText.startsWith("#"))
                    {
                        setVideoSummaries(newText);
                    }
                    else {
                        getData(newText);
                    }
                }
                else {
                    userArrayList.clear();
                    userAdapter.notifyDataSetChanged();
                    videoSummaries.clear();
                    videoSummaryAdapter.notifyDataSetChanged();
                }


                return false;
            }
        });


    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (userAdapter!=null){
            userAdapter.release();
        }
    }

    private void getData(String key) {
        userArrayList.clear();
        db.collection("users")
                .orderBy(USERNAME_LABEL)
                .startAt(key)
                .endAt(key+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userArrayList.add(new User(document.getString("userId"),document.getString(USERNAME_LABEL)));
                                userAdapter.notifyItemInserted(userArrayList.size() - 1);
                            };

                        } else {
                            Toast.makeText(SearchActivity.this,"Loi ket noi voi Server!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean checkIsFollowing(ArrayList<String> userIDFollowingList, String anotherUserID) {
        return userIDFollowingList.contains(anotherUserID);
    }

    private void setVideoSummaries(String hashtag) {

            db.collection("hashtags").document(hashtag).collection("video_summaries")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    videoSummaries.add(document.toObject(VideoSummary.class));
                                    Log.d(TAG, document.getData() + "");
                                    videoSummaryAdapter.notifyItemInserted(videoSummaries.size() - 1);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == imbBackToHome.getId()) {
            Intent intent = new Intent(SearchActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if(view.getId() == tvSubmitSearch.getId()) {
            searchView.clearFocus();
        }
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
}