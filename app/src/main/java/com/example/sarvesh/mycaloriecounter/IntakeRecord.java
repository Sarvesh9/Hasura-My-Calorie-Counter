package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 12/8/17.
 */

public class IntakeRecord {
    @SerializedName("fooditem")
     String fooditem;

    @SerializedName("calories")
    Integer calories;

    public IntakeRecord(String fooditem,Integer calories){
        this.fooditem = fooditem;
        this.calories = calories;
    }

    public void setFooditem(String fooditem) {
        this.fooditem = fooditem;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getFooditem() {
        return fooditem;
    }

    public Integer getCalories() {
        return calories;
    }
}
