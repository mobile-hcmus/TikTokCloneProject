package com.example.tiktokcloneproject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.adapters.UserAdapter;
import com.example.tiktokcloneproject.adapters.VideoSummaryAdapter;
import com.example.tiktokcloneproject.model.User;
import com.example.tiktokcloneproject.model.VideoSummary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends Activity {


     RecyclerView rcv_users;
     UserAdapter userAdapter;
     SearchView searchView;

     ArrayList<VideoSummary> videoSummaries;
     ArrayList<String> videoIds;
     VideoSummaryAdapter videoSummaryAdapter;
     RecyclerView rcvVideoSummary;
     Handler handler = new Handler();

    final String TAG = "SearchActivity";
     ArrayList <User> userArrayList=new ArrayList<User>();;


     FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        userArrayList.clear();
        db = FirebaseFirestore.getInstance();



        rcv_users=(RecyclerView) findViewById(R.id.rcv_users);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        rcv_users.setLayoutManager(linearLayoutManager);

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
                        userAdapter.notifyDataSetChanged();
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
//        userDB.collection("users").orderBy("userName", Query.Direction.ASCENDING)
//                .whereEqualTo("userName", true)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null){
//                    Toast.makeText(SearchActivity.this,"Loi ket noi voi Server!",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                };
//
//                Toast.makeText(SearchActivity.this, value.getDocuments().toString(),
//                        Toast.LENGTH_LONG).show();
//
//                for (DocumentChange dc : value.getDocumentChanges()){
//                        userArrayList.add(dc.getDocument().toObject(User.class));
//
//                }
//                userAdapter.notifyDataSetChanged();
//
//            }
//        });


        db.collection("users")
                .orderBy("userName")
                .startAt(key)
                .endAt(key+"\uf8ff")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                userArrayList.add(new User(document.getString("userId"),document.getString("userName")));

                            };
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(SearchActivity.this,"Loi ket noi voi Server!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });



    }





    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();

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