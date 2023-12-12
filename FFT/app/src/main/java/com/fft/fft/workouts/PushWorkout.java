package com.fft.fft.workouts;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PushWorkout extends ActiveWorkout {
    public String getType() {
        return "Push";
    }
}
