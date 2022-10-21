package com.example.tiktokcloneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SigninPage extends Activity implements View.OnClickListener {
    private LinearLayout llSigninPage, llChoice, llPhone, llWait;
    private EditText edtPhone, edtPassword, edtConfirm;
    private Button btnPhone, btnChoicePhone, btnChoiceEmail, btnChoiceFacebook, btnBackToHomeScreen,
            btnBackToChoice;
    private final int VISIBLE = View.VISIBLE;
    private final int GONE = View.GONE;
    //////thread/////
    private String msg;
    Handler handler = new Handler();
    /////firebase///////
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);

        llSigninPage = (LinearLayout) findViewById(R.id.llSigninPage);
        llPhone = (LinearLayout) llSigninPage.findViewById(R.id.llPhone);
        llChoice = (LinearLayout) llSigninPage.findViewById(R.id.llChoice);
        llWait = (LinearLayout) llSigninPage.findViewById(R.id.llWait);
        edtPhone = (EditText) llSigninPage.findViewById(R.id.edtPhone);
        edtPassword = (EditText) llSigninPage.findViewById(R.id.edtPassword);
        edtConfirm = (EditText) llSigninPage.findViewById(R.id.edtConfirm);
        btnPhone = (Button) llSigninPage.findViewById(R.id.btnPhone);
        btnChoicePhone = (Button) llSigninPage.findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) llSigninPage.findViewById(R.id.btnChoiceEmail);
        btnChoiceFacebook = (Button) llSigninPage.findViewById(R.id.btnChoiceFacebook);
        btnBackToHomeScreen = (Button) llSigninPage.findViewById(R.id.btnBackToHomeScreen);
        btnBackToChoice = (Button) llSigninPage.findViewById(R.id.btnBackToChoice);

        setVisibleVisibility(llChoice.getId());
        btnPhone.setText(getString(R.string.ic_btnLogin) + getString(R.string.ic_btnLogin));

        db = FirebaseFirestore.getInstance();

        btnChoicePhone.setOnClickListener(this);
        btnPhone.setOnClickListener(this);
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
                            Toast.makeText(SigninPage.this, getString(R.string.successful_signin), Toast.LENGTH_SHORT).show();
                            moveToAnotherActivity(HomeScreenActivity.class);
                        }
                    });
            setVisibleVisibility(llWait.getId());
        }
    }
}