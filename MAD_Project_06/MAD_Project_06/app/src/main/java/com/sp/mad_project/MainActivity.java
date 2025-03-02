package com.sp.mad_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.mad_project.databinding.ActivityMainBinding;
import com.sp.mad_project.placeholder.FoodDBHelper;
import com.sp.mad_project.placeholder.FoodFragment;
import com.sp.mad_project.placeholder.MoreFragment;
import com.sp.mad_project.placeholder.PlanFragment;
import com.sp.mad_project.placeholder.DashboardFragment;
import com.sp.mad_project.placeholder.WeightHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Binding and layout setup
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new DashboardFragment());

        // Set up the toolbar
        setSupportActionBar(binding.toolbar);

        // Initialize the drawer layout and navigation view
        drawerLayout = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Default fragment setup
        if (savedInstanceState == null) {
            replaceFragment(new DashboardFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // Bottom navigation listener
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.Dashboard) {
                replaceFragment(new DashboardFragment());
            } else if (itemId == R.id.Food) {
                replaceFragment(new FoodFragment());
            } else if (itemId == R.id.Plans) {
                replaceFragment(new PlanFragment());
            } else if (itemId == R.id.More) {
                replaceFragment(new MoreFragment());
            }
            return true;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            replaceFragment(new DashboardFragment());
            binding.bottomNavigationView.setSelectedItemId(R.id.Dashboard);

        }  else if (itemId == R.id.nav_aboutus) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fitbro1/"));
            startActivity(intent);


        } else if (itemId == R.id.nav_logout) {
            // Clear data from the tables
            FoodDBHelper foodDBHelper = FoodDBHelper.instanceOfDatabase(this);
            foodDBHelper.deleteAllFoods(); // Clears food items data

            WeightHelper weightHelper = new WeightHelper(this);
            weightHelper.deleteAllWeights(); // Clears weight data

            // Sign out the user and start the Login activity
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            Log.e("MainActivity", "log out of user main activity");

            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_main, fragment);
        fragmentTransaction.commit();
    }
}
