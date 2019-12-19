package com.example.andrew.hoptical;

import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class BeerMemoryDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);

        // Get the beer object we've passed
        BeerMemory beer = (BeerMemory) getIntent().getSerializableExtra("beerMemoryObject");

        // Setup the top toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();

        // Allow the user to use the toolbar to go back
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup the toolbar's title
        actionBar.setTitle(beer.getMemoryBeerTitle());

        // Set the title text
        TextView memoryBeerTitle = (TextView) findViewById(R.id.beerMemoryTitle);
        memoryBeerTitle.setText(beer.getMemoryBeerTitle());

        // Set the brewery title
        TextView memoryBeerBrewery = (TextView) findViewById(R.id.beerBreweryTitle);
        memoryBeerBrewery.setText(beer.getMemoryBeerBrewery());

        // Set the brewery title
        TextView memoryBeerDate = (TextView) findViewById(R.id.beerDate);
        memoryBeerDate.setText("Drank on " + beer.getMemoryDate());

        TextView memoryLocation = (TextView) findViewById(R.id.beerMemoryLocation);

        if (beer.getMemoryLocation() != null) {

            // Set the location text
            memoryLocation.setText("Drank at " + beer.getMemoryLocation());

        } else {
            memoryLocation.setVisibility(View.GONE);
        }

        TextView memoryDrinkingBuddy = (TextView) findViewById(R.id.beerMemoryDrinkingBuddy);

        if (beer.getMemoryDrinkingBuddy() != null) {

            // Set the drinking buddy text
            memoryDrinkingBuddy.setText("Drank with " + beer.getMemoryDrinkingBuddy());

        } else {
            memoryDrinkingBuddy.setVisibility(View.GONE);
        }

        // Set the description text
        TextView memoryNotes = (TextView) findViewById(R.id.beerMemoryNotes);

        if(memoryNotes != null){
            memoryNotes.setText(beer.getMemoryNotes());
        }

        // Get the image view
        ImageView beerImage = (ImageView) findViewById(R.id.beerMemoryImageView);

        // Does this beer memory have an image?
        if (beer.getMemoryImageFilePath() != null) {

            beerImage = (ImageView) findViewById(R.id.beerMemoryImageView);

            // Get a reference to the context wrapper
            ContextWrapper cw = new ContextWrapper(this);

            // Get the image from the filepath
            File directory = cw.getDir("hoptical_image", Context.MODE_PRIVATE);
            File imageFilePath = new File(directory, beer.getMemoryImageFilePath() + ".jpg");

            // Finally set the image for this memory
            beerImage.setImageDrawable(Drawable.createFromPath(imageFilePath.toString()));
            beerImage.setScaleType(ImageView.ScaleType.FIT_XY);

        } else {

            // Hide the image view
            beerImage.setVisibility(View.GONE);
        }
    }
}
