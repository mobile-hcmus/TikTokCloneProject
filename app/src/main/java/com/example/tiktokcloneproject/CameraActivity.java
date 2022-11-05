package com.example.tiktokcloneproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class CameraActivity extends Activity implements View.OnClickListener {
    CameraManager manager;
    FrameLayout cameraFrameLayout;
    TextureView textureFront, textureBack;
    String frontId, backId;
    Button btnUploadVideo;
    ImageView imv;
    FirebaseFirestore db;
    Uri videoUri;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Get Camera list
        manager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        try {
            String[] cameraList = manager.getCameraIdList();
            // Find front and back cameras
            for (String id : cameraList) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    backId = id;
                } else if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontId = id;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnUploadVideo = (Button) findViewById(R.id.btnUploadVideo);
        btnUploadVideo.setOnClickListener(this);

        // Get Camera TextureView
        cameraFrameLayout = (FrameLayout) findViewById(R.id.camera_frame);
        textureFront = (TextureView) findViewById(R.id.texture_view_front);
        textureBack = (TextureView) findViewById(R.id.texture_view_back);
        textureFront.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                openCamera(surfaceTexture, frontId);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {

                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });
        textureBack.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                openCamera(surfaceTexture, backId);
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });

        // Open Camera

    }

    private void openCamera(SurfaceTexture surfaceTexture, String defaultId) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "please grant permission!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);

            }
            manager.openCamera(defaultId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    Toast.makeText(getApplicationContext(), "open camera on open", Toast.LENGTH_SHORT).show();
                    Size maxImageSize = new Size(0, 0);
                    try {
                        Size[] previewSizes = manager.getCameraCharacteristics(defaultId).
                                get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).
                                getOutputSizes(ImageFormat.JPEG);
                        for (Size s : previewSizes) {
                            if (s.getHeight() * s.getWidth() > maxImageSize.getHeight() * maxImageSize.getWidth()) {
                                maxImageSize = s;
                            }
                        }
                        Toast.makeText(getApplicationContext(), "maxSize now changed to: " + maxImageSize.getWidth() + ", " + maxImageSize.getHeight(), Toast.LENGTH_SHORT).show();
                    } catch (CameraAccessException e) {
                        Toast.makeText(getApplicationContext(), "failed to load size array", Toast.LENGTH_SHORT).show();
                    }
                    setTextureViewSize(maxImageSize);
                    surfaceTexture.setDefaultBufferSize(textureFront.getWidth(), textureFront.getHeight());
                    Surface previewSurface = new Surface(surfaceTexture);

                    try {
                        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_PREVIEW);
                        builder.addTarget(previewSurface);
                        cameraDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                try {
                                    cameraCaptureSession.setRepeatingRequest(builder.build(), null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    Toast.makeText(getApplicationContext(), "on disconnect?", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                    Toast.makeText(getApplicationContext(), "error" + i, Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTextureViewSize(Size cameraImageSize) {
        Float ratio = (float) cameraImageSize.getWidth() / cameraImageSize.getHeight();
        Integer newWidth = Math.round((float) cameraFrameLayout.getWidth() * ratio);
        Integer newHeight = Math.round((float) cameraFrameLayout.getHeight());
        Toast.makeText(this, "changing frame layout size with ratio: " + ratio, Toast.LENGTH_SHORT).show();
        cameraFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.flip_camera) {
            if (textureBack.getVisibility() == View.VISIBLE) {
                textureBack.setVisibility(View.INVISIBLE);
            } else {
                textureBack.setVisibility(View.VISIBLE);
            }
        }
        if (view.getId() == btnUploadVideo.getId()) {
            //            progressDialog = new ProgressDialog(MainActivity.this);

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "please grant permission!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_MEDIA_LOCATION},
                            1);
                }

            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 5);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();


            Intent i = new Intent(this,
                    DescriptionVideoActivity.class);
            i.putExtra("videoUri", videoUri.toString());
            startActivity(i);

//            try {
//                uploadVideo();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
        }
    }
}
