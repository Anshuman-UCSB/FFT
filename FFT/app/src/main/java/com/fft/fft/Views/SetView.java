package com.fft.fft.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fft.fft.R;
import com.fft.fft.workouts.Exercise;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Set;

public class SetView extends LinearLayout {
    TextView weight, reps;
    CheckBox checkBox;
    private SetEventListener listener;
    boolean done;

    public SetView(Context context, Exercise exercise, int index){
        super(context);
        done = index<exercise.setsDone;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.custom_set_view, this);

        weight = findViewById(R.id.weight);
        reps = findViewById(R.id.reps);

        checkBox = findViewById(R.id.checkbox);
        checkBox.setChecked(done);
        checkBox.setText(String.format("set %d", index));
        checkBox.setOnCheckedChangeListener((a,checked)->{
            if(listener!=null){
                listener.onCheckboxClicked(checked, index);
            }
        });

        NumberFormat nf = new DecimalFormat("##.#");
        weight.setText(String.format("%slbs", nf.format(exercise.weight)));
        reps.setText(exercise.reps+" reps");

    }

    public void setListener(SetEventListener l){
        listener = l;
    }
}
