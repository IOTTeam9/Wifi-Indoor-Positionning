package com.example.wifimanager;

import com.google.gson.annotations.SerializedName;

public class NavigationResponse {
    @SerializedName("isSuccess")
    private boolean isSuccess;
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("result")
    private String result;

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }
}
