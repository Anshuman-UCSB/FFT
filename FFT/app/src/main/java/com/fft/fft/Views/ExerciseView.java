package com.fft.fft.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fft.fft.R;
import com.fft.fft.workouts.Exercise;

public class ExerciseView extends LinearLayout {

    private TextView nameTextView;
    private TextView descriptionTextView;
    private TextView setsAndReps;

    public ExerciseView(Context context, Exercise exercise){
        super(context);
        init(context, exercise);
    }

    private void init(Context context, Exercise exercise) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_exercise_view, this, true);

        nameTextView = view.findViewById(R.id.nameTextView);
        setsAndReps = view.findViewById(R.id.setsAndReps);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);

        // Set Exercise data to the views
        nameTextView.setText(exercise.name);
        descriptionTextView.setText(exercise.desc);
        setsAndReps.setText(exercise.sets+"x"+exercise.reps);
    }
}
