package com.example.teja2.tripplanner;

import java.io.Serializable;
import java.util.List;

/*
Created by
Bala Guna Teja Karlapudi
*/public class trips implements Serializable {
    String tripName, tripCity, tripDate, tripID, userID, username;
    List<places> places;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripCity() {
        return tripCity;
    }

    public void setTripCity(String tripCity) {
        this.tripCity = tripCity;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public List<places> getPlaces() {
        return places;
    }

    public void setPlaces(List<places> places) {
        this.places = places;
    }
}
