package com.example.andrew.hoptical;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.text.TextUtils.isEmpty;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Add the event listener for the sign in button
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Configure sign-in to request the user's ID, email address, and basic profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Create the google sign in button
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

    }

    @Override
    public void onStart() {
        super.onStart();

        // Check for existing Google Sign In account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent;
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            // Get the result of the login
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);

        } catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            Log.w("Exception", "signInResult:failed code=" + e.getStatusCode());

            // Show an error toast
            CharSequence errorText = "Please make sure you're connected to the internet and you're on the latest version of Google Play Services.";

            Toast errorToast = Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_SHORT);
            errorToast.show();

            updateUI(null);
        }
    }

    private void signIn() {

        // Start the Google sign in intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs aren't available
        Log.d("Exception", "onConnectionFailed:" + connectionResult);

        // Show an error toast
        CharSequence errorText = "There's been an error signing you in. Please try again later.";

        Toast errorToast = Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_SHORT);
        errorToast.show();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(GoogleSignInAccount signedIn) {
        if (signedIn != null) {

            // Hide and show the correct buttons
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);

            // Get user name
            String userName = getUserName();

            // Show a welcome message
            if(userName != null && !isEmpty(userName)){

                // Show a sign out message toast
                CharSequence connectivityText = "Welcome " + userName;

                // Show toast
                Toast connectivityToast = Toast.makeText(getApplicationContext(), connectivityText, Toast.LENGTH_SHORT);
                connectivityToast.show();
            }

            // User has logged in so start the main activity
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);

        } else {
            // Hide the button
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    // Sign in button click listener
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    // Returns userID
    public String getUserName(){

        // Get the userID of this user
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String userName;

        if (acct != null) {
            userName = acct.getGivenName();
        } else {
            userName = null;
        }

        return userName;
    }
}
