package com.fft.fft.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fft.fft.R;
import com.fft.fft.workouts.Exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseView extends LinearLayout implements SetEventListener{

    private static final String TAG = "FFT_ExerciseView";
    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView setsAndReps;
    private String name;
    LinearLayout setContainer;
    private List<Boolean> checks;

    private Exercise exercise;

    ExerciseEventListener listener;

    public ExerciseView(Context context, Exercise exercise){
        super(context);
        this.exercise = exercise;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_exercise_view, this, true);
        nameTextView = view.findViewById(R.id.nameTextView);
        setsAndReps = view.findViewById(R.id.setsAndReps);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);

        setContainer = view.findViewById(R.id.setContainer);

        // Set Exercise data to the views
        name = exercise.name;
        nameTextView.setText(name);
        descriptionTextView.setText(exercise.desc);
        setsAndReps.setText(exercise.sets+"x"+exercise.reps);
        checks = new ArrayList<Boolean>(Collections.nCopies(exercise.sets, false));

        for(int i = 0;i<exercise.sets;i++){
            SetView sv = new SetView(context, exercise, i);
            sv.setListener(this);
            if(sv.done)
                checks.set(i, true);
            setContainer.addView(sv);
        }
    }

    public void setListener(ExerciseEventListener l){
        listener = l;
    }

    @Override
    public void onCheckboxClicked(boolean checked, int index) {
        checks.set(index, checked);
        exercise.setsDone = 0;
        for(boolean b: checks){
            exercise.setsDone+=b?1:0;
        }
        if(listener != null){
            listener.onCompletedChange(name, exercise.setsDone);
        }
        Log.i(TAG, "Checkbox callback, "+checks+", "+exercise.name+" completed="+exercise.setsDone+ " on object "+exercise);
    }
}
