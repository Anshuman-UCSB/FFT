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
        addExercise("Bench Press", "Barbell",5,8);
        addExercise("Incline Press", "Barbell",3,8);
        addExercise("Shoulder Press", "Barbell or Dumbbells",3,8);
        addExercise("Triceps Pushdowns", "Cable Machine",3,15);
    }
}
