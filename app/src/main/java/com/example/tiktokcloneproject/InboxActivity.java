package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tiktokcloneproject.adapters.NotificationAdapter;
import com.example.tiktokcloneproject.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InboxActivity extends Activity {
    private final String TAG = "InboxActivity";
    private DatabaseReference mDatabase = null;
    private FirebaseUser user;
    private ListView lvNotifications;
    private ArrayList<Notification> notifications;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        lvNotifications = (ListView) findViewById(R.id.lvNotifications);
        notifications = new ArrayList<>();
        ArrayAdapter<Notification> adapter = new NotificationAdapter(
                this,
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
    }
}