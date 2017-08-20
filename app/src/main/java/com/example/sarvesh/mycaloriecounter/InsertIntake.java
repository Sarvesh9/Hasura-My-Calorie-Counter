package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 19/8/17.
 */

public class InsertIntake {
    @SerializedName("user_id")
    Integer user_id;

    @SerializedName("food_item")
    String food_item;

    @SerializedName("calories")
    Integer calories;

    @SerializedName("quantity")
    Integer quantity;

    @SerializedName("date")
    String  date;

    @SerializedName("mealtype")
    String mealtype;


    public InsertIntake(Integer user_id, String food_item, Integer calories, Integer quantity,  String date, String mealtype) {
        this.user_id=user_id;
        this.food_item=food_item;
        this.calories=calories;
        this.quantity=quantity;
        this.date=date;
        this.mealtype=mealtype;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setFood_item(String food_item) {
        this.food_item=food_item;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMealtype(String mealtype) {
        this.mealtype=mealtype;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public String getFood_item() {
        return food_item;
    }

    public Integer getCalories() {
        return calories;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public String getMealtype() {
        return mealtype;
    }
}
