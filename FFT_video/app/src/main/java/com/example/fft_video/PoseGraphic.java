package com.example.fft_video;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

public class PoseGraphic extends GraphicOverlay.Graphic {

    private static final float DOT_RADIUS = 8.0f;
    private static final String TAG = "PoseGraphic";
    Paint paint;
    private Pose pose;

    public PoseGraphic(GraphicOverlay overlay, Pose pose) {
        super(overlay);
        this.pose = pose;
        paint = new Paint();
    }

    @Override
    public void draw(Canvas canvas) {
        if(pose.getAllPoseLandmarks().isEmpty()){
            Log.e(TAG, "No landmarks, skipping graphic");
            return;
        }
        PoseLandmark nose = pose.getPoseLandmark(PoseLandmark.NOSE);
        Log.i(TAG, "Landmarks: "+pose.getAllPoseLandmarks());
        PointF point = nose.getPosition();
        canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint);
        Log.i(TAG, "Drawing at "+translateX(point.x)+", "+translateY(point.y));
    }
}
