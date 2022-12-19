package com.example.tiktokcloneproject.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.helper.Validator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Locale;


public class EditProfileActivity extends Activity implements View.OnClickListener {
    private EditText edtName, edtUsername, edtPhone, edtEmail, edtBirthdate;
    private String Name, Username, Phone, Email, Birthdate;
    private Button btnEdit, btnApply;
    private ImageButton imbPhoto, imbSelect;
    private LinearLayout llEditProfile, llChangePhoto;
    private FirebaseFirestore db;
    private Validator validator;
    private Uri avatarUri;
    private final int SELECT_IMAGE_CODE = 10;
    private ImageView imvBackToProfile;
    private Dialog dialog;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    final Calendar myCalendar= Calendar.getInstance();
    private final String TAG = "EditProfileActivity";

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
        llChangePhoto = (LinearLayout) llEditProfile.findViewById(R.id.llChangePhoto);
        edtName = (EditText) llEditProfile.findViewById(R.id.edtName);
        edtUsername = (EditText) llEditProfile.findViewById(R.id.edtUsername);
        edtPhone = (EditText) llEditProfile.findViewById(R.id.edtPhone);
        edtEmail = (EditText) llEditProfile.findViewById(R.id.edtEmail);
        edtBirthdate = (EditText) llEditProfile.findViewById(R.id.edtBirthdate);
        btnEdit = (Button) llEditProfile.findViewById(R.id.btnEditProfile);
        imbPhoto = (ImageButton) llEditProfile.findViewById(R.id.imbPhoto);
        btnApply = (Button) llEditProfile.findViewById(R.id.btnApply);
        imbSelect = (ImageButton) llEditProfile.findViewById(R.id.imbSelect);
        imvBackToProfile = (ImageView) findViewById(R.id.imvBackToProfile);

        validator = Validator.getInstance();
        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        imbSelect.setVisibility(View.GONE);

        btnEdit.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        imbPhoto.setOnClickListener(this);
        imbSelect.setOnClickListener(this);
        imvBackToProfile.setOnClickListener(this);

        setOnTextChanged();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_progress);
        dialog = builder.create();
        dialog.show();

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
                            dialog.dismiss();
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

        StorageReference upload = storageReference.child("/user_avatars").child(user.getUid().toString());
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
            llChangePhoto.setVisibility(View.GONE);
            imbSelect.bringToFront();
            imbSelect.setVisibility(View.VISIBLE);

            Name = edtName.getText().toString();
            setEnableEdt(true);

        }
        if (v.getId() == btnApply.getId()) {
            setOnTextChanged();
            update(Name, Username, Phone, Email, Birthdate);
        }

        if (v.getId() == imbPhoto.getId()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        }
        if (v.getId() == imvBackToProfile.getId()){
            onBackPressed();
            finish();
        }
        if (v.getId() == imbSelect.getId()){
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
        HashMap<String, Object> newData = new HashMap<>();
        newData.put("userName", username);
        newData.put("phone", phone);
        newData.put("email", email);
        newData.put("birthdate", birthdate);

        DocumentReference userDoc = db.collection("users").document(user.getUid());
        DocumentReference profileDoc = db.collection("profiles").document(user.getUid());

       userDoc.update(newData)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.d(TAG, "DocumentSnapshot successfully updated!");
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w(TAG, "Error updating document", e);
                   }
               });
       profileDoc.update("username", username)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void aVoid) {
                       Log.d(TAG, "DocumentSnapshot successfully updated!");
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.w(TAG, "Error updating document", e);
                   }
               });;

        setEnableEdt(false);
        btnApply.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
        llChangePhoto.setVisibility(View.VISIBLE);
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
