package com.profete162.WebcamWallonnes.models;

/**
 * Created by lionelschinckus on 23/10/14.
 */
public class Traffic {
    private String source;
    private String location;
    private String message;
    private String category;
    private long time;
    private double lat;
    private double lng;

    public String getLocation() {
        return location;
    }

    public long getTime() {
        return this.time;
    }

    public String getSource() {
        return this.source;
    }

    public String getCategory() {
        return this.category;
    }

    public String getMessage() {
        return this.message;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lng;
    }
}