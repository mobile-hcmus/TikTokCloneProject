package com.example.tiktokcloneproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tiktokcloneproject.R;

public class SigninChoiceActivity extends Activity implements View.OnClickListener {
    Button btnChoicePhone, btnChoiceEmail;
    TextView txvTitle, txvAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_choice);

        btnChoicePhone = (Button) findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) findViewById(R.id.btnChoiceEmail);
        txvTitle = (TextView) findViewById(R.id.txvTitle);
        txvAlt = (TextView) findViewById(R.id.txv_alternative);

        txvTitle.setText(getString(R.string.sign_in));
        txvAlt.setText(getString(R.string.sign_in_alt));
        btnChoicePhone.setOnClickListener(this);
        btnChoiceEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnChoicePhone.getId()) {
            Intent intent = new Intent(SigninChoiceActivity.this, PhoneSigninActivity.class);
            startActivity(intent);
        }
        if(view.getId() == btnChoiceEmail.getId()) {
            Intent intent = new Intent(SigninChoiceActivity.this, EmailSignInActivity.class);
            startActivity(intent);
        }
        if(view.getId() == txvAlt.getId()) {
            Intent intent = new Intent(SigninChoiceActivity.this, SignupChoiceActivity.class);
            startActivity(intent);
        }
    }
}