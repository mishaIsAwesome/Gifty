package com.inti.gifty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        bottomNavigationView.setSelectedItemId(R.id.home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Home()).commit();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (current instanceof Home || current instanceof Browse || current instanceof Wishlists || current instanceof Orders || current instanceof Profile){
                    bottomNavigationView.setVisibility(View.VISIBLE);
                } else
                    bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            getSupportFragmentManager().popBackStack("MAIN", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.browse:
                    selectedFragment = new Browse();
                    break;
                case R.id.wishlists:
                    selectedFragment = new Wishlists();
                    break;
                case R.id.home:
                    selectedFragment = new Home();
                    break;
                case R.id.orders:
                    selectedFragment = new Orders();
                    break;
                case R.id.profile:
                    selectedFragment = new Profile();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).addToBackStack("MAIN").commit();

            return true;
        }
    };
}