package com.fft.fft.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fft.fft.R;
import com.fft.fft.workouts.Exercise;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        findViewById(R.id.exerciseCard).setOnClickListener(v->{
            Log.i(TAG, name+" Card was clicked");
            TextInputEditText textInput = new TextInputEditText(getContext());
            textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Enter weight for "+exercise.name)
                    .setView(textInput)
                    .setPositiveButton("Save", (d,w)->{
                        String enteredText = textInput.getText().toString();
                        try{
                            int weight = Integer.parseInt(enteredText);
                            exercise.weight = weight;
                            if(listener != null){
                                listener.exerciseUpdated(exercise);
                            }
                        } catch (NumberFormatException e){
                            Log.e(TAG, "Invalid weight passed in: "+enteredText);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
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
            listener.exerciseUpdated(exercise);
        }
        Log.i(TAG, "Checkbox callback, "+checks+", "+exercise.name+" completed="+exercise.setsDone+ " on object "+exercise);
    }
}
