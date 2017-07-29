package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 29/7/17.
 */

public class RegisterUserResponse {
    @SerializedName("user_id")
    Integer user_id;

    @SerializedName("name")
    Integer name;

    public RegisterUserResponse(Integer name) {
        this.name = name;
    }
}
