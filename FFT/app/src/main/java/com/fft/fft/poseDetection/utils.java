package com.fft.fft.poseDetection;

import static java.lang.Math.atan2;

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
    public static PointF avg(PoseLandmark a, PoseLandmark b){
        PointF p1 = a.getPosition();
        PointF p2 = b.getPosition();
        return new PointF((p1.x+p2.x)/2, (p1.y+p2.y)/2);
    }
    public static float avgY(PoseLandmark a, PoseLandmark b){
        PointF p1 = a.getPosition();
        PointF p2 = b.getPosition();
        return (p1.y+p2.y)/2;
    }
    public static float diffY(PoseLandmark a, PoseLandmark b){
        PointF p1 = a.getPosition();
        PointF p2 = b.getPosition();
        return p1.y-p2.y;
    }
    public static float diffX(PoseLandmark a, PoseLandmark b){
        PointF p1 = a.getPosition();
        PointF p2 = b.getPosition();
        return p1.x-p2.x;
    }
    static double getAngle(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
        double result =
                Math.toDegrees(
                        atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                                lastPoint.getPosition().x - midPoint.getPosition().x)
                                - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                                firstPoint.getPosition().x - midPoint.getPosition().x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }
}
