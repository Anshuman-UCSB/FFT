package com.example.fft_testing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class Display extends View {
    private final int inputWidth = 480;
    private final int inputHeight = 640;
    Rect srcRect = new Rect(0,0,inputWidth, inputHeight);
    Rect disRect;

    Bitmap b;

    public Display(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBitmap (Bitmap bitmap) {
        this.b = bitmap;
    }

    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
        invalidate();
        disRect = new Rect(0,0,getRight(),getRight()*inputHeight/inputWidth);
        if (b != null) {
            canvas.drawBitmap(b, srcRect, disRect, null);
        }
    }
}
