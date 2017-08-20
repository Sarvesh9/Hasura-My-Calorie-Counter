package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 18/8/17.
 */

public class IdRecord {
    @SerializedName("user_id")
    Integer user_id;

    public IdRecord(Integer user_id){
        this.user_id = user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getUser_id() {
        return user_id;
    }
}
