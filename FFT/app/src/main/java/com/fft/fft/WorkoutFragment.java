package com.fft.fft;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WorkoutFragment extends Fragment {
    private static final String TAG = "FFT_WorkoutFragment";

    public WorkoutFragment(){ super(R.layout.workout_fragment);}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"Workout started");
        getActivity().setTitle("Workout");
    }
}
