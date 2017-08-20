package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 20/8/17.
 */

public class RequiredCalorieResponse {
    @SerializedName("req_cal")
    Integer req_cal;

    public RequiredCalorieResponse(Integer req_cal){
        this.req_cal = req_cal;
    }

    public void setReq_cal(Integer req_cal) {
        this.req_cal = req_cal;
    }

    public Integer getReq_cal() {
        return req_cal;
    }
}
