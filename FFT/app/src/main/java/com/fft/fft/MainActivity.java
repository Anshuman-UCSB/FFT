package com.fft.fft;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

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
    private Button button;
    private TextView greeter;
    private FirebaseUser user;
    private Toolbar toolbar;

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

        setupDrawer(toolbar);

        button.setOnClickListener(v->{
            auth.signOut();
            goToLogin();
        });
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
                makeItem("Home", R.drawable.home, 1, true),
                makeSection("ML Coaching", 102, true),
                makeItem("Bench", R.drawable.home, 2, false),
                makeItem("Squat", R.drawable.home, 3, false),
                makeItem("Deadlift", R.drawable.home, 4, false)
        );

        slider.getFooterAdapter().add(
                new DividerDrawerItem(),
                makeItem("Settings", R.drawable.settings_24px, 9, true)
        );
        slider.setOnDrawerItemClickListener((v,di,p) ->{
            Log.i(TAG, "Item clicked "+di+" at pos "+p);
            return false;
        });
    }

    private SectionDrawerItem makeSection(String name, long id, boolean divider) {
        SectionDrawerItem coaching = new SectionDrawerItem();
        coaching.setName(new StringHolder(name));
        coaching.setDivider(divider);
        coaching.setIdentifier(id);
        return coaching;
    }

    private AbstractBadgeableDrawerItem<?> makeItem(String name, int icon, long id, boolean primary){
        AbstractBadgeableDrawerItem<?> n = primary?(new PrimaryDrawerItem()):(new SecondaryDrawerItem());
        n.setName(new StringHolder(name));
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