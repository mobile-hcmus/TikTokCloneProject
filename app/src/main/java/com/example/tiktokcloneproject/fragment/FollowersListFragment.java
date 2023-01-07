package com.example.tiktokcloneproject.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.FollowerAdapter;
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
 * Use the {@link FollowersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowersListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FollowersListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowersListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowersListFragment newInstance(String param1, String param2) {
        FollowersListFragment fragment = new FollowersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    TextView tvTest;

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
    ArrayList<String> followerList = new ArrayList<String>();
    ArrayList<String> followerUserNameList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_followers_list, container, false);
        ListView lvFollowers = (ListView) contentView.findViewById(R.id.lv_followers);

        //userArrayList.clear();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        followerList.clear();
        followerUserNameList.clear();


        try {
            db.collection("profiles").document(user.getUid()).collection("followers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getId().equals("dump"))
                            {}
                            else {
                                followerList.add(document.getId());
                            }
                        }
                        Log.d("followers", followerList.toString());
                        if (!followerList.isEmpty()) {
                            db.collection("users").whereIn(FieldPath.documentId(), followerList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document: task.getResult()) {
                                            followerUserNameList.add(document.get("username", String.class));
                                            Log.d("followersName", followerUserNameList.toString());
                                        }

                                        showList(contentView, lvFollowers);
                                    }
                                }
                            });
                        }
                    } else {
                        //Log.d("followers", "Error getting documents: ", task.getException());
                    }
                }
            });
        } catch (Exception exception) {
            // Do nothing
            Log.d("followers", exception.toString());
        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contentView.getContext(),
//                android.R.layout.simple_list_item_1,
//                userArrayList);
//
//        lvFollowers.setAdapter(adapter);

        return contentView;
    }

    String userName = "";
    public void getUserNameById(ArrayList<String> userIdList){
    }

    void showList(View contentView, ListView lvFollowers) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contentView.getContext(),
//                android.R.layout.simple_list_item_1,
//                followerUserNameList);
        if (followerList.isEmpty() && followerUserNameList.isEmpty()) {
            return;
        }


        FollowerAdapter followerAdapter = new FollowerAdapter(contentView.getContext(), followerList, followerUserNameList);
        lvFollowers.setAdapter(followerAdapter);
    }
}
