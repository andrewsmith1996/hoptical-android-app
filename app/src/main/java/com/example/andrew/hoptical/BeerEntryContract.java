package com.example.andrew.hoptical;

import android.provider.BaseColumns;

public class BeerEntryContract {

    private BeerEntryContract(){}

    // Abstract class that defines the structure for our beer information table
    public static class BeerInfoEntry implements BaseColumns {

        public static final String TABLE_NAME = "beer_information";
        public static final String ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_ABV= "abv";
        public static final String COLUMN_NAME_IMAGE_URL= "image_url";
    }
}
