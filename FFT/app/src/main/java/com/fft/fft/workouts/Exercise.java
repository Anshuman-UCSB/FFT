package com.fft.fft.workouts;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Exercise {
    public String name;
    public String desc;
    public int sets, reps;
    public int setsDone;
    public double weight;
    public Exercise(){}
    public Exercise(String name, String desc, int sets, int reps, int setsDone, double weight) {
        this.name = name;
        this.desc = desc;
        this.sets = sets;
        this.reps = reps;
        this.setsDone = setsDone;
        this.weight = weight;
    }
}
