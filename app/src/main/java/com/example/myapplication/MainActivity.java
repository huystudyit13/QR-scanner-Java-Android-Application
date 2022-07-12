package com.example.myapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.example.myapplication.fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    public static DatabaseHandler databaseHandler;
    BottomNavigationView bottomNavigationView;
    public static Boolean check_fragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_scan);

        databaseHandler = new DatabaseHandler(this);
    }

    ScanFragment scanFragment = new ScanFragment();
    GeneratorFragment generatorFragment = new GeneratorFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    InfoFragment infoFragment = new InfoFragment();
    ChooseImageFragment chooseImageFragment = new ChooseImageFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_scan:
                if (check_fragment) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, scanFragment).commit();
                } else {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, chooseImageFragment).commit();
                }
                return true;

            case R.id.navigation_generator:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, generatorFragment).commit();
                return true;

            case R.id.navigation_history:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, historyFragment).commit();
                return true;

            case R.id.navigation_info:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_screen, infoFragment).commit();
                return true;
        }
        return false;
    }

}