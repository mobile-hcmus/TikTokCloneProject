package com.example.tiktokcloneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SigninChoiceActivity extends Activity implements View.OnClickListener {
    Button btnChoicePhone, btnChoiceEmail;
    TextView txvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_choice);

        btnChoicePhone = (Button) findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) findViewById(R.id.btnChoiceEmail);
        txvTitle = (TextView) findViewById(R.id.txvTitle);

        txvTitle.setText(getString(R.string.sign_in));

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
    }
}