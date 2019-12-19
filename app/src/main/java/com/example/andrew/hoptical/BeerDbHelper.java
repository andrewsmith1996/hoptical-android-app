package com.example.andrew.hoptical;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

public class BeerDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Hoptical";

    private static BeerDbHelper instance;

    public BeerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {

        // Create our tables
        db.execSQL(SQL_CREATE_INFO_ENTRIES);
        db.execSQL(SQL_CREATE_FOOD_PAIRING_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // If we're upgrading our database then drop our tables
        db.execSQL("DROP TABLE IF EXISTS " + BeerEntryContract.BeerInfoEntry.TABLE_NAME );
        db.execSQL("DROP TABLE IF EXISTS " + FoodPairingContract.FoodPairingEntry.TABLE_NAME);

        // Then recreate our tab;es
        onCreate(db);
    }

    // If we're downgrading the database
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Return our singleton instance of the database
    static public synchronized BeerDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new BeerDbHelper(context);
        }
        return instance;
    }

    // Create our beer information table
    private static final String SQL_CREATE_INFO_ENTRIES =
            "CREATE TABLE " + BeerEntryContract.BeerInfoEntry.TABLE_NAME + "(" +
                    BeerEntryContract.BeerInfoEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BeerEntryContract.BeerInfoEntry.COLUMN_NAME_NAME + " TEXT," +
                    BeerEntryContract.BeerInfoEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    BeerEntryContract.BeerInfoEntry.COLUMN_NAME_ABV + " TEXT," +
                    BeerEntryContract.BeerInfoEntry.COLUMN_NAME_IMAGE_URL + " TEXT"
                    + ")";


    // Create our food pairings table
    private static final String SQL_CREATE_FOOD_PAIRING_ENTRIES =
            "CREATE TABLE " + FoodPairingContract.FoodPairingEntry.TABLE_NAME + "(" +
                    FoodPairingContract.FoodPairingEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    FoodPairingContract.FoodPairingEntry.COLUMN_NAME_BODY + " TEXT," +
                    FoodPairingContract.FoodPairingEntry.COLUMN_NAME_BEER_INFO_ID + " INTEGER, FOREIGN KEY ("+FoodPairingContract.FoodPairingEntry.COLUMN_NAME_BEER_INFO_ID+") REFERENCES "+BeerEntryContract.BeerInfoEntry.TABLE_NAME+"("+BeerEntryContract.BeerInfoEntry.ID+"));";

}
