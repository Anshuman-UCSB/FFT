package com.fft.fft.coaches;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.mlkit.vision.pose.Pose;

public abstract class Coach {
    private static final String TAG = "FFT_Coach";
    protected Handler h;
    protected StringBuilder str;
    protected boolean debugPrint;
    protected String debug;
    public Coach(Context ctx){
        h = new Handler();
        str = new StringBuilder();
        debugPrint = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("debug", false);
        reset();
        Log.i(TAG, "Coach abstract initialized");
    }
    public String getAdvice(){
        if(debug != null && debugPrint){
            str.append(debug);
        }
        return str.toString();
    }
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
