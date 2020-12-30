package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        

        BottomNavigationView bottomnav = findViewById(R.id.bottom_navigation);

        bottomnav.setOnNavigationItemSelectedListener(navlistner);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new Dashboard()).commit();



        if(user == null){
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }



    private BottomNavigationView.OnNavigationItemSelectedListener navlistner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectfragment = null;
                    switch (item.getItemId()){

                        case R.id.nav_dashboard:
                            selectfragment = new Dashboard();
                            break;
                        case  R.id.nav_profile:
                            selectfragment = new Profile();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectfragment).commit();
                    return true;
                }
            };


}