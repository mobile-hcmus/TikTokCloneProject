package com.example.tiktokcloneproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.WrapContentLinearLayoutManager;
import com.example.tiktokcloneproject.activity.HomeScreenActivity;
import com.example.tiktokcloneproject.activity.SearchActivity;
import com.example.tiktokcloneproject.adapters.NotificationAdapter;
import com.example.tiktokcloneproject.adapters.UserAdapter;
import com.example.tiktokcloneproject.adapters.VideoSummaryAdapter;
import com.example.tiktokcloneproject.model.Notification;
import com.example.tiktokcloneproject.model.User;
import com.example.tiktokcloneproject.model.VideoSummary;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener {
    private Context context = null;
    private final String TAG = "SearchFragment";
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

    ArrayList <User> userArrayList=new ArrayList<User>();;


    FirebaseFirestore db;

    public static SearchFragment newInstance(String strArg) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("name", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// inflate res/layout_blue.xml to make GUI holding a TextView and a ListView
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.activity_searching, null);
        userArrayList.clear();
        db = FirebaseFirestore.getInstance();

        tvSubmitSearch = (TextView) layout.findViewById(R.id.tvSubmitSearch);


        rcv_users=(RecyclerView) layout.findViewById(R.id.rcv_users);
        rcv_users.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        String key="";

//        getData(key);
        userAdapter=new UserAdapter(context,userArrayList);
        rcv_users.setAdapter(userAdapter);
        RecyclerView.ItemDecoration itemDecoration= new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        rcv_users.addItemDecoration(itemDecoration);

        videoIds = new ArrayList<>();
        videoSummaries = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        rcvVideoSummary = (RecyclerView) layout.findViewById(R.id.rcvVideoSummary);
        videoSummaryAdapter = new VideoSummaryAdapter(context, videoSummaries);
        rcvVideoSummary.setLayoutManager(gridLayoutManager);
        rcvVideoSummary.addItemDecoration(new SearchFragment.GridSpacingItemDecoration(3, 10, true));
        rcvVideoSummary.setAdapter(videoSummaryAdapter);

        imbBackToHome.setOnClickListener(this);
        tvSubmitSearch.setOnClickListener(this);

        searchView = (SearchView) layout.findViewById(R.id.searchView);
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
        return layout;
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
                            Toast.makeText(context,"Loi ket noi voi Server!",
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
            Intent intent = new Intent(context, HomeScreenActivity.class);
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
