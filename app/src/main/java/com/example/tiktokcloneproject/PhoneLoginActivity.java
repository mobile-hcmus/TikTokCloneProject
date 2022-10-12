package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends Activity {

    View llPhoneLogin;
    LinearLayout llPhone;
    EditText edtPhone;
    LinearLayout llOtp;
    EditText edtOtp;
    Button btnPhone;
    Button btnOtp;


    ////////////firebase///////////////
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        llPhoneLogin = findViewById(R.id.llPhoneLogin);
        llPhone = (LinearLayout) llPhoneLogin.findViewById(R.id.llPhone);
        llOtp = (LinearLayout) llPhoneLogin.findViewById(R.id.llOtp);
        edtPhone = (EditText) llPhoneLogin.findViewById(R.id.edtPhone);
        edtOtp = (EditText) llPhoneLogin.findViewById(R.id.edtOtp);
        btnPhone = (Button) llPhoneLogin.findViewById(R.id.btnPhone);
        btnOtp = (Button) llPhoneLogin.findViewById(R.id.btnOtp);

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
                llPhone.setVisibility(View.VISIBLE);
                llOtp.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                llPhone.setVisibility(View.GONE);
                llOtp.setVisibility(View.VISIBLE);
            }
        }; // end callbacks

        btnOtp.setOnClickListener(v -> {
            if(TextUtils.isEmpty(edtOtp.getText().toString()) || edtOtp.getText().toString().length() != 6) {
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.error_Otp), Toast.LENGTH_SHORT).show();
            }
            else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, edtOtp.getText().toString());
                signInWithPhoneAuthCredential(credential);
                btnOtp.setVisibility(View.GONE);
            }
        });

        btnPhone.setOnClickListener(v -> {
            if(TextUtils.isEmpty(edtPhone.getText().toString()) || edtPhone.getText().toString().length() != 9) {
                Toast.makeText(PhoneLoginActivity.this, getString(R.string.error_PhoneAuth), Toast.LENGTH_SHORT).show();

            } else {
                login();
                btnPhone.setVisibility(View.GONE);
            }
        });

    } // end onCreate

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
                            llPhone.setVisibility(View.VISIBLE);
                            llOtp.setVisibility(View.GONE);

                        }

                    }
                });
    }
}// end PhoneLoginActivity class