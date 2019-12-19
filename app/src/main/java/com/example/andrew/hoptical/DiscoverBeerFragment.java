package com.example.andrew.hoptical;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

// GSON
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

public class DiscoverBeerFragment extends Fragment {

    private ArrayList<BeerEntry> beers = new ArrayList<>();
    private ProgressBar spinner;
    private DiscoverBeerRecyclerAdapter adapter;
    private Context appContext;

    public DiscoverBeerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getContext();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beer_information, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Get a reference to our loading spinner
        spinner = (ProgressBar)view.findViewById(R.id.progressBar);

        // Get a reference for connectivity manager
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE));

        // Check if we have an internet connection
        if(connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()){

            // Execute the function to get our API data
            callApi();

        } else {

            // We don't have an internet connection, so let's remove the spinner
            spinner.setVisibility(View.GONE);

            // Show toast regarding connectivity
            Toast connectivityToast = Toast.makeText(getActivity().getApplicationContext(), "Get online for the latest beer information!", Toast.LENGTH_SHORT);
            connectivityToast.show();

            // If we don't have an internet connection, then load the database files
            SQLiteDatabase.loadLibs(getActivity());
            SQLiteDatabase db = BeerDbHelper.getInstance(getActivity()).getReadableDatabase("Hoptical1797!");

            // Get a cursor for our beer information data we're getting from local storage
            Cursor beerInformationCursor = db.rawQuery("SELECT * FROM beer_information", null);

            // Get the number of entries
            int count = beerInformationCursor.getCount();

            // Do we have any beer information?
            if (count != 0) {

                // Clear out our beer array so we can refresh the list
                beers.clear();

                // Move to our first item in the cursor
                beerInformationCursor.moveToFirst();

                // Go through all the beers rows we've retrieved from the database
                while (!beerInformationCursor.isAfterLast()) {

                    // Create our food pairings array
                    ArrayList<String> food_pairings = new ArrayList<>();

                    // Get all the information that we want from the cursor
                    Integer id = beerInformationCursor.getInt(beerInformationCursor.getColumnIndex("id"));
                    String title = beerInformationCursor.getString(beerInformationCursor.getColumnIndex("name"));
                    String description = beerInformationCursor.getString(beerInformationCursor.getColumnIndex("description"));
                    Double ABV = beerInformationCursor.getDouble(beerInformationCursor.getColumnIndex("abv"));
                    String image_url = beerInformationCursor.getString(beerInformationCursor.getColumnIndex("image_url"));

                    // Clear out the food pairings array so we can start it again now we may have new data
                    food_pairings.clear();

                    // Get all the information from the food pairings table for that particular beer entry
                    Cursor foodPairingCursor = db.rawQuery("SELECT * FROM " + FoodPairingContract.FoodPairingEntry.TABLE_NAME + " WHERE " + FoodPairingContract.FoodPairingEntry.COLUMN_NAME_BEER_INFO_ID + "= " + id, null);

                    // Move to item one in the cursor
                    foodPairingCursor.moveToFirst();

                    // Get all the food pairing rows for this beer
                    while (!foodPairingCursor.isAfterLast()) {

                        // Get the individual string
                        String food_pairing = foodPairingCursor.getString(foodPairingCursor.getColumnIndex("body"));

                        // Add this to the array
                        food_pairings.add(food_pairing);

                        // Move onto the next part of the cursor
                        foodPairingCursor.moveToNext();
                    }

                    // Create a beer entry object from this data
                    BeerEntry newBeer = new BeerEntry(title, description, ABV, food_pairings, image_url);

                    // Add this new beer to the array
                    beers.add(newBeer);

                    // Close this instance of the food pairings cursor
                    foodPairingCursor.close();

                    // Move onto the next beer
                    beerInformationCursor.moveToNext();
                }
            }

            // Close off the cursor
            beerInformationCursor.close();
        }


        // Get the reference for our recycler view that we'll be populating
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.beerInfoRecyclerView);

        // Create our adapter for the recycler view
        adapter = new DiscoverBeerRecyclerAdapter(getActivity(), beers);

        // Set the adapter for the recycler view
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.notifyDataSetChanged();
    }

    public void callApi(){

        // Set the URL for the Rest API we want to call
        String api_url = "https://api.punkapi.com/v2/beers?per_page=15";

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api_url,

            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    // Remove the loading spinner
                    spinner.setVisibility(View.GONE);

                    // Clear our old beer list, as we're creating a new one
                    beers.clear();

                    // Create GSON object to parse our JSON data
                    Gson gson = new Gson();

                    // Create an array of beer objects from the JSON (using the GSON library)
                    TypeToken<ArrayList<BeerEntry>> token = new TypeToken<ArrayList<BeerEntry>>() {};
                    ArrayList<BeerEntry> beers_new = gson.fromJson(response, token.getType());

                    // Add all these beers to our array
                    beers.addAll(beers_new);

                    // Load our database files
                    SQLiteDatabase.loadLibs(getActivity());
                    SQLiteDatabase db = BeerDbHelper.getInstance(getActivity()).getWritableDatabase("Hoptical1797!");

                    // Delete the pre existing entries in the local database so we can refresh them
                    db.execSQL("DELETE FROM " + BeerEntryContract.BeerInfoEntry.TABLE_NAME);
                    db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = '" +  BeerEntryContract.BeerInfoEntry.TABLE_NAME + "'");

                    db.execSQL("DELETE FROM " + FoodPairingContract.FoodPairingEntry.TABLE_NAME);
                    db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = '" +  FoodPairingContract.FoodPairingEntry.TABLE_NAME+ "'");

                    // Write all the beers to the database
                    for(BeerEntry item : beers) {

                        // Construct the filename
                        String filename = getFileName(item.getBeerName());

                        // Download the image of the beer from the REST API
                        new DiscoverBeerFragment.DownloadImage(filename).execute(item.getImageUrl());

                        // Get the information from the object that we'll be wanting to insert
                        ContentValues beerInfoValues = new ContentValues();
                        beerInfoValues.put(BeerEntryContract.BeerInfoEntry.COLUMN_NAME_NAME, item.getBeerName());
                        beerInfoValues.put(BeerEntryContract.BeerInfoEntry.COLUMN_NAME_DESCRIPTION, item.getBeerDescription());
                        beerInfoValues.put(BeerEntryContract.BeerInfoEntry.COLUMN_NAME_ABV, item.getBeerABV());
                        beerInfoValues.put(BeerEntryContract.BeerInfoEntry.COLUMN_NAME_IMAGE_URL, filename);

                        // Insert the entry and return its ID
                        long beerId = db.insert(BeerEntryContract.BeerInfoEntry.TABLE_NAME, null, beerInfoValues);

                        // Write all the food pairings for this beer into the food pairing table
                        for(String foodPairing : item.getBeerFoodPairings()) {

                            // Get the information for this food pairing
                            ContentValues foodPairingValues = new ContentValues();
                            foodPairingValues.put(FoodPairingContract.FoodPairingEntry.COLUMN_NAME_BODY, foodPairing);
                            foodPairingValues.put(FoodPairingContract.FoodPairingEntry.COLUMN_NAME_BEER_INFO_ID, beerId);

                            // Insert the food pairing information into the food pairing database
                            db.insert(FoodPairingContract.FoodPairingEntry.TABLE_NAME, null, foodPairingValues);
                        }
                    }

                    // Close off our database connection
                    db.close();

                    // Tell our recycler view adapter that we have new data!
                    adapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                        Toast errorToast = Toast.makeText(getContext(), "Sorry, your connection has timed out, please try again.", Toast.LENGTH_SHORT);
                        errorToast.show();

                    } else if (error instanceof AuthFailureError) {
                        Toast errorToast = Toast.makeText(getContext(), "Sorry, there's been an authentication error, please try again.", Toast.LENGTH_SHORT);
                        errorToast.show();

                    } else if (error instanceof ServerError) {
                        Toast errorToast = Toast.makeText(getContext(), "Sorry, there's been a server error, please try again.", Toast.LENGTH_SHORT);
                        errorToast.show();

                    } else if (error instanceof NetworkError) {
                        Toast errorToast = Toast.makeText(getContext(), "Sorry, there's been a network error, please try again.", Toast.LENGTH_SHORT);
                        errorToast.show();

                    } else if (error instanceof ParseError) {
                        Toast errorToast = Toast.makeText(getContext(), "Sorry, we're unable to parse the data, please try again.", Toast.LENGTH_SHORT);
                        errorToast.show();
                    }
                }
            });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    // Async task to download the beer image
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String filename;

        public DownloadImage(String imageFileName) {
            filename = imageFileName;
        }

        protected Bitmap doInBackground(String... urls) {

            // Get the URL
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();

                // Show error toast
                Toast connectivityToast = Toast.makeText(getActivity().getApplicationContext(), "Unable to download image. Please try again.", Toast.LENGTH_SHORT);
                connectivityToast.show();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            // Save the image to storage
            saveImageToStorage(result, filename);
        }
    }

    public String saveImageToStorage(Bitmap bitmapImage, String fileName){

        ContextWrapper cw = new ContextWrapper(appContext);

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("downloaded_hoptical_image", Context.MODE_PRIVATE);

        // Create our image directory
        File imagePath = new File(directory,fileName + ".jpg");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(imagePath);

            // Compress the image
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();

            // Show error toast
            Toast connectivityToast = Toast.makeText(getActivity().getApplicationContext(), "Unable to save image. Please try again.", Toast.LENGTH_SHORT);
            connectivityToast.show();

        } finally {

            try {
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();

                // Show error
                Toast connectivityToast = Toast.makeText(getActivity().getApplicationContext(), "Unable to close the file. Please try again.", Toast.LENGTH_SHORT);
                connectivityToast.show();
            }
        }

        return directory.getAbsolutePath();
    }

    public String getFileName(String originalFileName) {
        return originalFileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_").toLowerCase();
    }

}


