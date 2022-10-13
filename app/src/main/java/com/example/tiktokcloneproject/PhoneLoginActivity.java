package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends Activity implements View.OnClickListener {

    LinearLayout llLoginPage, llChoice, llPhone, llOtp, llWait;
    EditText edtPhone, edtOtp;
    Button btnPhone, btnOtp, btnChoicePhone, btnChoiceEmail, btnChoiceFacebook;
    final int VISIBLE = View.VISIBLE;
    final int GONE = View.GONE;
    ////////////firebase///////////////
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        llLoginPage = (LinearLayout) findViewById(R.id.llLoginPage);
        llPhone = (LinearLayout) llLoginPage.findViewById(R.id.llPhone);
        llOtp = (LinearLayout) llLoginPage.findViewById(R.id.llOtp);
        llChoice = (LinearLayout) llLoginPage.findViewById(R.id.llChoice);
        llWait = (LinearLayout) llLoginPage.findViewById(R.id.llWait);
        edtPhone = (EditText) llLoginPage.findViewById(R.id.edtPhone);
        edtOtp = (EditText) llLoginPage.findViewById(R.id.edtOtp);
        btnPhone = (Button) llLoginPage.findViewById(R.id.btnPhone);
        btnOtp = (Button) llLoginPage.findViewById(R.id.btnOtp);
        btnChoicePhone = (Button) llLoginPage.findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) llLoginPage.findViewById(R.id.btnChoiceEmail);
        btnChoiceFacebook = (Button) llLoginPage.findViewById(R.id.btnChoiceFacebook);

        setVisibilityLayouts(VISIBLE, GONE, GONE, GONE);
        btnPhone.setText(getString(R.string.ic_btnLogin) + getString(R.string.ic_btnLogin));
        btnOtp.setText(getString(R.string.ic_btnLogin) + getString(R.string.ic_btnLogin));







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
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.error_PhoneAuth), Toast.LENGTH_SHORT).show();
                setVisibilityLayouts(GONE, VISIBLE, GONE, GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                setVisibilityLayouts(GONE, GONE, VISIBLE, GONE);
            }
        }; // end callbacks

        btnOtp.setOnClickListener(v -> {
            if(TextUtils.isEmpty(edtOtp.getText().toString()) || edtOtp.getText().toString().length() != 6) {
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.error_Otp), Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edtOtp.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });

        btnPhone.setOnClickListener(v -> {
            if(TextUtils.isEmpty(edtPhone.getText().toString()) || edtPhone.getText().toString().length() != 9) {
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.error_PhoneAuth), Toast.LENGTH_SHORT).show();

            } else {
                login();
                setVisibilityLayouts(GONE, GONE, GONE, VISIBLE);
            }
        });
        btnChoicePhone.setOnClickListener(this);
    } // end onCreate

    @Override
    public void onClick(View v) {
        if(v.getId() == btnChoicePhone.getId()) {
            setVisibilityLayouts(GONE, VISIBLE, GONE, GONE);
        }
    }

    private void login() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+84"+edtPhone.getText().toString(),
                60,
                TimeUnit.SECONDS,
                PhoneLoginActivity.this,
                callbacks
        );
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);


                            finish();
                        } else {
                            setVisibilityLayouts(GONE, VISIBLE, GONE, GONE);

                        }

                    }
                });
    }

    private void setVisibilityLayouts(int l1, int l2, int l3, int l4) {
        llChoice.setVisibility(l1);
        llPhone.setVisibility(l2);
        llOtp.setVisibility(l3);
        llWait.setVisibility(l4);
    }
}// end PhoneLoginActivity class