package com.profete162.WebcamWallonnes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.tests.toolbox.JsonObjectRequest;
import com.android.volley.tests.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.profete162.WebcamWallonnes.Utils.DataBaseHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.sql.Timestamp;

public class MainActivity extends DrawerActivity {

    int db_version = 0;

    private RequestQueue mRequestQueue;

    public static final String DATA_BEROADS_URL = "http://data.beroads.com/v2/IWay/";

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_PROGRESS);


        regid = getRegistrationId(this);
        Log.i(TAG, "+++regid= " + regid);
        if (regid.length() == 0) {
            registerBackground();
        }
        gcm = GoogleCloudMessaging.getInstance(this);

        super.onCreate(savedInstanceState);


        setProgressBarIndeterminateVisibility(true);
        setProgressBarVisibility(true);

        DataBaseHelper myDbHelper = new DataBaseHelper(this);

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        if (settings.getInt("db_version", -1) != db_version) {
            try {

                myDbHelper.forceCreateDataBase(this);

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("db_version", db_version);
                // Don't forget to commit your edits!!!
                editor.commit();
                Toast.makeText(this, "Database Updated", Toast.LENGTH_SHORT)
                        .show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(this, "Unable to create database",
                        Toast.LENGTH_LONG).show();
            }
        }

        mRequestQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(this);
        }
    }

    public static String getRegistrationId(Context context) {
        final SharedPreferences prefs = MainActivity.getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0) {
            Log.v(TAG, "Registration not found.");
            return "";
        }
        // check if app was updated; if so, it must clear registration id to
        // avoid a race condition if GCM sends a message
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion || MainActivity.isRegistrationExpired(context)) {
            Log.v(TAG, "App version changed or registration expired.");
            return "";
        }
        return registrationId;
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static boolean isRegistrationExpired(Context c) {
        final SharedPreferences prefs = getGCMPreferences(c);
        // checks if the information is not stale
        long expirationTime =
                prefs.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1);
        return System.currentTimeMillis() > expirationTime;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration id, app versionCode, and expiration time in the
     * application's shared preferences.
     */
    private void registerBackground() {
        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
            }

            regid = gcm.register(SENDER_ID);

            setRegistrationId(MainActivity.this, regid);

            String lat = "12.3";
            String lng = "45.6";

            if (loc != null) {
                lat = "" + loc.getLatitude();
                lng = "" + loc.getLongitude();
            }

            JSONObject jsonObj = new JSONObject();
            jsonObj.put("registration_id", regid);
            jsonObj.put("language", "fr");
            jsonObj.put("area", 30);
            JSONObject coords = new JSONObject();
            coords.put("lat", lat);
            coords.put("lng", lng);
            jsonObj.put("coords", coords);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "http://dashboard.beroads.com/gcm", jsonObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("", "+++" + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("",error.toString());
                }
            });

            request.setTag(this);

            mRequestQueue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stores the registration id, app versionCode, and expiration time in the
     * application's {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration id
     */
    private void setRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.v(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        long expirationTime = System.currentTimeMillis() + REGISTRATION_EXPIRY_TIME_MS;

        Log.v(TAG, "Setting registration expiry time to " +
                new Timestamp(expirationTime));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, expirationTime);
        editor.commit();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}
