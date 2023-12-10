package com.fft.fft;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {
    private final String TAG = "FFT_HomeFragment";
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    private TextView numWorkouts;

    public HomeFragment(){
        super(R.layout.home_fragment);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "fragment created with bundle ");

        MaterialCardView planCard = view.findViewById(R.id.card_plan);
        planCard.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.planCard));

        TextView greeter = view.findViewById(R.id.greeter);
        greeter.setText(String.format("Welcome %s!", requireArguments().getString("name")));

        numWorkouts = view.findViewById(R.id.numWorkout);
        DatabaseReference myRef = db.getReference("numWorkouts");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer value = snapshot.getValue(Integer.class);
                Log.i(TAG, "Value is: "+value);
                if(value == null){
                    myRef.setValue(0);
                }
                numWorkouts.setText(String.format("Workout #%d", value));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value", error.toException());
            }
        });

    }
}