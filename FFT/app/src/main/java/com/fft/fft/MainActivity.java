package com.fft.fft;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private Button button;
    private TextView greeter;
    private FirebaseUser user;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private MaterialDrawerSliderView slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null){
            goToLogin();
        }

        button = findViewById(R.id.logout);
        greeter = findViewById(R.id.greeter);
        greeter.setText(user.getDisplayName());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.root);
        slider = findViewById(R.id.slider);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        PrimaryDrawerItem item1 = new PrimaryDrawerItem();
        item1.setName(new StringHolder("test"));
        item1.setIdentifier(1);

        SecondaryDrawerItem item2 = new SecondaryDrawerItem();
        item2.setName(new StringHolder("test2"));
        item2.setIdentifier(2);
        slider.getItemAdapter().add(
                item1,
                new DividerDrawerItem(),
                item2
        );

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