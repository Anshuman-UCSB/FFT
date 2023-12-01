package com.example.fft_video;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class FrameExtractor {
    private static final String TAG = "FrameExtractor";
    private Context context;
    public FrameExtractor(Context context){
        this.context = context;
    }
    public List<Bitmap> extractFrames(Uri videoUri) {
        List<Bitmap> frames = new ArrayList<>();

        return frames;
    }
}
