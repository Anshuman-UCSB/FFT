package com.fft.fft;

import com.fft.fft.workouts.ActiveWorkout;
import com.fft.fft.workouts.PushWorkout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties
public class User {
    public int numWorkouts;
    public String uid;
    public String name;
    public String email;
    public ActiveWorkout workout;
    public boolean workoutActive;
    public User(){
        workout = null;
    }
    public User(FirebaseUser user) {
        uid = user.getUid();
        numWorkouts = 0;
        name = user.getDisplayName();
        email = user.getEmail();
        workout = null;
        workoutActive = false;
    }

    public void setupWorkout(){
        switch(numWorkouts%3){
            case 0:
                workout = new PushWorkout();
                break;
            case 1: // TODO: UPDATE WORKOUTS
                workout = new PushWorkout();
                break;
            case 2:
                workout = new PushWorkout();
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
}
