package com.profete162.WebcamWallonnes.misc;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.profete162.WebcamWallonnes.SortMap;

public class Snippets {

	public static void createImageMapReceiver(int picId, char cat,
			ImageView image, SortMap context) {

		String url = getUrlFromCat(picId, cat);
		context.new ImageMapReceiver(url, context, image);

	}

	public static float getDistanceBetweenLocations(Location loc1, Location loc2) {
		float[] results={999999999};
		Location.distanceBetween(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude(), results);
		return results[0];
	}
	
	public static double[] getLocationFromBundle(Bundle bundle) {
		double [] toReturn={bundle.getDouble("lat"),bundle.getDouble("lng")};
		return toReturn;
	}
	
	public static double getDistance(double sLat, double sLon, double eLat,
			double eLon) {
		double d2r = (Math.PI / 180);
		Log.i("DISTANCE: ", sLat+"/"+sLon+" * "+eLat+"/"+eLon);
		try {
			double dlong = (eLon - sLon) * d2r;
			double dlat = (eLat - sLat) * d2r;
			double a = Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(sLat * d2r)
					* Math.cos(eLat * d2r) * Math.pow(Math.sin(dlong / 2.0), 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

			return 6367 * c * 1000;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static String getUrlFromCat(int picId, char cat) {
		switch (cat) {
		case 'A':
			return "http://www.webcams-wallonnes.be/webcams/image_antwerpen_"
					+ (picId - 101) + ".jpg";
		case 'B':
			return "http://www.webcams-wallonnes.be/webcams/image_brussel_"
					+ (picId - 201) + ".jpg";
		case 'C':
			return "http://www.webcams-wallonnes.be/webcams/image_ringbxl_"
					+ (picId - 301) + ".jpg";
		case 'D':
			return "http://www.webcams-wallonnes.be/webcams/image_gand_"
					+ (picId - 401) + ".jpg";
		case 'E':
			return "http://www.webcams-wallonnes.be/webcams/image_lummen_"
					+ (picId - 501) + ".jpg";
		case 'F':
			return "http://www.webcams-wallonnes.be/webcams/image" + picId + ".jpg";
		default:
			return null;
		}
	}

}
