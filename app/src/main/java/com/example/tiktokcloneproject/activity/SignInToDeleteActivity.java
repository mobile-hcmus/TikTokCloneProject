package com.example.tiktokcloneproject.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tiktokcloneproject.R;

public class SignInToDeleteActivity extends AppCompatActivity {

    Button btnSignInAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_to_delete);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        btnSignInAgain = (Button) findViewById(R.id.btnSignInAgain);

        btnSignInAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInToDeleteActivity.this, PhoneSigninActivity.class);
                startActivity(intent);
            }
        });
    }
}