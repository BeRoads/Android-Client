package com.profete162.WebcamWallonnes.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by 201601 on 15/06/13.
 */
public class Utils {
    private static final String TAG = "WazaBe";

    public static double getDistance(double sLat, double sLon, double eLat,
                                     double eLon) {
        double d2r = (Math.PI / 180);
        Log.i("DISTANCE: ", sLat + "/" + sLon + " * " + eLat + "/" + eLon);
        try {
            double dlong = (eLon - sLon) * d2r;
            double dlat = (eLat - sLat) * d2r;
            double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(sLat * d2r)
                    * Math.cos(eLat * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return 6367 * c * 1000;

        } catch (Exception e) {
            Log.e(TAG, "An exemption occurred when getting lat/log", e);
        }
        return 0;
    }

    public static BufferedReader getFromFile(File file) {

        try {
            return new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
