package com.example.tiktokcloneproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiktokcloneproject.R;

public class SignupChoiceActivity extends Activity implements View.OnClickListener {
    Button btnChoicePhone, btnChoiceEmail;
    LinearLayout llSignupChoice;
    TextView txvTitle, txvAlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_choice);

        llSignupChoice = (LinearLayout) findViewById(R.id.llSignupChoice);
        btnChoicePhone = (Button) llSignupChoice.findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) llSignupChoice.findViewById(R.id.btnChoiceEmail);
        txvTitle = (TextView) llSignupChoice.findViewById(R.id.txvTitle);
        txvAlt = (TextView) llSignupChoice.findViewById(R.id.txv_alternative);

        txvTitle.setText(getString(R.string.sign_up));
        txvAlt.setText(getString(R.string.sign_up_alt));

        btnChoicePhone.setOnClickListener(this);
        btnChoiceEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnChoicePhone.getId()) {
            Intent intent = new Intent(SignupChoiceActivity.this, PhoneSignupActivity.class);
            startActivity(intent);
        }
        if(view.getId() == btnChoiceEmail.getId()) {
            Intent intent = new Intent(SignupChoiceActivity.this, EmailSignupActivity.class);
            startActivity(intent);
        }
        if(view.getId() == txvAlt.getId()) {
            Intent intent = new Intent(SignupChoiceActivity.this, SigninChoiceActivity.class);
            startActivity(intent);
        }
    }
}