package com.example.andrew.hoptical;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup the main toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(myToolbar);

        // Initialise the beerlist fragment by adding the fragment manager
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.beerListContainer);

        // If we don't have a fragment, create one
        if (fragment == null) {
            fragment = new BeerMemoryListFragment();
            fm.beginTransaction().add(R.id.beerListContainer, fragment).commit();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        // Refresh the fragment to check if we have a new beer memory entry
        Fragment fm = null;

        // Get the fragment manager
        fm = getSupportFragmentManager().findFragmentById(R.id.beerListContainer);
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        // Detach the fragment
        fragmentTransaction.detach(fm);

        // Reattach the fragment
        fragmentTransaction.attach(fm);

        // Re commit the fragment
        fragmentTransaction.commit();

    }

    // When the user adds a beer
    public void onAddBeerClick(View view){
        Intent addBeerIntent = new Intent(this, AddBeerMemoryActivity.class);
        startActivity(addBeerIntent);
    }

    // Handle the top toolbar functionality
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_beer_info:

                Intent beer_info_intent = new Intent(this, DiscoverBeerHome.class);
                startActivity(beer_info_intent);

                return true;

            case R.id.action_sign_out:

                // Request user details
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options in gso
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

                // Add on complete listener for when user has signed out
                mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Show a sign out message toast
                        CharSequence connectivityText = "Successfully signed out";
                        Context context = getApplicationContext();

                        Toast connectivityToast = Toast.makeText(context, connectivityText, Toast.LENGTH_SHORT);
                        connectivityToast.show();
                    }
                });

                // Create the Intent to take the user back to the login screen
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);

                return true;

            default:

                // User action not recognised
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}