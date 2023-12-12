package com.fft.fft.workouts;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PullWorkout extends ActiveWorkout {
    private static final String TAG = "FFT_PullWorkout";
    public String type = "Pull";
    public PullWorkout(){
        super();
        Log.d(TAG, "PullWorkout constructor called");
        addExercise("Deadlift", "Barbell",3,5);
        addExercise("Bent Over Rows", "Barbell",4,12);
        addExercise("Dumbell Curls", "Dumbbells",4,15);
        addExercise("Concentration Curls", "Dumbbells",4,15);
    }
}
