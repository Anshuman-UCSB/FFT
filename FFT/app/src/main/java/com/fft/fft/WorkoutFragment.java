package com.fft.fft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private ValueEventListener listener;

    public WorkoutFragment(){ super(R.layout.workout_fragment);}
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"Workout started");
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
        Log.i(TAG, "Removing listener");
    }

    public void pushUser(User user){
        ref.setValue(user);
    }

    private void processUser(User user) {
        Log.i(TAG, "Currently processing user " + user.name);
        if(!user.workoutActive){
            Log.d(TAG, "Setting up new workout");
            user.setupWorkout();
            pushUser(user);
        }
    }
}
