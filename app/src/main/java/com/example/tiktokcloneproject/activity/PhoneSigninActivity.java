package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PhoneSigninActivity extends FragmentActivity implements View.OnClickListener {
    private LinearLayout llSigninPage, llPhone;
    private RelativeLayout rlOtp;
    private EditText edtPhone, edtOtp;
    private ImageButton btnPhone, btnOtp;

    private final int VISIBLE = View.VISIBLE;
    private final int GONE = View.GONE;
    //////thread/////
    private String msg;
    Handler handler = new Handler();
    /////firebase///////
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private FirebaseFirestore db;
    Validator validator;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_signup);

        llSigninPage = (LinearLayout) findViewById(R.id.llSignupPage);
        llPhone = (LinearLayout) llSigninPage.findViewById(R.id.llPhone);
        rlOtp = (RelativeLayout) llSigninPage.findViewById(R.id.rlOtp);
        edtPhone = (EditText) llSigninPage.findViewById(R.id.edtPhone);
        edtOtp = (EditText) llSigninPage.findViewById(R.id.edtOtp);
        btnPhone = (ImageButton) llSigninPage.findViewById(R.id.btnPhone);
        btnOtp = (ImageButton) llSigninPage.findViewById(R.id.btnOtp);

        validator = Validator.getInstance();

        setVisibleVisibility(llPhone.getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.dialog_progress);
        dialog = builder.create();

        db = FirebaseFirestore.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        //////callbacks//////
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(code != null) {
                    edtOtp.setText(code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneSigninActivity.this, getString(R.string.error_verify), Toast.LENGTH_SHORT).show();
               dialog.dismiss();
                setVisibleVisibility(llPhone.getId());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Toast.makeText(PhoneSigninActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                setVisibleVisibility(rlOtp.getId());
                mVerificationId = verificationId;
                mResendToken = token;

            }
        }; // end callbacks


        btnPhone.setOnClickListener(this);
        btnOtp.setOnClickListener(this);
    }//on create

    public void onClick(View v) {

        if(v.getId() == btnPhone.getId()) {
            handleBtnPhoneClick();
        }
        if(v.getId() == btnOtp.getId()) {
            if(TextUtils.isEmpty(edtOtp.getText().toString()) || edtOtp.getText().toString().length() != 6) {
                Toast.makeText(PhoneSigninActivity.this, getString(R.string.error_Otp), Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edtOtp.getText().toString());
                dialog.show();
                signInWithPhoneAuthCredential(credential);
            }
        }
    }

    private void signIn() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(edtPhone.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(PhoneSigninActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setVisibleVisibility(int id) {
        rlOtp.setVisibility(GONE);
        llPhone.setVisibility(GONE);

        findViewById(id).setVisibility(VISIBLE);
    }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(PhoneSigninActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(PhoneSigninActivity.this, getString(R.string.successful_signin), Toast.LENGTH_SHORT).show();
                            moveToAnotherActivity(HomeScreenActivity.class);

                        } else {
                            dialog.dismiss();
                            Toast.makeText(PhoneSigninActivity.this, getString(R.string.error_verify), Toast.LENGTH_SHORT).show();
                            setVisibleVisibility(llPhone.getId());

                        }

                    }
                });
    }


    private void handleBtnPhoneClick() {

        String phone = edtPhone.getText().toString();
        if(!validator.isValidPhone(phone)) {
            Toast.makeText(PhoneSigninActivity.this, getString(R.string.error_PhoneAuth), Toast.LENGTH_SHORT).show();
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
                            dialog.dismiss();
                            Toast.makeText(PhoneSigninActivity.this, getString(R.string.error_signin, phone), Toast.LENGTH_SHORT).show();
                            setVisibleVisibility(llPhone.getId());
                        } else {
                            handler.post(this::signIn);
                        }
                    });
        }
    }

}