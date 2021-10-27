package com.example.cryptochime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;

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

        startAlarm(this);
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
                        case R.id.nav_alert2:
                            selectedFragment = new AlertFragment();
                            fragmentTag = "Alert2";
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment, fragmentTag).commit();



                    return true;
                }
            };


    //Dynamic Decimal Format for price
    public static DecimalFormat dynDF(double price){
        DecimalFormat dyndf;
        if (price >= 10){
            dyndf = new DecimalFormat("#.##");
        } else if (price >= 1){
            dyndf = new DecimalFormat("#.###");
        }else if (price >= 0.1){
            dyndf = new DecimalFormat("#.####");
        }else if (price >= 0.01){
            dyndf = new DecimalFormat("#.#####");
        } else if (price >= 0.001){
            dyndf = new DecimalFormat("#.######");
        } else {
            dyndf = new DecimalFormat("#.########");
        }
        return dyndf;
    }

    public void startAlarm(Context context){
        //Toast.makeText(context, "Reminder Set!", Toast.LENGTH_SHORT).show();
        int alertId = 0;

        Intent intent = new Intent(context, NotificationBroadcast.class );
        intent.putExtra("alertId", alertId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        //prefNotify.edit().putInt("isNotified", 0).apply();
        long interval = 5000;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                1000, interval,
                pendingIntent);

    }





}