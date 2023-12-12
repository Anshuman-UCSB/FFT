package com.fft.fft.workouts;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LegsWorkout extends ActiveWorkout {
    private static final String TAG = "FFT_LegsWorkout";
    public String type = "Legs";
    public LegsWorkout(){
        super();
        Log.d(TAG, "LegsWorkout constructor called");
        addExercise("Squat", "Barbell",4,10);
        addExercise("Leg Press", "Machine",4,10);
        addExercise("Leg Extensions", "Machine",4,20);
        addExercise("Leg Curls", "Machine",4,8);
    }
}
