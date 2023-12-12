package com.fft.fft.workouts;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ActiveWorkout {
    public long startTime;
    public String type = "null";

    public ActiveWorkout(){
        startTime = System.currentTimeMillis();
    }
    public String getElapsedTime(){
        long elapsedMs = System.currentTimeMillis()-startTime;
        long s = elapsedMs/1000;
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }
}
