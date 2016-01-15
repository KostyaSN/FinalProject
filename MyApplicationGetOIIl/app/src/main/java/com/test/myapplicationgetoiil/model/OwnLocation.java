package com.test.myapplicationgetoiil.model;

import com.test.myapplicationgetoiil.constants.GooglePlaceType;
import com.test.myapplicationgetoiil.R;

public class OwnLocation {
    private double latitude;
    private double longitude;
    private String name;
    private GooglePlaceType type;
    private double distance;
    private int imageID;
    public String reference;
    public String icon;
    private String[] types;

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
        switch (types[0]) {

            case "gas_station":
                setType(GooglePlaceType.GAS_STATION);
                break;

            default:
                break;
        }
    }

    public int getImageID() {
        return imageID;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GooglePlaceType getType() {
        return type;
    }

    public void setType(GooglePlaceType type) {
        this.type = type;
        switch (type) {

            case GAS_STATION:
                imageID = R.drawable.gas;
                break;

            default:
                imageID = R.drawable.ic_launcher;
                break;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OwnLocation() {
        setDistance(0);
        setLatitude(1);
        setLongitude(1);
        setName("default");
    }

    public OwnLocation(String name, double latitude, double longitude, GooglePlaceType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        setType(type);
    }

}