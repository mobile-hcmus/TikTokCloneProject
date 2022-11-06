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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraActivity extends Activity implements View.OnClickListener {
    CameraManager manager;
    FrameLayout cameraFrameLayout;
    TextureView textureFront;
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            getCameraIds(i, i1, true);
            connectCamera();
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
    };
    String frontId, backId, defaultId;
    Size previewSize;
    Button btnUploadVideo;
    ImageView imv;
    FirebaseFirestore db;
    Uri videoUri;
    FirebaseAuth mAuth;
    FirebaseUser user;
    static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    static int sensorToDeviceToRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }
    CameraDevice mainCamera;
    CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mainCamera = cameraDevice;
            startCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            Toast.makeText(getApplicationContext(), "on disconnect?", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            Toast.makeText(getApplicationContext(), "error" + i, Toast.LENGTH_SHORT).show();
        }
    };

    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;

    CaptureRequest.Builder captureRequestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Get Camera list

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        btnUploadVideo = (Button) findViewById(R.id.btnUploadVideo);
        btnUploadVideo.setOnClickListener(this);

        // Get Camera TextureView
        cameraFrameLayout = (FrameLayout) findViewById(R.id.camera_frame);
        textureFront = (TextureView) findViewById(R.id.texture_view_front);

        // Open Camera

    }

    @Override
    protected void onResume() {
        super.onResume();
        // thread starting
        startBackgroundThread();
        if (textureFront.isAvailable()) {
            getCameraIds(textureFront.getWidth(), textureFront.getHeight(), true);
            connectCamera();
        } else {
            textureFront.setSurfaceTextureListener(textureListener);
        }

    }

    private void setTextureViewSize(Size cameraImageSize) {
        int currentWidth = cameraFrameLayout.getWidth();
        float ratio = (float)cameraImageSize.getWidth()/cameraImageSize.getHeight();
        int newHeight = Math.round((float)currentWidth*ratio);
        int newWidth = Math.round(currentWidth);
        Toast.makeText(this, "new size is: " + newWidth + "," + newHeight, Toast.LENGTH_SHORT).show();
        cameraFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));
        currentWidth = cameraFrameLayout.getWidth();
        Toast.makeText(this, "ratio is: " + ratio, Toast.LENGTH_SHORT).show();

    }

    private void connectCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "please grant permission!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        1);

            }
            manager.openCamera(defaultId, cameraDeviceCallback, backgroundHandler);
        } catch(Exception e) {

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.flip_camera) {
            if (mainCamera != null) {
                mainCamera.close();
                mainCamera = null;
            }
            getCameraIds(textureFront.getWidth(), textureFront.getHeight(), defaultId != frontId);
            connectCamera();
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

    private void getCameraIds(int width, int height, boolean isFront) {
        manager = (CameraManager) this.getSystemService(CAMERA_SERVICE);
        try {
            String[] cameraList = manager.getCameraIdList();
            // Find front and back cameras
            for (String id : cameraList) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(id);
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    frontId = id;
                    if (!isFront) {
                        continue;
                    }
                    defaultId = frontId;
                }
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    backId = id;
                    if (isFront) {
                        continue;
                    }
                    defaultId = backId;
                }
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                int totalRotation = sensorToDeviceToRotation(characteristics, deviceOrientation);
                boolean swapRotation = totalRotation == 90 || totalRotation == 270;
                int rotateWidth = width;
                int rotateHeight = height;
                if (swapRotation) {
                    rotateWidth = height;
                    rotateHeight = width;
                }
                try {
                    StreamConfigurationMap map = manager.getCameraCharacteristics(defaultId).
                            get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Log.i("DIMENSION:", rotateWidth + "," + rotateHeight);
                    previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotateWidth, rotateHeight);
                    setTextureViewSize(previewSize);
                    Toast.makeText(getApplicationContext(), "preview size:" + previewSize.getWidth() + "," + previewSize.getHeight(), Toast.LENGTH_SHORT).show();
                } catch (CameraAccessException e) {
                    Toast.makeText(getApplicationContext(), "failed to load size array", Toast.LENGTH_SHORT).show();
                }
                break;
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("CameraThread");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());

    }

    private void stopBackgroundThread() {
        backgroundHandlerThread.quitSafely();
        try {
            backgroundHandlerThread.join();
            backgroundHandlerThread = null;
            backgroundHandler = null;
        } catch(Exception exception) {

        }


    }

    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getHeight() * lhs.getHeight() / (long)rhs.getHeight() * rhs.getHeight());
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        int i = 0;
        Log.i("GETSIZE added", "default " + ": " + (float)width / height);
        for (Size s : choices) {
            i += 1;
            Log.i("GETSIZE added", "ratio " + i + ": " + (float)s.getWidth() / s.getHeight());
            Log.i("GETSIZE added", "compare: " + Math.abs((float)s.getWidth()/s.getHeight() - (float)width/height));
            if (Math.abs((float)s.getWidth()/s.getHeight() - (float)width/height) < 0.21) {
//                Log.i("GETSIZE added", "size added: " + s.getWidth() + "," + s.getHeight());
                bigEnough.add(s);
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizeByArea());
        }
        Log.i("GETSIZE default", "size added: " + choices[0].getWidth() + "," + choices[0].getHeight());
        return choices[0];
    }

    private void startCameraPreview() {
        SurfaceTexture surfaceTexture = textureFront.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            captureRequestBuilder = mainCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            mainCamera.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, backgroundHandler);
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
    protected void onPause() {
        super.onPause();
        // Stop background thread
        stopBackgroundThread();
        // Close camera
        if (mainCamera != null) {
            mainCamera.close();
            mainCamera = null;
        }


    }   @Override
    protected void onStop() {
        super.onStop();

    }
}
