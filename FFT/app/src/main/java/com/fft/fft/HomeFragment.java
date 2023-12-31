package com.fft.fft;

import android.content.Intent;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.Random;

public class HomeFragment extends Fragment {
    private final String TAG = "FFT_HomeFragment";
    private DatabaseReference db;
    private DatabaseReference myRef;
    private ValueEventListener listener;

    public HomeFragment(){
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "fragment created with bundle ");

        getActivity().setTitle("FFT");
        db = FirebaseDatabase.getInstance().getReference();

        MaterialCardView planCard = view.findViewById(R.id.card_plan);
        planCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.planCard));

        MaterialCardView coachCard = view.findViewById(R.id.card_coach);
        coachCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.secondaryColor));

        MaterialCardView statCard = view.findViewById(R.id.card_stats);
        statCard.setVisibility(View.GONE);

        TextView greeter = view.findViewById(R.id.greeter);
        greeter.setText(String.format("Welcome %s!", requireArguments().getString("name")));
        String uid = requireArguments().getString("uid");

        TextView numWorkouts = view.findViewById(R.id.numWorkout);
        TextView currentWorkout = view.findViewById(R.id.CurrentWorkout);

        TextView stats = view.findViewById(R.id.stats);
        myRef = db.child("users").child(uid);
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user == null){
                    Log.e(TAG, "LOGGING OUT");
//                    FirebaseAuth.getInstance().signOut();
//                    Intent intent = new Intent(getContext(), Login.class);
//                    startActivity(intent);
                }else {
                    Log.i(TAG, "user is: " + user);
                    Log.i(TAG, "Value is: " + user.numWorkouts);
                    numWorkouts.setText(String.format("Workout #%d", user.numWorkouts+1));
                    currentWorkout.setText(String.format("Today is your %s day! %s",user.getDay(), getRandomEmoji()));
                    Button startWorkout = view.findViewById(R.id.startWorkoutBtn);
                    startWorkout.setVisibility(View.VISIBLE);
                    if(user.workoutActive){
                        startWorkout.setText("Continue workout");
                    }
                    StringBuilder text = new StringBuilder();
                    text.append("You've completed ").append(user.numWorkouts).append(" workout").append(user.numWorkouts!=1?"s":"").append("!");
                    text.append("\nIn all of those, you lifted a total of ").append(user.totalWeight).append("lbs!");

                    long s = user.totalTime;
                    String totalTime = String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
                    text.append("\nThis took ").append(totalTime).append(", now that's dedication. Keep it up!");

                    stats.setText(text.toString());
                    if(user.numWorkouts>0)
                        statCard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value", error.toException());
            }
        };
        myRef.addValueEventListener(listener);

        MaterialDrawerSliderView slider = getActivity().findViewById(R.id.slider);
        view.findViewById(R.id.startWorkoutBtn).setOnClickListener(v->slider.setSelectionAtPosition(1,true));
        view.findViewById(R.id.benchBtn).setOnClickListener(v->slider.setSelectionAtPosition(3,true));
        view.findViewById(R.id.squatBtn).setOnClickListener(v->slider.setSelectionAtPosition(4,true));
        view.findViewById(R.id.deadliftBtn).setOnClickListener(v->slider.setSelectionAtPosition(5,true));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myRef.removeEventListener(listener);
        Log.i(TAG, "Removing listener");
    }

    private String getRandomEmoji() {
        String[] emojis = {"🥰","😍","🥳","🎉","⭐","✨","🌟"};
        return emojis[new Random().nextInt(emojis.length)];
    }
}