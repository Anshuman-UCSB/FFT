package com.fft.fft.workouts;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PushWorkout extends ActiveWorkout {
    private static final String TAG = "FFT_PushWorkout";
    public String type = "Push";
    public PushWorkout(){
        super();
        Log.d(TAG, "PushWorkout constructor called");
    }
}
