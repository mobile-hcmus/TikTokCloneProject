package com.example.tiktokcloneproject;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends Activity implements View.OnClickListener {
    private EditText edtName, edtUsername, edtPhone, edtEmail, edtBirthdate;
    private Button btnEdit, btnPhoto, btnApply;
    private LinearLayout llEditProfile;
    private FirebaseFirestore db;
    private Validator validator;

    enum Flag {
        USERNAME,
        EMAIL,
        PHONE,
        BIRTHDATE
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        llEditProfile = (LinearLayout) findViewById(R.id.llEditProfile);
        edtName = (EditText) llEditProfile.findViewById(R.id.edtName);
        edtUsername = (EditText) llEditProfile.findViewById(R.id.edtUsername);
        edtPhone = (EditText) llEditProfile.findViewById(R.id.edtPhone);
        edtEmail = (EditText) llEditProfile.findViewById(R.id.edtEmail);
        edtBirthdate = (EditText) llEditProfile.findViewById(R.id.edtBirthdate);
        btnEdit = (Button) llEditProfile.findViewById(R.id.btnEditProfile);
        btnPhoto = (Button) llEditProfile.findViewById(R.id.btnPhoto);
        btnApply = (Button) llEditProfile.findViewById(R.id.btnApply);

        validator = Validator.getInstance();
        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        btnEdit.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);

        setOnTextChanged();
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
    public void onClick(View v) {
        if (v.getId() == btnEdit.getId()) {
            setEnableEdt(true);
            btnApply.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnPhoto.setVisibility(View.GONE);
        }
        if (v.getId() == btnApply.getId()) {
//            setOnTextChanged(edt);
//            if (!validator.isValidUsername(edtUsername.getText().toString())){
//                Toast.makeText(this, "Invalid, please try again.", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                //do nothing
            setOnTextChanged();


        }
    }//on click

    private void setEnableEdt(boolean value) {
        edtName.setEnabled(value);
        edtUsername.setEnabled(value);
        edtPhone.setEnabled(value);
        edtEmail.setEnabled(value);
        edtBirthdate.setEnabled(value);
    }

    private void setOnTextChanged() {
        edtUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!validator.isValidUsername(charSequence.toString())) {
                    edtUsername.setTextColor(getResources().getColor(R.color.tiktok_red));
                } else {
                    edtUsername.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!validator.isValidPhone(charSequence.toString())) {
                    edtPhone.setTextColor(getResources().getColor(R.color.tiktok_red));
                } else {
                    edtPhone.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!validator.isValidEmail(charSequence.toString())) {
                    edtEmail.setTextColor(getResources().getColor(R.color.tiktok_red));
                }
                else
                {
                    edtEmail.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtBirthdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!validator.isValidBirthdate(charSequence.toString())) {
                    edtBirthdate.setTextColor(getResources().getColor(R.color.tiktok_red));
                }
                else
                {
                    edtBirthdate.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}
