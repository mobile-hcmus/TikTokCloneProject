package com.example.tiktokcloneproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Locale;


public class EditProfileActivity extends Activity implements View.OnClickListener {
    private EditText edtName, edtUsername, edtPhone, edtEmail, edtBirthdate;
    private String Name, Username, Phone, Email, Birthdate;
    private Button btnEdit, btnPhoto, btnApply, btnSelect;
    private LinearLayout llEditProfile;
    private FirebaseFirestore db;
    private Validator validator;
    private Uri avatarUri;
    private final int SELECT_IMAGE_CODE = 10;
    private ProgressBar progressbar;
    private ImageView imvBackToProfile;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    final Calendar myCalendar= Calendar.getInstance();

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
        btnSelect = (Button) llEditProfile.findViewById(R.id.btnSelect);
        imvBackToProfile = (ImageView) findViewById(R.id.imvBackToProfile);
        progressbar = findViewById(R.id.progressBar);

        validator = Validator.getInstance();
        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        btnSelect.setVisibility(View.GONE);

        btnEdit.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        btnPhoto.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        imvBackToProfile.setOnClickListener(this);

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
            btnApply.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
            btnPhoto.setVisibility(View.GONE);
            btnSelect.bringToFront();
            btnSelect.setVisibility(View.VISIBLE);

            Name = edtName.getText().toString();
            setEnableEdt(true);

        }
        if (v.getId() == btnApply.getId()) {
            setOnTextChanged();
            update(Name, Username, Phone, Email, Birthdate);
        }

        if (v.getId() == btnPhoto.getId()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        }
        if (v.getId() == imvBackToProfile.getId()){
            onBackPressed();
            finish();
        }
        if (v.getId() == btnSelect.getId()){
            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, month);
                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                    updateLabel();
                }
            };
            new DatePickerDialog(this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }//on click

    private void update(String name, String username, String phone, String email, String birthdate) {
        HashMap newData = new HashMap();
        newData.put("userName", username);
        newData.put("phone", phone);
        newData.put("email", email);
        newData.put("birthdate", birthdate);

        db.collection("users").document(user.getUid()).update(newData).addOnCompleteListener(this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Update successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update fail!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        btnPhoto.setVisibility(View.VISIBLE);
}

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
                    Username = edtUsername.getText().toString();
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
                    Phone = edtPhone.getText().toString();
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
                } else {
                    edtEmail.setTextColor(getResources().getColor(R.color.black));
                    Email = edtEmail.getText().toString();
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
                } else {
                    edtBirthdate.setTextColor(getResources().getColor(R.color.black));
                    Birthdate = edtBirthdate.getText().toString();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    private void updateLabel()
    {
        String myFormat="dd/MM/yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        edtBirthdate.setText(dateFormat.format(myCalendar.getTime()));
    };
}
