package com.fft.fft;

import com.fft.fft.workouts.ActiveWorkout;
import com.fft.fft.workouts.Exercise;
import com.fft.fft.workouts.LegsWorkout;
import com.fft.fft.workouts.PullWorkout;
import com.fft.fft.workouts.PushWorkout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public int numWorkouts;
    public String uid;
    public String name;
    public String email;
    public ActiveWorkout workout;
    public boolean workoutActive;
    public Map<String, Double> nameToWeight;
    public long totalWeight;
    public long totalTime;

    public User(){
        workout = null;
    }
    public User(FirebaseUser user) {
        uid = user.getUid();
        numWorkouts = 0;
        totalWeight = 0;
        totalTime = 0;
        name = user.getDisplayName();
        email = user.getEmail();
        workout = null;
        workoutActive = false;
        nameToWeight = new HashMap<>();
        setWeight("null",0);
    }

    public void setWeight(String name, double weight){
        nameToWeight.put(name, weight);
    }

    public void setupWorkout(){
        switch(numWorkouts%3){
            case 0:
                workout = new PushWorkout();
                break;
            case 1:
                workout = new PullWorkout();
                break;
            case 2:
                workout = new LegsWorkout();
                break;
        }
        workoutActive = true;
    }

    public String getDay() {
        switch(numWorkouts%3){
            case 0: return "push";
            case 1: return "pull";
            case 2: return "legs";
        }
        return "rest";
    }
    public void finishWorkout(){
        assert workoutActive: "Tried to finish workout, but workoutActive is false";
        workoutActive = false;
        numWorkouts++;
        for(Exercise e: workout.exercises){
            totalWeight+=e.weight*e.setsDone;
        }
        long elapsedMs = System.currentTimeMillis()-workout.startTime;
        totalTime += elapsedMs/1000;
        workout = null;
    }
}
