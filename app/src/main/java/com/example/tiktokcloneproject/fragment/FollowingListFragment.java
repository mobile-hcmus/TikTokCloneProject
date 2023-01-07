package com.example.tiktokcloneproject.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.FollowerAdapter;
import com.example.tiktokcloneproject.adapters.FollowingAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FollowingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingListFragment newInstance(String param1, String param2) {
        FollowingListFragment fragment = new FollowingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArrayList <String> userIdArrayList=new ArrayList<String>();
    ArrayList <String> userNameArrayList=new ArrayList<String>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> followingList = new ArrayList<String>();
    ArrayList<String> followingUserNameList = new ArrayList<String>();




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ArrayList <String> test=new ArrayList<String>();

        //get mang ve
        test.add("abc");








        View contentView = inflater.inflate(R.layout.fragment_following_list, container, false);
        ListView lvFollowing= (ListView) contentView.findViewById(R.id.lv_following);



        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        db.collection("profiles").document(user.getUid()).collection("following").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals("dump"))
                        {}
                        else {
                            followingList.add(document.getId());
                        }
//                        Log.d("followers", followerList.toString());
                    }
                    if (!followingList.isEmpty())
                    db.collection("users").whereIn(FieldPath.documentId(), followingList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document: task.getResult()) {
                                    followingUserNameList.add(document.get("username", String.class));
                                    Log.d("followers", followingUserNameList.toString());
                                }
                                //lvFollowing.setAdapter(new ArrayAdapter<>(contentView.getContext(), android.R.layout.simple_list_item_1,followingUserNameList));
                                showList(contentView, lvFollowing);

                            }
                        }
                    });




                } else {
                    String[] message = {"Không tìm thấy"};
                    lvFollowing.setAdapter(new ArrayAdapter<>(contentView.getContext(), android.R.layout.simple_list_item_1,message));
                    Log.d("following", "Error getting documents: ", task.getException());
                }
            }
        });

        return contentView;

    }

    void showList(View contentView, ListView lvFollowers) {

        FollowingAdapter followingAdapter = new FollowingAdapter(contentView.getContext(), followingList, followingUserNameList);
        lvFollowers.setAdapter(followingAdapter);
    }
}