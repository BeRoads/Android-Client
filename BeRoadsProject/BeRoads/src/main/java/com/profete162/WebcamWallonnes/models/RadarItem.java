package com.profete162.WebcamWallonnes.models;

import android.util.Log;

/**
 * Created by lionelschinckus on 23/10/14.
 */
public class RadarItem {
    public int distance;
    private String name;
    private String address;
    private int speedLimit;
    private String type;
    private double lat;
    private double lng;

    public RadarItem(String name, int speedLimit, String type, double lat,
                     double lon) {
        Log.e("", "WOOT");
        this.name = name;
        this.speedLimit = speedLimit;
        this.type = type;
        this.lat = lat;
        this.lng = lon;
    }

    public String getAddress() {
        return address;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public String getType() {
        return type;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
