package com.example.andrew.hoptical;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class BeerMemoryRecyclerAdapter extends RecyclerView.Adapter<BeerMemoryRecyclerAdapter.ViewHolder>{

    private ArrayList<BeerMemory> beerList;
    private Context appContext;


    public BeerMemoryRecyclerAdapter(Context context, ArrayList<BeerMemory> beers){
        beerList = beers;
        appContext = context;
    }


    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        // Inflate the view
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_beer_entry, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        // Get the beer
        final BeerMemory beerMemory = beerList.get(i);

        viewHolder.beerTitle.setText(beerMemory.getMemoryBeerTitle());
        viewHolder.beerBrewery.setText(beerMemory.getMemoryBeerBrewery());
        viewHolder.beerDate.setText(beerMemory.getMemoryDate());

        // Do we have a location for this memory? If so, add it to the card
        if(beerMemory.getMemoryLocation() != null){
            viewHolder.beerLocation.setText(beerMemory.getMemoryLocation());

        } else {

            // Hide the text
            viewHolder.beerLocation.setVisibility(View.GONE);
        }

        // Does this beer memory have an image?
        if(beerMemory.getMemoryImageFilePath() != null){

            // Get a reference to the context wrapper
            ContextWrapper cw = new ContextWrapper(appContext);

            try {
                // Get the image from the filepath
                File directory = cw.getDir("hoptical_image", Context.MODE_PRIVATE);
                File imageFilePath = new File(directory, beerMemory.getMemoryImageFilePath() + ".jpg");

                // Finally set the image for this memory
                viewHolder.beerImage.setImageDrawable(Drawable.createFromPath(imageFilePath.toString()));

            } catch(Exception e){

                // Couldn't get the image, show an error message
                Toast errorToast = Toast.makeText(appContext, "Sorry, there's been an error retrieving an image", Toast.LENGTH_SHORT);
                errorToast.show();

                // Hide the ImageView
                viewHolder.beerImage.setVisibility(View.GONE);
            }

        } else {

            // Hide the ImageView
            viewHolder.beerImage.setVisibility(View.GONE);
        }

        // Add an on click listener for the "view more" button
        viewHolder.viewMemoryButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){

                // Set up the intent
                Intent beerMemoryIntent = new Intent(appContext, BeerMemoryDetail.class);

                // Add the object as an extra
                beerMemoryIntent.putExtra("beerMemoryObject", beerMemory);

                // Start the activity
                appContext.startActivity(beerMemoryIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // Get references to our widgets
        TextView beerTitle;
        TextView beerBrewery;
        TextView beerDate;
        TextView beerLocation;
        Button viewMemoryButton;
        ConstraintLayout container;
        ImageView beerImage;

        public ViewHolder(View itemView) {
            super(itemView);

            // Instantiate our widgets
            beerTitle = itemView.findViewById(R.id.beerDetailTitle);
            beerBrewery = itemView.findViewById(R.id.beerDetailBrewery);
            beerDate = itemView.findViewById(R.id.beerMemoryDate);
            beerLocation = itemView.findViewById(R.id.beerDetailLocation);
            viewMemoryButton = itemView.findViewById(R.id.viewMemoryButton);
            container = itemView.findViewById(R.id.beerListParent);
            beerImage = itemView.findViewById(R.id.beerMemoryImage);


        }
    }


}
