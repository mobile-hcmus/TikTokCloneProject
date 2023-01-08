package com.example.tiktokcloneproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.adapters.NotificationAdapter;
import com.example.tiktokcloneproject.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class InboxFragment extends Fragment implements View.OnClickListener {
    private Context context = null;
    private final String TAG = "InboxActivity";
    private DatabaseReference mDatabase = null;
    private FirebaseUser user;
    private ListView lvNotifications;
    private ArrayList<Notification> notifications;

    public static InboxFragment newInstance(String strArg) {
        InboxFragment fragment = new InboxFragment();
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
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_inbox, null);
        lvNotifications = (ListView) layout.findViewById(R.id.lvNotifications);
        notifications = new ArrayList<>();
        ArrayAdapter<Notification> adapter = new NotificationAdapter(
                context,
                R.layout.notification_row,
                notifications);
        lvNotifications.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                // A new comment has been added, add it to the displayed list
                Notification notification = dataSnapshot.getValue(Notification.class);
//                Toast.makeText(InboxActivity.this, notification.getTimestamp() + "", Toast.LENGTH_SHORT).show();


                adapter.insert(notification, 0);
                layout.findViewById(R.id.blank_notification).setVisibility(View.GONE);


                // ...
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        mDatabase.child(user.getUid()).addChildEventListener(childEventListener);
        return layout;
    }

    @Override public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View view) {


    }//on click


}
