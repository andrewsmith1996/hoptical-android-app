package com.example.andrew.hoptical;

public class FoodPairing {

    private String food_pairing;

    public void FoodPairing(String beerFoodPairing){
        food_pairing = beerFoodPairing;
    }

    public String getFoodPairings(){
        return food_pairing;
    }

}
