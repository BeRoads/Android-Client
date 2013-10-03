package com.profete162.WebcamWallonnes.Utils;

/**
 * Created by 201601 on 18/06/13.
 */
public class Webcam {
    public String city;
    public boolean starred;
    public int id;
    public double lat;
    public double lon;
    public double distance;
    public String img;

    public Webcam(int id, String s, double i, String img, double lat, double lon, boolean starred) {
        this.id = id;
        this.city = s;
        this.distance = i;
        this.img = img;
        this.lat = lat;
        this.lon = lon;
        this.starred=starred;
    }
}


