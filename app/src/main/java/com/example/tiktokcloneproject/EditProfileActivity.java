package com.example.tiktokcloneproject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;




public class EditProfileActivity extends Activity implements View.OnClickListener {
    private EditText edtName, edtUsername, edtPhone, edtEmail, edtBirthdate;
    private Button btnEdit, btnPhoto, btnApply;
    private LinearLayout llEditProfile;
    private FirebaseFirestore db;
    private Validator validator;
    private Uri avatarUri;
    private final int SELECT_IMAGE_CODE = 10;
    private ProgressBar progressbar;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    FirebaseUser user;

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


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        llEditProfile = (LinearLayout) findViewById(R.id.llEditProfile);
        edtName = (EditText) llEditProfile.findViewById(R.id.edtName);
        edtUsername = (EditText) llEditProfile.findViewById(R.id.edtUsername);
        edtPhone = (EditText) llEditProfile.findViewById(R.id.edtPhone);
        edtEmail = (EditText) llEditProfile.findViewById(R.id.edtEmail);
        edtBirthdate = (EditText) llEditProfile.findViewById(R.id.edtBirthdate);
        btnEdit = (Button) llEditProfile.findViewById(R.id.btnEditProfile);
        btnPhoto = (Button) llEditProfile.findViewById(R.id.btnPhoto);
        btnApply = (Button) llEditProfile.findViewById(R.id.btnApply);
        progressbar = findViewById(R.id.progressBar);

        validator = Validator.getInstance();
        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        btnEdit.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);

        setOnTextChanged();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressbar.setVisibility(View.VISIBLE);

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
        progressbar.setVisibility(View.GONE);


    }//on create

    private String getData(Object data) {
        return data == null ? "" : data.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            ((GlobalVariable) this.getApplication()).setAvatarUri(data.getData());
            avatarUri = data.getData();
            uploadAvatar();
        }
    }

    private void uploadAvatar() {

        ProgressDialog progress = new ProgressDialog(EditProfileActivity.this);
        progress.setTitle("Loading");
        progress.setMessage("Please Wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        StorageReference upload = storageReference.child(user.getUid().toString());

        upload.putFile(avatarUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progress.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Image Failed", Toast.LENGTH_SHORT).show();
                    }
                });

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
            setOnTextChanged();
        }

        if (v.getId() == btnPhoto.getId()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);

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
