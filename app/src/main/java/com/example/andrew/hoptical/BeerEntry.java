package com.example.andrew.hoptical;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class BeerEntry implements Serializable {

    private String name;
    private String description;
    private Double abv;
    private ArrayList<String> food_pairing;
    private String image_url;

    public BeerEntry(String beerTitle, String beerDescription, Double beerABV, ArrayList<String> beerFoodPairing, String beerImageUrl){
        name = beerTitle;
        description = beerDescription;
        abv = beerABV;
        food_pairing = beerFoodPairing;
        image_url = beerImageUrl;
    }

    public String getBeerName(){
        return name;
    }

    public String getBeerDescription(){
        return description;
    }

    public Double getBeerABV(){
        return abv;
    }

    public ArrayList<String> getBeerFoodPairings(){
        return food_pairing;
    }

    public String getImageUrl(){
        return image_url;
    }

    public void setImageUrl(String newFilePath){
        image_url = newFilePath;
    }
}
