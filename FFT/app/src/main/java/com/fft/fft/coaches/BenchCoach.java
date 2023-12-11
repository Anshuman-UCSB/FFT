package com.fft.fft.coaches;

import static com.fft.fft.poseDetection.utils.dist;

import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class BenchCoach extends Coach{
    public final String TAG = "FFT_BenchCoach";
    private int elbow;
    public BenchCoach(){
        Log.i(TAG, "Initialized bench coach");

    }
    @Override
    public String getAdvice() {
        StringBuilder str = new StringBuilder();
        switch(elbow) {
            case 1:
                str.append("Your elbows are too flared out, try to keep them at a 45 degree angle!\n");
                break;
            case -1:
                str.append("Your elbows are tucked in too much, try to keep them at a 45 degree angle!\n");
                break;
            default:
                str.append("Remember to keep your elbows at a 45 degree angle.\n");
        }
        return str.toString();
    }

    @Override
    public void reset() {
        elbow = 0;
    }

    @Override
    public void process(Pose pose) {
        Log.d(TAG, "Processing pose");
        PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
        PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
        PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
        PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
        PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
        PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);

        float shoulderWidth = dist(leftShoulder, rightShoulder);

    }
}
