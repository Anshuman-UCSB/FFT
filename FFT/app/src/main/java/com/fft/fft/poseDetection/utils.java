package com.fft.fft.poseDetection;

import android.graphics.PointF;
import android.util.Size;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class utils {


    public static Size getSizeForDesiredSize(int width, int height, int desiredSize){
        int w, h;
        if(width > height){
            w = desiredSize;
            h = Math.round((height/(float)width) * w);
        }else{
            h = desiredSize;
            w = Math.round((width/(float)height) * h);
        }
        return new Size(w, h);
    }

    public static float dist(PoseLandmark a, PoseLandmark b){
        PointF p1 = a.getPosition();
        PointF p2 = b.getPosition();
        return (float) Math.hypot(p1.x-p2.x, p1.y-p2.y);
    }
}
