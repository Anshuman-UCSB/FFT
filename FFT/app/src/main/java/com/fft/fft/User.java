package com.fft.fft;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public int numWorkouts;
    public String uid;
    public String name;
    public String email;
    public User(){}
    public User(FirebaseUser user) {
        uid = user.getUid();
        numWorkouts = 0;
        name = user.getDisplayName();
        email = user.getEmail();
    }
}
