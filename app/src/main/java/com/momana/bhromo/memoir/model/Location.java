package com.momana.bhromo.memoir.model;

public class Location {

    // Attributes
    public String placeName;
    public double latitude;
    public double longitude;

    // Constructor
    public Location() {
        this.placeName = "";
        this.latitude = 0;
        this.longitude = 0;
    }
    public Location(String placeName, double latitude, double longitude) {
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
