package com.fft.fft.poseDetection;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class CustomPoseDetector {
    private static final String TAG = "FFT_CustomPoseDetector";
    private final boolean ACCURATE = true;
    private PoseDetector poseDetector;
    public CustomPoseDetector(){
        if(ACCURATE) {
            AccuratePoseDetectorOptions options =
                    new AccuratePoseDetectorOptions.Builder()
                            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                            .build();
            poseDetector = PoseDetection.getClient(options);
            Log.i(TAG, "using accurate pose detection");
        } else {
            PoseDetectorOptions options =
                    new PoseDetectorOptions.Builder()
                            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                            .build();
            poseDetector = PoseDetection.getClient(options);
            Log.i(TAG, "using fast pose detection");
        }

        Log.i(TAG, "CustomPoseDetector initialized");
    }

    public void requestPose(Bitmap bitmap, OnSuccessListener<? super Pose> listener){
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        Task<Pose> result = poseDetector.process(image)
                .addOnSuccessListener(listener);
    }
}
