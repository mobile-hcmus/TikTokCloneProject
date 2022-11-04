package com.example.tiktokcloneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class SigninChoiceActivity extends Activity implements View.OnClickListener {
    Button btnChoicePhone, btnChoiceEmail;
    LinearLayout llSigninChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_choice);

        llSigninChoice = (LinearLayout) findViewById(R.id.llSigninChoice);
        btnChoicePhone = (Button) llSigninChoice.findViewById(R.id.btnChoicePhone);
        btnChoiceEmail = (Button) llSigninChoice.findViewById(R.id.btnChoiceEmail);

        btnChoicePhone.setOnClickListener(this);
        btnChoiceEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnChoicePhone.getId()) {
            Intent intent = new Intent(SigninChoiceActivity.this, PhoneSigninActivity.class);
            startActivity(intent);
        }
    }
}