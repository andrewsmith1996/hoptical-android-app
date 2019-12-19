package com.example.andrew.hoptical;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class DiscoverBeerRecyclerAdapter extends RecyclerView.Adapter<DiscoverBeerRecyclerAdapter.ViewHolder>{

    private ArrayList<BeerEntry> beers;
    private Context appContext;

    public DiscoverBeerRecyclerAdapter(Context context, ArrayList<BeerEntry> beerObjects){
        beers = beerObjects;
        appContext = context;
    }


    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // Inflate our layout
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.beer_info_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        try{

            // Get the beer
            final BeerEntry beer = beers.get(i);

            // Set the text and description of the layout
            viewHolder.beerName.setText(beer.getBeerName());
            viewHolder.beerDescription.setText(beer.getBeerDescription());

            // Check for connection
            final ConnectivityManager connectivityManager = ((ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE));

            // If we have no connection then download the image from the REST API
            if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {

                // Use Picasso to download the image of the beer from the REST API
                Picasso.get().load(beer.getImageUrl()).into(viewHolder.beerImage);

            } else {

                // Get a reference to the context wrapper
                ContextWrapper cw = new ContextWrapper(appContext);

                // Get the image from the filepath
                File directory = cw.getDir("downloaded_hoptical_image", Context.MODE_PRIVATE);
                File imageFilePath = new File(directory, beer.getImageUrl() + ".jpg");

                // Does the file exist?
                if(imageFilePath.exists()){

                    // Finally set the image for this memory
                    viewHolder.beerImage.setImageDrawable(Drawable.createFromPath(imageFilePath.toString()));

                } else {

                    // Hide the image view
                    viewHolder.beerImage.setVisibility(View.GONE);
                }
            }


            // Add an on click listener for the "view more" button
            viewHolder.viewMoreButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view){

                    // Set up the intent
                    Intent beerDetailIntent = new Intent(appContext, DiscoverBeerDetail.class);

                    // Add the object as an extra
                    beerDetailIntent.putExtra("beerObject", beer);

                    // Start the activity
                    appContext.startActivity(beerDetailIntent);
                }
            });

        } catch(Exception e){
            Toast errorToast = Toast.makeText(appContext, "Sorry, we're unable to get any beer information right now.", Toast.LENGTH_SHORT);
            errorToast.show();
        }

    }

    @Override
    public int getItemCount() {
        return beers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView beerName;
        TextView beerDescription;
        ImageView beerImage;
        Button viewMoreButton;

        ConstraintLayout container;

        public ViewHolder(View itemView) {
            super(itemView);

            // Get references the layout widgets
            beerName = itemView.findViewById(R.id.beerInfoTitle);
            beerDescription = itemView.findViewById(R.id.beerInfoDescription);
            beerImage = itemView.findViewById(R.id.beerInfoImage);
            viewMoreButton = itemView.findViewById(R.id.viewMoreButton);
            container = itemView.findViewById(R.id.beerInfoParent);

        }
    }
}
