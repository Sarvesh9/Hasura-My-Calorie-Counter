package com.example.sarvesh.mycaloriecounter;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sarvesh on 9/8/17.
 */

public class InsertPersonalInfoResult {

        @SerializedName("user_id")
        Integer user_id;

        @SerializedName("height")
        Integer height;

        @SerializedName("weight")
        Integer weight;

        @SerializedName("bmi")
        Float bmi;

        @SerializedName("req_cal")
        Integer req_cal;


        public InsertPersonalInfoResult(Integer user_id, Integer height, Integer weight, Float bmi ,  Integer req_cal) {
            this.user_id=user_id;
            this.height=height;
            this.weight=weight;
            this.bmi=bmi;
            this.req_cal=req_cal;

        }

        public void setUser_id(Integer user_id) {
            this.user_id = user_id;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }
        public void setBMI(Float bmi) {
            this.bmi = bmi;
        }

        public void setReq_cal(Integer req_cal) {
            this.req_cal = req_cal;
        }

        public Integer getUser_id() {
            return user_id;
        }

        public Integer getHeight() {
            return height;
        }

        public Integer getWeight() {
            return weight;
        }

        public Float getBMI() {
            return bmi;
        }

        public Integer getReq_cal() {
            return req_cal;
        }
}
