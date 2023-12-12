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
    public Exercise(String name, String desc, int sets, int reps, double weight) {
        this.name = name;
        this.desc = desc;
        this.sets = sets;
        this.reps = reps;
        this.setsDone = 0;
        this.weight = weight;
    }
}
