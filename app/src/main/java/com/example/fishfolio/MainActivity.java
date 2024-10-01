package com.example.fishfolio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Asking for notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        //Starting the Notification Service.
        Intent intent = new Intent(MainActivity.this, NotificationService.class);
        startService(intent);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavBar);
        FrameLayout frameLayout = findViewById(R.id.frameLayout);

        View rootLayout = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            // Get system insets (including navigation bar and status bar)
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Set padding for the root layout to avoid overlap with the status bar
            v.setPadding(0, systemBarsInsets.top, 0, 0);  // Only top padding for status bar

            // Adjust bottom padding for the BottomNavigationView based on the navigation type
            ViewGroup bottomNavBar = findViewById(R.id.bottomNavBar);

            // If there are bottom insets (indicating the system navigation bar), apply padding
            if (systemBarsInsets.bottom > 0) {
                bottomNavBar.setPadding(0, 0, 0, systemBarsInsets.bottom);
            } else {
                // No system navigation bar (gesture navigation) — reset any previous padding
                bottomNavBar.setPadding(0, 0, 0, 0);
            }

            return WindowInsetsCompat.CONSUMED;
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int item_id = menuItem.getItemId();

                if(item_id == R.id.navHome){
                    loadfragment(new HomeFragment(),false);
                } else if (item_id == R.id.navProfile) {
                    loadfragment(new ProfileFragment(),false);
                } else if (item_id == R.id.navSettings) {
                    loadfragment(new SettingsFragment(),false);
                }

                return true;
            }

        });

        if (savedInstanceState == null) {
            loadfragment(new HomeFragment(), true);
        }


    }
    public void loadfragment(Fragment fragment, boolean isAppInitialize){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(isAppInitialize){
            fragmentTransaction.add(R.id.frameLayout, new HomeFragment());
        }else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }
        fragmentTransaction.commit();
    }
}