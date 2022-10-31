package com.example.tiktokcloneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout llChangePassword, llOldPassword, llNewPassword;
   private FragmentTransaction ft;
   private FragmentManager fm;
   Fragment fragmentWaiting;
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnOldPassword;
    final Integer GONE = View.GONE;
    final Integer VISIBLE = View.VISIBLE;
    Validator validator;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    Handler handler = new Handler();
    String phone;
    String password;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        llChangePassword = (LinearLayout) findViewById(R.id.llChangePassword);
        llOldPassword = (LinearLayout) llChangePassword.findViewById(R.id.llOldPassword);
        llNewPassword = (LinearLayout) llChangePassword.findViewById(R.id.llNewPassword);
        edtOldPassword = (EditText) llChangePassword.findViewById(R.id.edtOldPassword);
        edtNewPassword = (EditText) llChangePassword.findViewById(R.id.edtNewPassword);
        edtConfirmPassword = (EditText) llChangePassword.findViewById(R.id.edtConfirmPassword);
        btnOldPassword = (Button) llChangePassword.findViewById(R.id.btnOldPassword);

        fragmentWaiting = (Fragment) getSupportFragmentManager().findFragmentById(R.id.fragWaiting);

        validator = Validator.getInstance();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        fm= getSupportFragmentManager();

       addShowHideListener(fragmentWaiting);

        setVisibleVisibility(llOldPassword.getId());
        btnOldPassword.setText(getString(R.string.ic_btnLogin) + getString(R.string.ic_btnLogin));
        btnOldPassword.setOnClickListener(this);
    }

    private void moveToAnotherActivity(Class<?> cls) {
        Intent intent = new Intent(ChangePasswordActivity.this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }


    private void setVisibleVisibility(Integer id) {
        llNewPassword.setVisibility(GONE);
        llOldPassword.setVisibility(GONE);

        findViewById(id).setVisibility(VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btnOldPassword.getId()) {
            password = edtOldPassword.getText().toString();
            if(password.isEmpty() || !validator.isValidPassword(password)) {
                Toast.makeText(ChangePasswordActivity.this, getString(R.string.error_Password), Toast.LENGTH_SHORT).show();
            } else {
                phone = user.getPhoneNumber().toString();

                db.collection("users")
                        .whereEqualTo("phone", phone)
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

                            if (msg.equals("TRUE")) {
                                addShowHideListener(fragmentWaiting);
                                setVisibleVisibility(llNewPassword.getId());
                            } else {
                                addShowHideListener(fragmentWaiting);
                                Toast.makeText(this, getString(R.string.error_signin), Toast.LENGTH_SHORT).show();
                            }
                        });
                addShowHideListener(fragmentWaiting);
            }
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
}