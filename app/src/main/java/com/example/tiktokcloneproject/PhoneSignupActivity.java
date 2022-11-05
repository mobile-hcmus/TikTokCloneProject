package com.example.tiktokcloneproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneSignupActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout llSignupPage, llPhone, llOtp;
    private EditText edtPhone, edtOtp;
    private Button btnPhone, btnOtp;
    private Fragment waitingFragment;
    private FragmentTransaction ft;
    private FragmentManager fm;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_signup);

        llSignupPage = (LinearLayout) findViewById(R.id.llSignupPage);
        llPhone = (LinearLayout) llSignupPage.findViewById(R.id.llPhone);
        llOtp = (LinearLayout) llSignupPage.findViewById(R.id.llOtp);
        edtPhone = (EditText) llSignupPage.findViewById(R.id.edtPhone);
        edtOtp = (EditText) llSignupPage.findViewById(R.id.edtOtp);
        btnPhone = (Button) llSignupPage.findViewById(R.id.btnPhone);
        btnOtp = (Button) llSignupPage.findViewById(R.id.btnOtp);

        validator = Validator.getInstance();
        fm= getSupportFragmentManager();
        waitingFragment = fm.findFragmentById(R.id.fragWaiting);

        addShowHideListener(waitingFragment);

        setVisibleVisibility(llPhone.getId());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();



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
                addShowHideListener(waitingFragment);
                Toast.makeText(PhoneSignupActivity.this, getString(R.string.error_verify), Toast.LENGTH_SHORT).show();
                setVisibleVisibility(llPhone.getId());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                addShowHideListener(waitingFragment);
                Toast.makeText(PhoneSignupActivity.this, getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                setVisibleVisibility(llOtp.getId());
                mVerificationId = verificationId;
                mResendToken = token;

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

                            moveToAnotherActivity(HomeScreenActivity.class);

                        } else {
                            addShowHideListener(waitingFragment);
                            setVisibleVisibility(llPhone.getId());

                        }

                    }
                });
    }

    private void setVisibleVisibility(int id) {
        llPhone.setVisibility(GONE);
        llOtp.setVisibility(GONE);

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
                            addShowHideListener(waitingFragment);
                            Toast.makeText(PhoneSignupActivity.this, getString(R.string.error_existedPhone), Toast.LENGTH_SHORT).show();
                            setVisibleVisibility(llPhone.getId());
                        }
                    });
            addShowHideListener(waitingFragment);
        }
    }

    void addShowHideListener(final Fragment fragment) {
        ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if (fragment.isHidden()) {
            ft.show(fragment);
        } else {
            ft.hide(fragment);
        }
        ft.commit();

    }

}// end PhoneLoginActivity class