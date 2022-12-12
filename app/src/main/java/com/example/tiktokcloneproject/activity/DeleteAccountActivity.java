package com.example.tiktokcloneproject.activity;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.tiktokcloneproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountActivity extends Activity {

    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(DeleteAccountActivity.this, "test", Toast.LENGTH_SHORT).show();
                                Toast.makeText(DeleteAccountActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DeleteAccountActivity.this, HomeScreenActivity.class);
                                startActivity(intent);

                                finish();
                            }
                            else
                            {
                                Intent intent = new Intent(DeleteAccountActivity.this, SignInToDeleteActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }
}