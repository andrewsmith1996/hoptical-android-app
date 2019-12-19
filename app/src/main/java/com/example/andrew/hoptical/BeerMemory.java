package com.example.andrew.hoptical;
import android.content.Context;

import java.io.File;
import java.io.Serializable;

import net.sqlcipher.database.SQLiteDatabase;

public class BeerMemory implements Serializable {

    private String memoryBeerTitle;
    private String memoryBeerBrewery;
    private String memoryDrinkingBuddy;
    private String memoryLocation;
    private String memoryImageFilePath;
    private String memoryNotes;
    private String memoryUserId;
    private String memoryDate;

    public BeerMemory(String beerTitle, String brewery, String date, String drinkingBuddy, String location, String imageFilePath, String notes, String userID){

        memoryBeerTitle = beerTitle;
        memoryBeerBrewery = brewery;
        memoryDate = date;
        memoryDrinkingBuddy = drinkingBuddy;
        memoryLocation = location;
        memoryImageFilePath = imageFilePath;
        memoryNotes = notes;
        memoryUserId = userID;
    }

    public BeerMemory(){}

    public String getMemoryBeerTitle(){
        return memoryBeerTitle;
    }

    public String getMemoryBeerBrewery(){
        return memoryBeerBrewery;
    }

    public String getMemoryDate(){
        return memoryDate;
    }

    public String getMemoryDrinkingBuddy(){
        return memoryDrinkingBuddy;
    }

    public String getMemoryLocation(){
        return memoryLocation;
    }

    public String getMemoryImageFilePath(){
        return memoryImageFilePath;
    }

    public String getMemoryNotes(){
        return memoryNotes;
    }

    public String getUserID(){
        return memoryUserId;
    }

    public void setUserID(String id){
        memoryUserId = id;
    }


}
