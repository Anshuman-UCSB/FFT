package com.fft.fft;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;

public class HomeFragment extends Fragment {
    private final String TAG = "FFT_HomeFragment";
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    private TextView numWorkouts;

    public HomeFragment(){
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "fragment created with bundle ");

        getActivity().setTitle("FFT");

        MaterialCardView planCard = view.findViewById(R.id.card_plan);
        planCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.planCard));

        MaterialCardView coachCard = view.findViewById(R.id.card_coach);
        coachCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondaryColor));

        TextView greeter = view.findViewById(R.id.greeter);
        greeter.setText(String.format("Welcome %s!", requireArguments().getString("name")));
        String uid = requireArguments().getString("uid");
        numWorkouts = view.findViewById(R.id.numWorkout);
        DatabaseReference myRef = db.child("users").child(uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user == null){
                    Log.e(TAG, "LOGGING OUT");
                    FirebaseAuth.getInstance().signOut();
                }else {
                    Log.i(TAG, "user is: " + user);
                    Log.i(TAG, "Value is: " + user.numWorkouts);
                    numWorkouts.setText(String.format("Workout #%d", user.numWorkouts));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value", error.toException());
            }
        });

        MaterialDrawerSliderView slider = getActivity().findViewById(R.id.slider);
        Log.i(TAG, "Slider:"+slider);
        view.findViewById(R.id.benchBtn).setOnClickListener(v->slider.setSelectionAtPosition(2,true));
        view.findViewById(R.id.squatBtn).setOnClickListener(v->slider.setSelectionAtPosition(3,true));
        view.findViewById(R.id.deadliftBtn).setOnClickListener(v->slider.setSelectionAtPosition(4,true));
    }
}