package com.example.tiktokcloneproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signUpPage(View v) {
        Intent intent = new Intent(MainActivity.this, PhoneSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void signInPage(View v) {
        Intent intent = new Intent(MainActivity.this, SigninPage.class);
        startActivity(intent);
    }
}