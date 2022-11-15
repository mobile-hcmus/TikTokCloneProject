package com.example.tiktokcloneproject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CommentActivity extends Activity implements View.OnClickListener{
    private ImageView imvBack;
    private LinearLayout llComment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        llComment = (LinearLayout) findViewById(R.id.llComment);
        imvBack = (ImageView) llComment.findViewById(R.id.imvBackToHomeScreen);

        imvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == imvBack.getId()){
            onBackPressed();
            finish();
        }
    }
}
