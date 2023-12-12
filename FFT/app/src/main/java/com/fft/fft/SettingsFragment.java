package com.fft.fft;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = "FFT_Settings";

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        getActivity().setTitle("Settings");
        findPreference("deleteUser").setOnPreferenceClickListener(v->{
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Delete account")
                    .setIcon(R.drawable.delete)
                    .setMessage("Are you sure you want to delete your account? This cannot be undone.")
                    .setPositiveButton("Delete", (d,w)->{
                        deleteUser();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        });
    }
    public void deleteUser(){
        Log.e(TAG, "DELETING USER");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        String uid = requireArguments().getString("uid");
        DatabaseReference ref = db.child("users").child(uid);
        Log.i(TAG, "Deleting user from Database");
        ref.removeValue();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
    }
}
