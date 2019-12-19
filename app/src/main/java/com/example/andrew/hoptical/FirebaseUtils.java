
package com.example.andrew.hoptical;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {

    private static FirebaseDatabase mDatabase;

    // Return our Firebase database reference if we have one
    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();

            // Enable offline storage and caching
            mDatabase.setPersistenceEnabled(true);
        }

        return mDatabase;
    }

}