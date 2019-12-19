package com.example.andrew.hoptical;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.text.TextUtils.isEmpty;

public class AddBeerMemoryActivity extends AppCompatActivity {

    // Google Play Services API provider for location
    private FusedLocationProviderClient mFusedLocationClient;

    // Object variables
    public String beerName;
    public String breweryName;
    public Bitmap beerImage;
    public String locationString;
    public String contact;
    public String imageFilePath;
    public String notes;

    // Geolocation Variables
    public Double longVal = 0.0;
    public Double latVal = 0.0;

    // Reference to Firebase
    private DatabaseReference firebaseDatabase;

    // Reference to app's context
    private Context appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beer);

        // Set the context
        appContext = this;

        // Set up the top toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add Beer");

        // Set the Google Play Services location service provider
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialise the reference to Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    }

    // On save memory
    public void onSaveMemoryClick(View view){

        // Get a reference to the text fields
        EditText beerNameTextField = (EditText)findViewById(R.id.beerName);
        EditText breweryTextField = (EditText)findViewById(R.id.beerBrewery);
        EditText tastingNotesField = (EditText)findViewById(R.id.tastingNotes);

        // Get the text field text
        beerName = beerNameTextField.getText().toString();
        breweryName = breweryTextField.getText().toString();
        notes = tastingNotesField.getText().toString();

        // Get the current date/time for our filename
        Date currentTime = Calendar.getInstance().getTime();

        // Format the current date into a string for the database
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(currentTime);

        // Get the bitmap image from our ImageView
        ImageView memoryImage = (ImageView)findViewById(R.id.beerImageView);

        // Have we filled in our necessary fields?
        if((beerName != null && !beerName.isEmpty()) && (breweryName != null && !breweryName.isEmpty()) && (notes != null && !notes.isEmpty())) {

            if(beerImage != null){

                // Call our function to get the stripped down filename
                imageFilePath = getFileName(beerName);

                // Append the date identifier to our filename
                imageFilePath += date;

                // Get the Bitmap
                Bitmap bitmap = ((BitmapDrawable)memoryImage.getDrawable()).getBitmap();

                // Call our function to save it to local storage
                saveImageToStorage(bitmap, imageFilePath);

            } else {
                // No image has been set, so don't add an image
                imageFilePath = null;
            }

            // Get userID
            String userID = getUserId();

            // Do we have a signed in user?
            if(userID != null){

                // Reformat the date for the memory
                DateFormat memoryFormat = new SimpleDateFormat("dd/MM/yyyy");
                String memoryDate = memoryFormat.format(currentTime);

                // Create the beer memory object
                BeerMemory memory = new BeerMemory(beerName, breweryName, memoryDate, contact, locationString, imageFilePath, notes, userID);

                // Firstly let's sync our object up to our Firebase remote database
                firebaseDatabase.child("beer_memories").push().setValue(memory);

                // Finish our activity
                finish();

            } else {

                // User isn't signed in, show error message
                Toast connectivityToast = Toast.makeText(this, "Please make sure to sign into Hoptical", Toast.LENGTH_SHORT);
                connectivityToast.show();
            }


        } else {

            // User hasn't filled in all correct fields, show error
            Toast connectivityToast = Toast.makeText(this, "Please fill in all the information.", Toast.LENGTH_SHORT);
            connectivityToast.show();
        }
    }

    public void onAddImageClick(View view){

        // Request the permission for the camera
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    // Handles our users permission options
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    int REQUEST_IMAGE_CAPTURE = 1;

                    // Start Intent for taking a picture
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }

                } else {

                    // Permission has been denied, show Toast
                    Context context = this;
                    Toast toast = Toast.makeText(context, "You'll need to allow permission for this camera feature.", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return;
            }

            case 2: {

                // Had permission been granted for location access?
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Get the last known location of the user
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {

                            String drinkingLocation = null;

                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                // Get the latitude and longitude
                                longVal = location.getLongitude();
                                latVal = location.getLatitude();

                                // Geocode the location to get the plaintext city name
                                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = null;

                                // Try and get the location
                                try {
                                    addresses = gcd.getFromLocation(latVal, longVal, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                // Do we have a location
                                if (addresses != null && addresses.size() > 0) {

                                    // Get the first line of the address
                                    locationString = addresses.get(0).getAddressLine(0);

                                    // Do we have a first name of the location?
                                    if(locationString != null) {
                                        drinkingLocation = "You are drinking in: " + locationString;
                                    } else {
                                        drinkingLocation = Double.toString(latVal) + ", " + Double.toString(longVal);
                                    }

                                } else {

                                    // Error getting location
                                    Context context = getApplicationContext();
                                    Toast toast = Toast.makeText(context, "Sorry, but we're unable to find your location. Please make sure you're connected to the Internet and your phone has your last known location.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }

                            } else {

                                // Error getting location
                                Context context = getApplicationContext();
                                Toast toast = Toast.makeText(context, "Sorry, but we're unable to find your location. Please make sure you're connected to the Internet and your phone has your last known location.", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                            // Do we have a location set?
                            if(drinkingLocation != null && !isEmpty(drinkingLocation)){

                                // Set the text to the location
                                TextView locationText = (TextView)findViewById(R.id.locationText);
                                locationText.setText(drinkingLocation);
                            }

                        }
                    });

                } else {

                    // Permission has been denied, show Toast
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "You'll need to allow permission for this location feature.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                return;
            }



        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Handle the activity result for the camera
        if (requestCode == 1 && resultCode == RESULT_OK) {

            // Get the image data
            Bundle extras = data.getExtras();
            beerImage = (Bitmap) extras.get("data");

            // Set the image thumbnail to the captured picture
            ImageView thumbnailView = (ImageView)findViewById(R.id.beerImageView);
            thumbnailView.setImageBitmap(beerImage);
        }

        // Handle the activity result for the contact pick
        if (requestCode == 3 && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            // Set the data we're looking for
            String[] projection = {
                    ContactsContract.Data._ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            };

            // Get the data using the projection we specified
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            // Get the display name of the selected contact
            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            contact = cursor.getString(nameColumnIndex);

            // Set the button's text to be the selected contact's name
            Button buddyButton = (Button)findViewById(R.id.drinkingBuddyButton);
            buddyButton.setText("Drank with \n" + contact);

        }
    }

    public void onGetLocationClick(View view){

        // Check for location permissions
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
    }

    public void onChooseContactClick(View view){
        // Start the select contact activity
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 3);
    }

    public String saveImageToStorage(Bitmap bitmapImage, String fileName){

        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("hoptical_image", Context.MODE_PRIVATE);

        // Create our image directory
        File imagePath = new File(directory,fileName + ".jpg");

        FileOutputStream fos = null;

        // Write the image to storage
        try {
            // Create our output file stream
            fos = new FileOutputStream(imagePath);

            // Compress the image
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            try {
                // Close our file steam
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return directory.getAbsolutePath();
    }

    public String getFileName(String originalFileName) {
        return originalFileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    // Returns userID
    public String getUserId(){

        // Get the userID of this user
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String userID;

        if (acct != null) {
            userID = acct.getId();
        } else {
            userID = null;
        }

        return userID;
    }
}
