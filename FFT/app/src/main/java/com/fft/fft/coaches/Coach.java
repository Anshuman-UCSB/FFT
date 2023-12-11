package com.fft.fft.coaches;

import android.os.Handler;
import android.util.Log;

import com.google.mlkit.vision.pose.Pose;

public abstract class Coach {
    private static final String TAG = "FFT_Coach";
    protected Handler h;
    protected StringBuilder str;
    public Coach(){
        h = new Handler();
        str = new StringBuilder();
        reset();
        Log.i(TAG, "Coach abstract initialized");
    }
    public abstract String getAdvice();
    public void note(String note){
        str.append("• ").append(note).append("\n");
    }
    public abstract void reset();
    public abstract void process(Pose pose);
    public void queue(Pose pose){
        h.post(new Runnable() {
            @Override
            public void run() {
                process(pose);
            }
        });
    }
}
