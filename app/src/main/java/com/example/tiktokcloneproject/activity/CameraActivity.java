package com.example.tiktokcloneproject.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.tiktokcloneproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CameraActivity extends Activity implements View.OnClickListener {
    CameraManager manager;
    FrameLayout cameraFrameLayout;
    TextureView textureFront;
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            getCameraIds(i, i1, Objects.equals(defaultId, frontId));
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
    Size videoSize;
    Button btnUploadVideo;
    Button btnClose;
    Button btnPause;
    Button btnContinue;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser user;

    MediaRecorder mediaRecorder;
    File videoFileHolder;
    Button btnStartRecording;
    ImageButton btnFlip;
    Button btnStopRecording;
    boolean isRecording = false;
    boolean isPaused = false;

    String videoFileName;
    String userId;
    File videoFolder;

    Animation animRotate;

    int totalRotation;
    static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    static int sensorToDeviceToRotation(CameraCharacteristics c, int deviceOrientation) {
        if (deviceOrientation == android.view.OrientationEventListener.ORIENTATION_UNKNOWN) return 0;
        int sensorOrientation = c.get(CameraCharacteristics.SENSOR_ORIENTATION);

        // Round device orientation to a multiple of 90
        deviceOrientation = (deviceOrientation + 45) / 90 * 90;

        // Reverse device orientation for front-facing cameras
        boolean facingFront = c.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
        if (facingFront) deviceOrientation = -deviceOrientation;

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        int jpegOrientation = (sensorOrientation + deviceOrientation + 360) % 360;

        return jpegOrientation;
    }
    CameraDevice mainCamera;
    CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mainCamera = cameraDevice;
            if (isRecording) {
                try {
                    createVideoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord();
                mediaRecorder.start();
            }
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

        // Create folder for storing videos
        createVideoFolder();
        mediaRecorder = new MediaRecorder();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        btnUploadVideo.setOnClickListener(this);
        btnStartRecording = findViewById(R.id.button_record);
        btnClose = findViewById(R.id.button_close);
        btnFlip = findViewById(R.id.imb_flip_camera);
        btnPause = findViewById(R.id.button_pause);
        btnContinue = findViewById(R.id.button_continue);
        btnStopRecording = findViewById(R.id.button_stop);

        // Get Camera TextureView
        cameraFrameLayout = findViewById(R.id.camera_frame);
        textureFront = findViewById(R.id.texture_view_front);

        animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate);
        // Filter list
    }

    @Override
    protected void onResume() {
        super.onResume();
        // thread starting
        startBackgroundThread();
        if (textureFront.isAvailable()) {
            getCameraIds(textureFront.getWidth(), textureFront.getHeight(), Objects.equals(defaultId, frontId));
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
        cameraFrameLayout.setLayoutParams(new FrameLayout.LayoutParams(newWidth, newHeight));

    }

    private void connectCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "please grant permission!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        1);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "please grant permission!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
            manager.openCamera(defaultId, cameraDeviceCallback, backgroundHandler);
        } catch(Exception e) {
            Log.i("EXCEPTION: ", e.toString());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imb_flip_camera) {
            if (mainCamera != null) {
                mainCamera.close();
                mainCamera = null;
            }
            view.startAnimation(animRotate);

            defaultId = (Objects.equals(defaultId, frontId)) ? backId : frontId;
            textureFront.setSurfaceTextureListener(textureListener);
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

        if (view.getId() == R.id.button_record) {
            btnStopRecording.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            btnFlip.setVisibility(View.GONE);
            findViewById(R.id.tv_flip_camera).setVisibility(View.GONE);
            btnUploadVideo.setVisibility(View.GONE);
            btnStartRecording.setVisibility(View.GONE);
            btnClose.setVisibility(View.GONE);

            isRecording = true;
            try {
                videoFileHolder = createVideoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startRecord();
            mediaRecorder.start();
        }
        if (view.getId() == R.id.button_pause) {
            if (!isPaused) {
                mediaRecorder.pause();
                btnPause.setVisibility(View.GONE);
                btnContinue.setVisibility(View.VISIBLE);
                isPaused = true;
            }
        }
        if (view.getId() == R.id.button_continue) {
            if (isPaused) {
                mediaRecorder.resume();
                btnPause.setVisibility(View.VISIBLE);
                btnContinue.setVisibility(View.GONE);
                isPaused = false;
            }
        }
        if (view.getId() == R.id.button_stop) {
            if (isRecording) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{videoFileHolder.getAbsolutePath()}, null, null);
                startCameraPreview();

                btnStartRecording.setVisibility(View.VISIBLE);
                isRecording = false;
                btnFlip.setVisibility(View.VISIBLE);
                findViewById(R.id.tv_flip_camera).setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.GONE);
                btnUploadVideo.setVisibility(View.VISIBLE);
                btnStopRecording.setVisibility(View.GONE);
                btnPause.setVisibility(View.GONE);
                btnContinue.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
//                connectCamera();
                startUploadingActivity(Uri.fromFile(videoFileHolder));
            }
        }

        if (view.getId() == R.id.button_close) {
            finish();
            overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_out_bottom);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri videoUri = data.getData();

            startUploadingActivity(videoUri);
        }
    }

    void startUploadingActivity(Uri videoUri) {
        Intent i = new Intent(this,
                DescriptionVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("videoUri", videoUri.toString());
        i.putExtras(bundle);
        startActivity(i);
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
                    Log.i("BACK:", "ok");
                }
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                totalRotation = sensorToDeviceToRotation(characteristics, deviceOrientation);
                boolean swapRotation = totalRotation == 90 || totalRotation == 270;
                int rotateWidth = width;
                int rotateHeight = height;
                if (swapRotation) {
                    Log.i("BACK:", "not ok");
                    rotateWidth = height;
                    rotateHeight = width;
                }
                try {
                    StreamConfigurationMap map = manager.getCameraCharacteristics(defaultId).
                            get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Log.i("DIMENSION:", rotateWidth + "," + rotateHeight);
                    previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotateWidth, rotateHeight);
                    videoSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotateWidth, rotateHeight);
                    setTextureViewSize(previewSize);
                } catch (CameraAccessException e) {

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
            Log.i("EXCEPTION: ", exception.toString());
        }


    }

    private static class CompareSizeByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.getHeight() * lhs.getHeight() / (long)rhs.getHeight() * rhs.getHeight());
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnough = new ArrayList<>();
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

    void startRecord() {
        try {
            setupMediaRecorder();
            SurfaceTexture surfaceTexture = textureFront.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(videoSize.getWidth(), videoSize.getHeight());
            Surface previewSurface = new Surface(surfaceTexture);
            Surface recordSurface = mediaRecorder.getSurface();
            try {
                captureRequestBuilder = mainCamera.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
                captureRequestBuilder.addTarget(previewSurface);
                captureRequestBuilder.addTarget(recordSurface);
                mainCamera.createCaptureSession(Arrays.asList(previewSurface, recordSurface),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                try {
                                    cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startCameraPreview() {
        SurfaceTexture surfaceTexture = textureFront.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            captureRequestBuilder = mainCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            mainCamera.createCaptureSession(Collections.singletonList(previewSurface), new CameraCaptureSession.StateCallback() {
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

    void createVideoFolder() {
        File movieFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        videoFolder = new File(movieFile, "TopTopVideos");
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }
    }

    File createVideoFileName() throws IOException {
       String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
       String fileName = userId + "_" + timestamp;
        File videoFile = File.createTempFile(fileName, ".mp4", videoFolder);
        videoFileName = videoFile.getAbsolutePath();
        return videoFile;
    }

    void setupMediaRecorder() throws IOException{
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(videoFileName);
        mediaRecorder.setVideoSize(videoSize.getWidth(), videoSize.getHeight());
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoEncodingBitRate(3000000);
        if (defaultId == frontId) {
            mediaRecorder.setOrientationHint(totalRotation);
        } else {
        mediaRecorder.setOrientationHint((totalRotation + 180) % 360);
        }
        mediaRecorder.prepare();
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


    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
