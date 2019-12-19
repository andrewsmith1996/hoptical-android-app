package com.example.andrew.hoptical;

import android.provider.BaseColumns;

public class FoodPairingContract {

    private FoodPairingContract(){}

    public static class FoodPairingEntry implements BaseColumns {

        public static final String TABLE_NAME = "food_pairings";
        public static final String ID = "id";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_BEER_INFO_ID = "beer_info_id";
    }


}
