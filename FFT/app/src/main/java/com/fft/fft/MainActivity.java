package com.fft.fft;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button;
    TextView greeter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        greeter = findViewById(R.id.greeter);
        user = auth.getCurrentUser();
        if (user == null){
            goToLogin();
        }
        greeter.setText(user.getEmail());

        button.setOnClickListener(v->{
            auth.signOut();
            goToLogin();
        });
    }
    private void goToLogin(){
        Intent intent = new Intent(getApplication(), Login.class);
        startActivity(intent);
        finish();
    }
}