package com.example.wifimanager;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("ssid")
    private String ssid;
    @SerializedName("bssid")
    private String bssid;
    @SerializedName("rssi")
    private int rssi;

    @SerializedName("place")
    private String place;

    public Location(String ssid, String bssid, int rssi, String place) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.rssi = rssi;
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public int getRssi() {
        return rssi;
    }
}
