package com.example.tiktokcloneproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends Activity implements View.OnClickListener {
    private EditText edtName, edtUsername, edtPhone, edtEmail, edtBirthdate;
    private Button btnEdit, btnPhoto, btnApply;
    private LinearLayout llEditProfile;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        llEditProfile=(LinearLayout) findViewById(R.id.llEditProfile);
        edtName =(EditText) llEditProfile.findViewById(R.id.edtName);
        edtUsername =(EditText) llEditProfile.findViewById(R.id.edtUsername);
        edtPhone =(EditText) llEditProfile.findViewById(R.id.edtPhone);
        edtEmail =(EditText) llEditProfile.findViewById(R.id.edtEmail);
        edtBirthdate =(EditText) llEditProfile.findViewById(R.id.edtBirthdate);
        btnEdit = (Button) llEditProfile.findViewById(R.id.btnEditProfile);
        btnPhoto = (Button) llEditProfile.findViewById(R.id.btnPhoto);
        btnApply = (Button) llEditProfile.findViewById(R.id.btnApply);

        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        btnEdit.setOnClickListener(this);

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
                            Toast.makeText(EditProfileActivity.this, "success", Toast.LENGTH_SHORT).show();
                            edtUsername.setText(getData(document.get("userName")));
                            edtPhone.setText(getData(document.get("phone")));
                            edtEmail.setText(getData(document.get("email")));
                            edtBirthdate.setText(getData(document.get("birthdate")));
                            Log.d(TAG, "DocumentSnapshot data: " + document.get("following"));
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

    @Override
    public void onClick(View v)
    {
        if (v.getId()==btnEdit.getId())
        {
            setEnableEdt(true);
            btnApply.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnPhoto.setVisibility(View.GONE);
        }
    }

    private void setEnableEdt(boolean value) {
        edtName.setEnabled(value);
        edtUsername.setEnabled(value);
        edtPhone.setEnabled(value);
        edtEmail.setEnabled(value);
        edtBirthdate.setEnabled(value);
    }
}
