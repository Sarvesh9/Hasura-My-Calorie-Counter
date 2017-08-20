package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 19/8/17.
 */

public class CalorieResponse {

    @SerializedName("calories")
    Integer calories;

    public CalorieResponse(Integer calories){
        this.calories = calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getCalories() {
        return calories;
    }
}
