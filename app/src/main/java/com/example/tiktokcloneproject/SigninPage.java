package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class SigninPage extends Activity implements View.OnClickListener {
    private LinearLayout llSigninPage, llChoice, llPhone, llWait, llOtp;
    private EditText edtPhone, edtPassword, edtOtp;
    private Button btnPhone, btnOtp, btnChoicePhone, btnChoiceEmail, btnChoiceFacebook, btnBackToHomeScreen,
            btnBackToChoice;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);

        llSigninPage = (LinearLayout) findViewById(R.id.llSigninPage);
        llPhone = (LinearLayout) llSigninPage.findViewById(R.id.llPhone);
        llChoice = (LinearLayout) llSigninPage.findViewById(R.id.llChoice);
        llOtp = (LinearLayout) llSigninPage.findViewById(R.id.llOtp);
        llWait = (LinearLayout) llSigninPage.findViewById(R.id.llWait);
        edtPhone = (EditText) llSigninPage.findViewById(R.id.edtPhone);
        edtOtp = (EditText) llSigninPage.findViewById(R.id.edtOtp);
        edtPassword = (EditText) llSigninPage.findViewById(R.id.edtPassword);
        btnPhone = (Button) llSigninPage.findViewById(R.id.btnPhone);
        btnOtp = (Button) llSigninPage.findViewById(R.id.btnOtp);
        btnChoicePhone = (Button) llSigninPage.findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) llSigninPage.findViewById(R.id.btnChoiceEmail);
        btnChoiceFacebook = (Button) llSigninPage.findViewById(R.id.btnChoiceFacebook);
        btnBackToHomeScreen = (Button) llSigninPage.findViewById(R.id.btnBackToHomeScreen);
        btnBackToChoice = (Button) llSigninPage.findViewById(R.id.btnBackToChoice);

        setVisibleVisibility(llChoice.getId());
        btnPhone.setText(getString(R.string.ic_btnLogin) + getString(R.string.ic_btnLogin));
        btnOtp.setText(getString(R.string.ic_btnLogin) + getString(R.string.ic_btnLogin));

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
                Toast.makeText(SigninPage.this, getString(R.string.error_verify), Toast.LENGTH_SHORT).show();
                setVisibleVisibility(llPhone.getId());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Toast.makeText(SigninPage.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                setVisibleVisibility(llOtp.getId());
                mVerificationId = verificationId;
                mResendToken = token;

            }
        }; // end callbacks


        btnChoicePhone.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
        btnOtp.setOnClickListener(this);
        btnBackToChoice.setOnClickListener(this);
        btnBackToHomeScreen.setOnClickListener(this);
    }//on create

    public void onClick(View v) {
        if(v.getId() == btnChoicePhone.getId()) {
            setVisibleVisibility(llPhone.getId());
        }
        if(v.getId() == btnBackToChoice.getId()) {
            setVisibleVisibility(llChoice.getId());
        }
        if(v.getId() == btnBackToHomeScreen.getId()) {
            moveToAnotherActivity(HomeScreenActivity.class);
        }
        if(v.getId() == btnPhone.getId()) {
           handleBtnPhoneClick();
        }
        if(v.getId() == btnOtp.getId()) {
            if(TextUtils.isEmpty(edtOtp.getText().toString()) || edtOtp.getText().toString().length() != 6) {
                Toast.makeText(SigninPage.this, getString(R.string.error_Otp), Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edtOtp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        }

    }

    private void signIn() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(edtPhone.getText().toString())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(SigninPage.this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void setVisibleVisibility(int id) {
        llChoice.setVisibility(GONE);
        llPhone.setVisibility(GONE);
        llWait.setVisibility(GONE);

        findViewById(id).setVisibility(VISIBLE);
    }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(SigninPage.this, cls);
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
                            Toast.makeText(SigninPage.this, getString(R.string.successful_signin), Toast.LENGTH_SHORT).show();
                            moveToAnotherActivity(HomeScreenActivity.class);

                        } else {

                            setVisibleVisibility(llPhone.getId());

                        }

                    }
                });
    }


    private void handleBtnPhoneClick() {
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();
        if(phone.isEmpty() || !PhoneSignupActivity.isValidPhone(phone)) {
            Toast.makeText(SigninPage.this, getString(R.string.error_PhoneAuth), Toast.LENGTH_SHORT).show();
        } else if(password.isEmpty() || !PhoneSignupActivity.isValidPassword(password)) {
            Toast.makeText(SigninPage.this, getString(R.string.error_Password), Toast.LENGTH_SHORT).show();
        } else {
            String formattedPhone = "+84" + phone.substring(phone.length() - 9);
            edtPhone.setText(formattedPhone);
            db.collection("users")
                    .whereEqualTo("phone", formattedPhone)
                    .whereEqualTo("password", password)
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
                            Toast.makeText(SigninPage.this, getString(R.string.error_signin), Toast.LENGTH_SHORT).show();
                            setVisibleVisibility(llPhone.getId());
                        } else {
                            handler.post(this::signIn);
                        }
                    });
            setVisibleVisibility(llWait.getId());
        }
    }
}