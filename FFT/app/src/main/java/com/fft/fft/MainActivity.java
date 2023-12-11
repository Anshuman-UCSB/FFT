package com.fft.fft;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FFT_main";
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Entered OnCreate");
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null){
            goToLogin();
        }

        if(savedInstanceState == null){
            setFragment(HomeFragment.class);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupDrawer(toolbar);
    }

    private void setupDrawer(Toolbar toolbar) {
        DrawerLayout drawerLayout = findViewById(R.id.root);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        MaterialDrawerSliderView slider = findViewById(R.id.slider);
        slider.getItemAdapter().add(
                makeItem("Home", R.drawable.home, 1, true, true),
                makeSection("ML Coaching", 102, true),
                makeItem("Bench", R.drawable.exercise_24px, 2, false, true),
                makeItem("Squat", R.drawable.exercise_24px, 3, false, true),
                makeItem("Deadlift", R.drawable.exercise_24px, 4, false, true)
        );

        slider.getFooterAdapter().add(
                new DividerDrawerItem(),
                makeItem("Settings", R.drawable.settings_24px, 11, true, true),
                makeItem("Logout", R.drawable.logout_24px, 12, true, false)
        );
        slider.setOnDrawerItemClickListener((v,di,p) ->{
            Log.i(TAG, "Item clicked "+di+" at pos "+p);
            processClick(p);
            return false;
        });
        slider.setSelectionAtPosition(0);
    }

    private void processClick(int pos){
        switch(pos){
            case 0: // home
                setFragment(HomeFragment.class);
                break;
            case 2: // Bench
                setFragment(PoseFragment.class);
                break;
            case 7: // logout
                new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle("Logout")
                        .setIcon(R.drawable.logout_24px)
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Logout", (d,w)->{
                            Log.i(TAG, "Logging out");
                            auth.signOut();
                            goToLogin();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                break;
        }
    }

    private void setFragment(Class<?> frag) {
        Log.i(TAG, "changing fragment to "+frag);
        Bundle bundle = new Bundle();
        String firstName = user.getDisplayName().split("\\W")[0];
        String uid = user.getUid();
        bundle.putString("name", firstName);
        bundle.putString("uid", uid);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, (Class<? extends Fragment>) frag, bundle)
                .commit();
    }

    private SectionDrawerItem makeSection(String name, long id, boolean divider) {
        SectionDrawerItem coaching = new SectionDrawerItem();
        coaching.setName(new StringHolder(name));
        coaching.setDivider(divider);
        coaching.setIdentifier(id);
        return coaching;
    }

    private AbstractBadgeableDrawerItem<?> makeItem(String name, Integer icon, long id, boolean primary, boolean selectable){
        AbstractBadgeableDrawerItem<?> n = primary?(new PrimaryDrawerItem()):(new SecondaryDrawerItem());
        n.setName(new StringHolder(name));
        n.setSelectable(selectable);
        if(icon!=null)
            n.setIcon(new ImageHolder(icon));
        n.setIdentifier(id);
        return n;
    }

    private void goToLogin(){
        Intent intent = new Intent(getApplication(), Login.class);
        startActivity(intent);
        finish();
    }
}