package com.example.andrew.hoptical;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;

public class DiscoverBeerDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_detail);

        try {

            // Get the beer object we've passed
            BeerEntry beer = (BeerEntry)getIntent().getSerializableExtra("beerObject");

            // Setup the top toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();

            // Allow the user to use the toolbar to go back
            actionBar.setDisplayHomeAsUpEnabled(true);

            // Setup the toolbar's title
            actionBar.setTitle(beer.getBeerName());

            // Set the title text
            TextView beerTitle = (TextView)findViewById(R.id.beerDetailTitle);
            beerTitle.setText(beer.getBeerName());

            // Set the ABV text
            TextView beerABV = (TextView)findViewById(R.id.beerDetailABV);
            beerABV.setText(Double.toString(beer.getBeerABV()) + "%");

            // Set the description text
            TextView beerDescription = (TextView)findViewById(R.id.beerDetailDescription);
            beerDescription.setText(beer.getBeerDescription());

            // Get connectivity manager
            final ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));

            // Do we have an internet connection? If not then download the image from the REST API
            if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {

                // Get a reference to the beer image
                ImageView beerDetailImage = findViewById(R.id.beerDetailImageView);

                // Use Picasso to download the image of the beer from the REST API
                Picasso.get().load(beer.getImageUrl()).into(beerDetailImage);

            } else {

                // Get a reference to the context wrapper
                ContextWrapper cw = new ContextWrapper(this);

                // Get the image from the filepath
                File directory = cw.getDir("downloaded_hoptical_image", Context.MODE_PRIVATE);
                File imageFilePath = new File(directory, beer.getImageUrl() + ".jpg");

                // Finally set the image for this memory
                ImageView beerDetailImageView = findViewById(R.id.beerDetailImageView);

                // Does the file exist?
                if(imageFilePath.exists()){

                    // Set the image into the view
                    beerDetailImageView.setImageDrawable(Drawable.createFromPath(imageFilePath.toString()));

                } else {

                    // Hide the image view
                    beerDetailImageView.setVisibility(View.GONE);
                }

            }

            // Get the linear layout of the scroll view we want to dynamically add items to
            LinearLayout beerPairingView = (LinearLayout)findViewById(R.id.beerDetailFoodPairingView);

            // Add each food pairing text to scrollview
            for (String item : beer.getBeerFoodPairings()) {

                // Set the margin parameters
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(4, 8, 0, 0);

                // Create the text view
                TextView textView = new TextView(this);

                // Set the text view's text
                textView.setText("- " + item);

                // Set the text appearance and the margin
                textView.setTextAppearance(android.R.style.TextAppearance_Material_Small);

                textView.setLayoutParams(params);

                // Finally add the text view to the scrollview
                beerPairingView.addView(textView);
            }
        } catch (Exception e){

            Toast errorToast = Toast.makeText(getApplicationContext(), "Unavailable to get Beer details.", Toast.LENGTH_SHORT);
            errorToast.show();
        }

    }

}
