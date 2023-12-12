package com.fft.fft.workouts;

import android.util.Log;
import android.widget.Toast;

import com.fft.fft.User;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class ActiveWorkout {
    private static final String TAG = "FFT_ActiveWorkout";
    public long startTime;
    public String type = "null";
    public List<Exercise> exercises;

    public ActiveWorkout(){
        startTime = System.currentTimeMillis();
        exercises = new ArrayList<>();
        Log.d(TAG, "ActiveWorkout constructor called");
    }

    public void addExercise(String name, String desc, int sets, int reps){
        exercises.add(new Exercise(name, desc, sets, reps,-1));
    }

    public boolean updateExerciseWeights(User user){
        boolean changed = false;
        for(Exercise e: exercises){
            if(e.weight == -1){
                e.weight = user.nameToWeight.getOrDefault(e.name, 0d);
                changed = true;
            }
        }
        return changed;
    }

    public String getElapsedTime(){
        long elapsedMs = System.currentTimeMillis()-startTime;
        long s = elapsedMs/1000;
        return String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }
}
