package com.example.tiktokcloneproject;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.widget.Toast;

public class CameraActivity extends Activity {
    CameraManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        manager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        try {
            Toast.makeText(this, "Id:" + manager.getCameraIdList().length, Toast.LENGTH_SHORT).show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
