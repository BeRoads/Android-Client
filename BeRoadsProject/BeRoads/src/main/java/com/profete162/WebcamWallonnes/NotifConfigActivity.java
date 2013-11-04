package com.profete162.WebcamWallonnes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.profete162.WebcamWallonnes.Utils.Web;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotifConfigActivity extends Activity implements GoogleMap.OnMapClickListener {

    GoogleMap mMap;
    ArrayList<LatLng> points = new ArrayList<LatLng>();
    String regid;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifconfig);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMapClickListener(this);
        regid = MainActivity.getRegistrationId(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mMap != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(preferences.getFloat("lat", 50.5f),
                            preferences.getFloat("lng", 4)), preferences
                    .getFloat("zoom", ZeMapFragment.ZOOM)));
    }

    @Override
    public void onMapClick(LatLng latLng) {

        drawCircle(latLng);
        points.add(latLng);
    }

    private void drawCircle(LatLng latLng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)   //set center
                .radius(1000)   //set radius in meters
                .fillColor(this.getResources().getColor(R.color.holo_blue_light))
                .strokeColor(this.getResources().getColor(R.color.holo_blue_dark))
                .strokeWidth(5);
        mMap.addCircle(circleOptions);

        if (points.size() > 0) {

            PolylineOptions polylinesOptions = new PolylineOptions().color(this.getResources().getColor(R.color.holo_blue_dark))
                    .width(5).add(points.get(points.size() - 1)).add(latLng);

            mMap.addPolyline(polylinesOptions);
        }
    }

    public void sendToServer(View v) {
        new Thread(new Runnable() {
            public void run() {

                try {


                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("registration_id", regid);
                    jsonObj.put("language", "fr");
                    jsonObj.put("area", 30);

                    JSONArray array = new JSONArray();
                    for (LatLng aPoint : points) {
                        JSONObject coords = new JSONObject();
                        coords.put("lat", aPoint.latitude);
                        coords.put("lng", aPoint.longitude);
                        array.put(coords);
                    }


                    jsonObj.put("points",array);


                    HttpPost httpPost = new HttpPost("http://dashboard.beroads.com/gcm");
                    StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
                    entity.setContentType("application/json");
                    httpPost.setEntity(entity);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(httpPost);
                    HttpEntity entity2 = response.getEntity();
                    String responseString = EntityUtils.toString(entity2, "UTF-8");
                    Log.d("", "+++" + responseString);
                    // Web.makeRequest("http://dashboard.beroads.com/gcm", regid, "fr", "30", lat, lng);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Web.retrieveStream("http://beroads.com/test_notif.php?reg_id=" + regid, NotifConfigActivity.this);
            }
        }).start();
    }


    @Override
    public void onPause() {
        super.onPause();

        if (preferences != null && mMap != null) {
            SharedPreferences.Editor editor = preferences.edit(); // Put the values
            // from the UI
            if (mMap != null) {
                editor.putFloat("lat",
                        (float) mMap.getCameraPosition().target.latitude);
                editor.putFloat("lng",
                        (float) mMap.getCameraPosition().target.longitude);
                editor.putFloat("zoom", mMap.getCameraPosition().zoom);
                // Commit to storage
                editor.commit();
                Log.i("-***", "Pause" + mMap.getCameraPosition().target.latitude);
            }
        }

    }

}