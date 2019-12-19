package com.example.andrew.hoptical;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class DiscoverBeerHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_info_home);

        // Setup the top toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Allow the user to use the toolbar to go back
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup the toolbar's title
        actionBar.setTitle("Discover Beer");

        // Initialise the information fragment by adding the fragment manager
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.beerInfoContainer);

        // If we don't have a fragment, create one
        if (fragment == null) {
            fragment = new DiscoverBeerFragment();
            fm.beginTransaction().add(R.id.beerInfoContainer, fragment).commit();
        }
    }

}
