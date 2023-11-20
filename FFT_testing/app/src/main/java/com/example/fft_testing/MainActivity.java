package com.example.fft_testing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "FFT_tester";
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private PreviewView mPreviewView;
    private GraphicOverlay mGraphicOverlay;

    private PoseDetector poseDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviewView = findViewById(R.id.previewView);
        mGraphicOverlay = findViewById(R.id.graphicOverlay);

        mPreviewView.setScaleType(PreviewView.ScaleType.FIT_START);
        getCameraPermission();

        poseDetector = PoseDetection.getClient(new PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build());

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(()->{
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException | InterruptedException e) {}
            bindPreview(cameraProvider);
        }, ContextCompat.getMainExecutor(this));
    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setResolutionSelector(new ResolutionSelector.Builder()
                        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY)
                        .build())
                .build();

        imageAnalysis.setAnalyzer(getCameraExecutor(), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                processImage(imageProxy);
            }
        });
        // Obtain the preview resolution
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());


        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);
        Log.i("GraphicOverlay", "resolution info: "+preview.getResolutionInfo().getResolution().toString());
        Size resolution = preview.getResolutionInfo().getResolution();
        mGraphicOverlay.setImageSourceInfo(resolution.getWidth(),resolution.getHeight(), true);
    }

    @OptIn(markerClass = ExperimentalGetImage.class) private void processImage(ImageProxy imageProxy) {
        // Convert ImageProxy to InputImage
        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees());
//        int width = image.getWidth();
//        int height = image.getHeight();
//        if (width != mGraphicOverlay.getImageWidth() || height != mGraphicOverlay.getImageHeight()){
//            mGraphicOverlay.setImageSourceInfo(image.getWidth(), image.getHeight(), true);
//            Log.i("graphic overlay", "setting image source info to "+image.getWidth()+ "x"+image.getHeight());
//        }
        // Perform pose detection
        poseDetector.process(image)
                .addOnSuccessListener(pose -> {
                    // Handle the detected pose
                    handlePose(pose);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Pose Detection Failed");
                })
                .addOnCompleteListener(task -> imageProxy.close());
    }
    private void handlePose(Pose pose) {
        // Handle the detected poses
        List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
        if(landmarks.size() == 0){
            Log.i(TAG, "No person detected");
            return;
        }
        Map<String, PoseLandmark> extracted = extractLandmarks(landmarks);
        mGraphicOverlay.add(new PoseLineGraphic(mGraphicOverlay, extracted.get("LEFT_EYE"), extracted.get("RIGHT_EYE")));
        // Access landmarks for further processing or visualization
        Log.i(TAG, "Landmarks: "+landmarks.size());
        Log.i(TAG, "Preview resolution: "+mPreviewView.getWidth() + "x"+mPreviewView.getHeight());
    }

    private Map<String, PoseLandmark> extractLandmarks(List<PoseLandmark> landmarks){
        Map<String, PoseLandmark> out = new HashMap<String, PoseLandmark>();
        for(PoseLandmark pl: landmarks){
            switch(pl.getLandmarkType()){
                case 5:
                    out.put("RIGHT_EYE", pl);
                    break;
                case 2:
                    out.put("LEFT_EYE", pl);
                    break;
            }
        }
        return out;
    }

    private Executor getCameraExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}