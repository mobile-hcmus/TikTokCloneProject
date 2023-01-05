package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tiktokcloneproject.R;
import com.example.tiktokcloneproject.helper.Validator;
import com.example.tiktokcloneproject.model.Profile;
import com.example.tiktokcloneproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneSignupActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout llSignupPage, llPhone;
    RelativeLayout rlOtp;
    private EditText edtPhone, edtOtp;
    private ImageButton btnPhone, btnOtp;
    private final int VISIBLE = View.VISIBLE;
    private final int GONE = View.GONE;
    private Validator validator;
    ////////////firebase///////////////
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private FirebaseFirestore db;
    /////Thread//////////
    private String msg;
    Handler handler = new Handler();
    private Dialog dialog;
    private Uri avatarUri=Uri.parse("android.resource://com.example.tiktokcloneproject/drawable/default_avatar");

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_signup);

        llSignupPage = (LinearLayout) findViewById(R.id.llSignupPage);
        llPhone = (LinearLayout) llSignupPage.findViewById(R.id.llPhone);
        rlOtp = (RelativeLayout) llSignupPage.findViewById(R.id.rlOtp);
        edtPhone = (EditText) llSignupPage.findViewById(R.id.edtPhone);
        edtOtp = (EditText) llSignupPage.findViewById(R.id.edtOtp);
        btnPhone = (ImageButton) llSignupPage.findViewById(R.id.btnPhone);
        btnOtp = (ImageButton) llSignupPage.findViewById(R.id.btnOtp);

        validator = Validator.getInstance();


        setVisibleVisibility(llPhone.getId());

        db = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_progress);
        dialog = builder.create();



        mAuth = FirebaseAuth.getInstance();
        //////callbacks//////
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(code != null) {
                    dialog.show();
                    edtOtp.setText(code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(PhoneSignupActivity.this, getString(R.string.error_verify), Toast.LENGTH_SHORT).show();
                setVisibleVisibility(llPhone.getId());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Toast.makeText(PhoneSignupActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                setVisibleVisibility(rlOtp.getId());
                mVerificationId = verificationId;
                mResendToken = token;
                dialog.dismiss();
            }
        }; // end callbacks

        btnPhone.setOnClickListener(this);
        btnOtp.setOnClickListener(this);
    } // end onCreate

    @Override
    public void onClick(View v) {
        if(v.getId() == btnPhone.getId()) {
            handleBtnPhoneClick();
        }
        if(v.getId() == btnOtp.getId()) {
            if(TextUtils.isEmpty(edtOtp.getText().toString()) || edtOtp.getText().toString().length() != 6) {
                Toast.makeText(PhoneSignupActivity.this, getString(R.string.error_Otp), Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edtOtp.getText().toString());
                dialog.show();
                signInWithPhoneAuthCredential(credential);
            }
        }

    }

    private void signUp() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(edtPhone.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(PhoneSignupActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser firebaseUser = task.getResult().getUser();

                            String id = firebaseUser.getUid();
                            String username = id.substring(0, Math.min(id.length(), 6));
                            User user = new User(id, username, edtPhone.getText().toString(), "");
                            writeNewUser(user);
                            Profile profile = new Profile(id, username);
                            writeNewProfile(profile);
                            dialog.dismiss();
                            //upload default avatar


                            storage = FirebaseStorage.getInstance();
                            storageReference = storage.getReference();


                            StorageReference upload = storageReference.child("/user_avatars").child(firebaseUser.getUid().toString());
                            upload.putFile(avatarUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            Toast.makeText(PhoneSignupActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PhoneSignupActivity.this, "Image Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

















                            moveToAnotherActivity(HomeScreenActivity.class);

                        } else {
                            dialog.dismiss();
                            setVisibleVisibility(llPhone.getId());

                        }

                    }
                });
    }

    private void setVisibleVisibility(int id) {
        llPhone.setVisibility(GONE);
        rlOtp.setVisibility(GONE);

        findViewById(id).setVisibility(VISIBLE);
    }

    private void writeNewUser(User user) {

        // Basic sign-in info:
        Map<String, Object> userValues = user.toMap();
        final String TAG = "ADD";
        Map<String, Object> childUpdates = new HashMap<>();
        db.collection("users").document(user.getUserId())
                .set(userValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private void writeNewProfile(Profile profile) {

        // Basic sign-in info:
        Map<String, Object> userValues = profile.toMap();
        final String TAG = "ADD";
        Map<String, Object> childUpdates = new HashMap<>();
        db.collection("profiles").document(profile.getUserId())
                .set(userValues)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        Map<String, Object> Data1 = new HashMap<>();
        Data1.put("userID","dump");


        db.collection("profiles").document(profile.getUserId())
                .collection("following").document("dump")
                .set(Data1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });


        db.collection("profiles").document(profile.getUserId())
                .collection("followers").document("dump")
                .set(Data1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }


    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(PhoneSignupActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private void handleBtnPhoneClick() {

        String phone = edtPhone.getText().toString();
        if(!validator.isValidPhone(phone)) {
            Toast.makeText(this, getString(R.string.error_PhoneAuth, phone), Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            String formattedPhone = "+84" + phone.substring(phone.length() - 9);
            edtPhone.setText(formattedPhone);
            db.collection("users")
                    .whereEqualTo("phone", formattedPhone)
                    .get().addOnCompleteListener(task -> {
                        msg = "FALSE";
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    msg = "TRUE";
                                    break;
                                }
                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        if (msg.equals("FALSE")) {
                            handler.post(PhoneSignupActivity.this::signUp);
                        } else {
                            dialog.dismiss();
                            Toast.makeText(PhoneSignupActivity.this, getString(R.string.error_existedPhone), Toast.LENGTH_SHORT).show();
                            setVisibleVisibility(llPhone.getId());
                        }
                    });
        }
    }


}// end PhoneLoginActivity class