package com.example.teja2.tripplanner;

import java.io.Serializable;

/*
Created by
Bala Guna Teja Karlapudi
*/public class places implements Serializable {
    String latitude=null, longitude=null, name=null;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
