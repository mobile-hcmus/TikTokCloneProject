package com.example.tiktokcloneproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends Activity{
    private TextView tvName, tvUsername, tvPhone, tvEmail, tvBirthdate;
    private Button btnEdit, btnPhoto;
    private LinearLayout llEditProfile;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        llEditProfile=(LinearLayout) findViewById(R.id.llEditProfile);
        tvName=(TextView) llEditProfile.findViewById(R.id.tvName);
        tvUsername=(TextView) llEditProfile.findViewById(R.id.tvUsername);
        tvPhone=(TextView) llEditProfile.findViewById(R.id.tvPhone);
        tvEmail=(TextView) llEditProfile.findViewById(R.id.tvEmail);
        tvBirthdate=(TextView) llEditProfile.findViewById(R.id.tvBirthdate);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String TAG = "LOG";
            DocumentReference docRef = db.collection("users").document(user.getUid().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            tvUsername.setText(getData(document.get("userName")));
                            tvPhone.setText(getData(document.get("phone")));
                            tvEmail.setText(getData(document.get("email")));
                            tvBirthdate.setText(getData(document.get("birthdate")));
//                            Log.d(TAG, "DocumentSnapshot data: " + document.get("following"));
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }//on create

    private String getData(Object data) {
        return data == null ? "" : data.toString();
    }
}
