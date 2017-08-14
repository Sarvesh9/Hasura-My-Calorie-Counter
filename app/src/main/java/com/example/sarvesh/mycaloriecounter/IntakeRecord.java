package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 12/8/17.
 */

public class IntakeRecord {
    @SerializedName("Item")
     String Item;

    @SerializedName("calories")
    Integer calories;

    public IntakeRecord(String Item,Integer calories){
        this.Item = Item;
        this.calories = calories;
    }

    public void setItem(String Item) {
        this.Item = Item;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getItem() {
        return Item;
    }

    public Integer getCalories() {
        return calories;
    }
}
