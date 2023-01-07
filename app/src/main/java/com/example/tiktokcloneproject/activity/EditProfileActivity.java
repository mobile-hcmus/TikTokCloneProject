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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.helper.GlobalVariable;
import com.example.tiktokcloneproject.helper.StaticVariable;
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
    private TextView tvUsername, tvPhone, tvEmail, tvBirthdate;
    private ImageButton imbPhoto, imbSelect, imbUsername, imbBirthdate;
    private LinearLayout llEditProfile, llChangePhoto, llPhone, llEmail;
    private FirebaseFirestore db;
    private Uri avatarUri;
    private final int SELECT_IMAGE_CODE = 10;
    private ImageView imvBackToProfile;
    private Dialog dialog;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private final String TAG = "EditProfileActivity";

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        llEditProfile = (LinearLayout) findViewById(R.id.llEditProfile);
        llChangePhoto = (LinearLayout) llEditProfile.findViewById(R.id.llChangePhoto);
        llPhone = (LinearLayout) llEditProfile.findViewById(R.id.llPhone);
        llEmail = (LinearLayout) llEditProfile.findViewById(R.id.llEmail);
        tvUsername = (TextView) llEditProfile.findViewById(R.id.tvUsername);
        tvPhone = (TextView) llEditProfile.findViewById(R.id.tvPhone);
        tvEmail = (TextView) llEditProfile.findViewById(R.id.tvEmail);
        tvBirthdate = (TextView) llEditProfile.findViewById(R.id.tvBirthdate);
        imbPhoto = (ImageButton) llEditProfile.findViewById(R.id.imbPhoto);
        imbSelect = (ImageButton) llEditProfile.findViewById(R.id.imbSelect);
        imbUsername = (ImageButton) llEditProfile.findViewById(R.id.imbUsername);
        imbBirthdate = (ImageButton) llEditProfile.findViewById(R.id.imbBirthdate);
        imvBackToProfile = (ImageView) findViewById(R.id.imvBackToProfile);

        imbSelect.setVisibility(View.GONE);


        imbPhoto.setOnClickListener(this);
        imbSelect.setOnClickListener(this);
        imvBackToProfile.setOnClickListener(this);
        imbUsername.setOnClickListener(this);
        imbBirthdate.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_progress);
        dialog = builder.create();
        dialog.show();



    }//on create

    @Override
    protected void onStart() {
        super.onStart();

        if (user != null) {
            if (user.getPhoneNumber().isEmpty()) {
                llPhone.setVisibility(View.GONE);
                llEmail.setVisibility(View.VISIBLE);
            } else {
                llPhone.setVisibility(View.VISIBLE);
                llEmail.setVisibility(View.GONE);
            }
            DocumentReference docRef = db.collection("users").document(user.getUid().toString());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
//                            Toast.makeText(EditProfileActivity.this, "success", Toast.LENGTH_SHORT).show();
                            tvUsername.setText(getData(document.get("username")));
                            tvPhone.setText(getData(document.get("phone")));
                            tvEmail.setText(getData(document.get("email")));
                            tvBirthdate.setText(getData(document.get("birthdate")));
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
    }

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

        if (v.getId() == imbPhoto.getId()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        }
        if (v.getId() == imvBackToProfile.getId()){

            finish();
        }

        if(v.getId() == imbUsername.getId()) {
            moveToEdit(StaticVariable.USERNAME, tvUsername.getText().toString());
        }

        if(v.getId() == imbBirthdate.getId()) {
            moveToEdit(StaticVariable.BIRTHDATE, tvBirthdate.getText().toString());
        }
    }//on click

    private void moveToEdit(String mode, String content) {
        Intent intent = new Intent(this, EditActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("mode", mode);
        bundle.putString("content", content);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }
}
