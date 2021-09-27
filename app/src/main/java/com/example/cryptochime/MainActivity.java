package com.example.cryptochime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();


        //Stop media Player
        if(NotificationBroadcast.mPlayer != null){
            NotificationBroadcast.mPlayer.stop();
        }



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    String fragmentTag = "";

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            fragmentTag = "Home";

                            break;
                        case R.id.nav_favorites:
                            selectedFragment = new FavoriteFragment();
                            fragmentTag = "Favorite";
                            break;
                        case R.id.nav_alert:
                            selectedFragment = new AlertFragment();
                            fragmentTag = "Alert";
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment, fragmentTag).commit();



                    return true;
                }
            };


}