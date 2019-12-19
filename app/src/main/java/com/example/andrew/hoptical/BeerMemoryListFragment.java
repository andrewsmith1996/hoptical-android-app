package com.example.andrew.hoptical;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// add below
import java.util.ArrayList;
import android.widget.*;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class BeerMemoryListFragment extends Fragment {

    private ArrayList<BeerMemory> beerList = new ArrayList<BeerMemory>();

    // Reference to our Recycler Adapter
    private BeerMemoryRecyclerAdapter adapter;

    public BeerMemoryListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {

        // Clear out our beers list so we can refresh it
        beerList.clear();

        // Get a reference to our recycler view
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.beerListRecyclerView);

        // Initialise our recycler view adapter
        adapter = new BeerMemoryRecyclerAdapter(getActivity(), beerList);

        // Set the adapter for the recycler view
        recyclerView.setAdapter(adapter);

        // Set the layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create a reference to our Firebase database
        DatabaseReference firebaseReference = FirebaseUtils.getDatabase().getReference();

        // Get our userID
        String userID = getUserId();

        // Do we have a signed in user?
        if(userID != null){

            // Query firebase for all memories with this ID
            Query query = firebaseReference.child("beer_memories").orderByChild("userID").equalTo(userID);

            // New event listener for our Firebase real-time database
            ValueEventListener postListener = new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Do we have data in our remote database?
                    if(dataSnapshot.exists() && dataSnapshot != null) {

                        beerList.clear();

                        // Loop through all our data and create our objects
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            // Create our new beer memory object
                            BeerMemory memory = postSnapshot.getValue(BeerMemory.class);

                            // Add our new object to our array
                            beerList.add(memory);

                        }

                    } else {

                        // We have no beer memories, so get the layout
                        View layout = view.findViewById(R.id.beerListParent);

                        Context context = getActivity();

                        // Create a text view to inform the user that there's no beer memories
                        TextView noMemoriesText = new TextView(context);

                        // Set the parameters for our text
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                        // Set the text and its appearance
                        noMemoriesText.setText("You don't currently have any beer memories. Hit the plus button in the bottom right to begin!");
                        noMemoriesText.setTextAppearance(android.R.style.TextAppearance_DeviceDefault);

                        // Position the text in the center of the screen
                        noMemoriesText.setGravity(Gravity.CENTER);
                        params.setMargins(16, 0, 16, 0);
                        // Actually set our parameters
                        noMemoriesText.setLayoutParams(params);

                        // Finally add our text view to the layout
                        ((FrameLayout) layout).addView(noMemoriesText);
                    }

                    // Notify our adapter that we have new data
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    // Show toast regarding connectivity
                    Context context = getActivity().getApplicationContext();

                    Toast connectivityToast = Toast.makeText(context, "Sorry, but we're unable to access the remote database", Toast.LENGTH_SHORT);
                    connectivityToast.show();

                }
            };

            // Add the event listener so we can check for real-time updates
            query.addValueEventListener(postListener);

        } else {

            Context context = getActivity();

            Toast connectivityToast = Toast.makeText(context, "Please make sure to sign into Hoptical", Toast.LENGTH_SHORT);
            connectivityToast.show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the BeerList fragment
        View v = inflater.inflate(R.layout.fragment_home_beer_list, container, false);

        return v;
    }

    // Returns userID
    public String getUserId(){

        // Get the userID of this user
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());

        String userID;

        if (acct != null) {
            userID = acct.getId();
        } else {
            userID = null;
        }

        return userID;
    }
}
