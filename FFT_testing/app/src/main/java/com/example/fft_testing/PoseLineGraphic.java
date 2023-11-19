package com.example.fft_testing;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.google.mlkit.vision.pose.PoseLandmark;

public class PoseLineGraphic extends GraphicOverlay.Graphic {

    private final PoseLandmark startLandmark;
    private final PoseLandmark endLandmark;

    private final Paint linePaint;

    public PoseLineGraphic(GraphicOverlay overlay, PoseLandmark startLandmark, PoseLandmark endLandmark) {
        super(overlay);
        this.startLandmark = startLandmark;
        this.endLandmark = endLandmark;

        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(10f);
    }

    @Override
    public void draw(Canvas canvas) {
        PointF start = translateLandmark(startLandmark.getPosition());
        PointF end = translateLandmark(endLandmark.getPosition());

        canvas.drawLine(start.x, start.y, end.x, end.y, linePaint);
//        Log.i("Line", "Drawing line with points "+start+" and "+end);
    }

    private PointF translateLandmark(PointF landmark) {
        return new PointF(translateX(landmark.x), translateY(landmark.y));
    }
}