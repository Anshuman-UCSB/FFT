package com.fft.fft.workouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fft.fft.R;

public class ExerciseView extends LinearLayout {

    private TextView nameTextView;
    private TextView descriptionTextView;

    public ExerciseView(Context context, Exercise exercise){
        super(context);
        init(context, exercise);
    }

    private void init(Context context, Exercise exercise) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_exercise_view, this, true);

        nameTextView = view.findViewById(R.id.nameTextView);
        descriptionTextView = view.findViewById(R.id.descriptionTextView);

        // Set Exercise data to the views
        nameTextView.setText(exercise.name);
        descriptionTextView.setText(exercise.desc);
    }
}
