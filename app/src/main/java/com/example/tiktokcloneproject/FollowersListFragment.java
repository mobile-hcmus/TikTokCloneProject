package com.example.tiktokcloneproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_followers_list, container, false);
        ListView lvFollowers = (ListView) contentView.findViewById(R.id.lv_followers);

        //userArrayList.clear();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            userIdArrayList = (ArrayList<String>)document.get("followers");
            getUserNameById(userIdArrayList);
            showList(contentView, lvFollowers);

        });


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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contentView.getContext(),
                android.R.layout.simple_list_item_1,
                userIdArrayList);

        lvFollowers.setAdapter(adapter);
    }
}
