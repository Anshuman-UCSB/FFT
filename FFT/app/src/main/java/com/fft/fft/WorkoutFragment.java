package com.fft.fft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fft.fft.workouts.ActiveWorkout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkoutFragment extends Fragment {
    private static final String TAG = "FFT_WorkoutFragment";
    private DatabaseReference db;
    DatabaseReference ref;
    private boolean updatedWorkoutUI;
    private ValueEventListener listener;

    private Handler handler;
    private Runnable updateTime;
    private ActiveWorkout workout;

    TextView workoutName, elapsedTime, workoutNumber;

    public WorkoutFragment(){ super(R.layout.workout_fragment);}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"Workout started");

        handler = new Handler();
        updateTime = new Runnable() {
            @Override
            public void run() {
                if(workout!=null){
                    Log.d(TAG, "Updating time");
                    elapsedTime.setText(workout.getElapsedTime());
                    handler.postDelayed(this, 1000);
                }
            }
        };

        updatedWorkoutUI = false;
        workoutName = view.findViewById(R.id.workoutName);
        workoutNumber = view.findViewById(R.id.workoutNumber);
        elapsedTime = view.findViewById(R.id.elapsedTime);

        getActivity().setTitle("Workout");
        db = FirebaseDatabase.getInstance().getReference();
        String uid = requireArguments().getString("uid");
        ref = db.child("users").child(uid);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user == null){
                    Log.e(TAG, "LOGGING OUT");
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity().getApplicationContext(), Login.class);
                    startActivity(intent);
                }else {
                    processUser(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value", error.toException());
            }
        };
        ref.addValueEventListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ref.removeEventListener(listener);
        handler.removeCallbacks(updateTime);
        Log.i(TAG, "Removing listener");
    }

    public void pushUser(User user){
        ref.setValue(user);
    }

    private void processUser(User user) {
        Log.i(TAG, "Currently processing user " + user.name);
        workout = user.workout;
        if(user.workoutActive && workout == null){
            Log.e(TAG, "workout disappeared");
            user.workoutActive = false;
        }
        if(!updatedWorkoutUI && user.workoutActive){
            workoutName.setText(String.format("%s %s", getString(R.string.current_workout), workout.type));
            workoutNumber.setText(String.format("%s%d", getString(R.string.workout_number), user.numWorkouts+1));
            handler.post(updateTime);
            updatedWorkoutUI = true;
        }
        if(!user.workoutActive) {
            Log.d(TAG, "Setting up new workout");
            user.setupWorkout();
            updatedWorkoutUI = false;
            pushUser(user);
        }
    }
}
