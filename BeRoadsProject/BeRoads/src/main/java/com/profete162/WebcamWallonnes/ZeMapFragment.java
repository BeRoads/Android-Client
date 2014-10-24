package com.profete162.WebcamWallonnes;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.tests.toolbox.GsonRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.profete162.WebcamWallonnes.Adapter.PopupTrafficAdapter;
import com.profete162.WebcamWallonnes.Utils.DataBaseHelper;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.Utils.Web;
import com.profete162.WebcamWallonnes.models.RadarItem;
import com.profete162.WebcamWallonnes.models.Traffic;
import com.profete162.WebcamWallonnes.models.Webcam;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ZeMapFragment extends SupportMapFragment implements GoogleMap.OnInfoWindowClickListener {

    Location location;
    GoogleMap mMap;
    static int ZOOM = 9;
    SharedPreferences preferences;
    private GsonRequest<List<Traffic>> trafficsGsonRequest;
    private GsonRequest<List<RadarItem>> radarsGsonRequest;
    private MainActivity activity;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        activity = ((MainActivity) this.getActivity());

        // mMapFragment = MapFragment.newInstance();
        mMap = this.getMap();
        if (mMap != null) {

            location = activity.loc;

            CamRequest camTask = new CamRequest();
            camTask.execute("CVE");
            mMap.setInfoWindowAdapter(new PopupTrafficAdapter(getParentFragment().getActivity().getLayoutInflater()));


            preferences = PreferenceManager.getDefaultSharedPreferences(getParentFragment().getActivity());

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(preferences.getFloat("lat", 50.5f),
                            preferences.getFloat("lng", 4)), preferences
                    .getFloat("zoom", ZOOM)));

            mMap.setOnInfoWindowClickListener(this);
        }else
            getParentFragment().getActivity().setProgressBarIndeterminateVisibility(false);


    }

    public void doStuff() {
        getParentFragment().getActivity().setProgressBarIndeterminateVisibility(true);
        if (mMap != null) {
            mMap.clear();
            location.setLatitude(mMap.getCameraPosition().target.latitude);
            location.setLongitude(mMap.getCameraPosition().target.longitude);
            CamRequest camTask = new CamRequest();
            camTask.execute("CVE");
        }
    }

    public void toggleTraffic() {
        if (mMap != null) {
            if (mMap.isTrafficEnabled())
                mMap.setTrafficEnabled(false);
            else
                mMap.setTrafficEnabled(true);
        }
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mMap != null) {
            savedInstanceState.putDouble("lat",
                    mMap.getCameraPosition().target.latitude);
            savedInstanceState.putDouble("lng",
                    mMap.getCameraPosition().target.longitude);
            savedInstanceState.putFloat("zoom", mMap.getCameraPosition().zoom);
        }

    }


    public void reloadTraffic(){
        String url;
        if (location != null) {
            url = MainActivity.DATA_BEROADS_URL+"TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json&from="
                    + location.getLatitude()
                    + ","
                    + location.getLongitude()
                    +"&max=30";
        } else {
            url = MainActivity.DATA_BEROADS_URL+"TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json&max=30";
        }

        Log.d("","*** URL:" + url);

        trafficsGsonRequest = new GsonRequest<List<Traffic>>(url,new TypeToken<List<Traffic>>(){}.getType(),null,new Response.Listener<List<Traffic>>() {
            @Override
            public void onResponse(List<Traffic> response) {
                for (Traffic aTraffic : response) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(aTraffic.getLat(), aTraffic.getLon()))
                            .title("T;" + ";" + aTraffic.getCategory())
                            .anchor(0.5f, 1.0f)
                            .snippet(aTraffic.getMessage())
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_m_traffic)));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("",error.toString());
            }
        });
        trafficsGsonRequest.setTag(activity);
        activity.getRequestQueue().add(trafficsGsonRequest);
    }

    private void reloadRadars() {
        String url;
        if (location != null) {
            url = MainActivity.DATA_BEROADS_URL+"Radar.json?format=json&from="
                    + location.getLatitude()
                    + ","
                    + location.getLongitude()
                    + "&max=30";
        } else {
            url = MainActivity.DATA_BEROADS_URL + "Radar.json?format=json&max=30";
        }

        radarsGsonRequest = new GsonRequest<List<RadarItem>>(url, new TypeToken<List<RadarItem>>(){}.getType(),null,new Response.Listener<List<RadarItem>>() {
            @Override
            public void onResponse(List<RadarItem> response) {
                for (RadarItem aRadar : response) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(aRadar.getLat(), aRadar.getLng()))
                            .title("R;" + aRadar.getType() + ";" + aRadar.getAddress())
                            .anchor(0.5f, 1.0f)
                            .snippet("" + aRadar.getSpeedLimit() + ";" + aRadar.getLat() + "," + aRadar.getLng())
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_m_radar)));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("",error.toString());
            }
        });
        radarsGsonRequest.setTag(activity);
        activity.setProgressBarIndeterminateVisibility(false);;
        activity.getRequestQueue().add(radarsGsonRequest);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        final String[] text = marker.getTitle().split(";");
        final String[] snippet = marker.getSnippet().split(";");
        switch (text[0].charAt(0)) {
            case 'W':
                final Dialog dialog = new Dialog(ZeMapFragment.this.getParentFragment()
                        .getActivity(), R.style.full_screen_dialog) {
                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);

                        ImageView view = new ImageView(ZeMapFragment.this.getParentFragment()
                                .getActivity());
                        //UrlImageViewHelper.setUrlDrawable(view, text[1], null, 20 * DateUtils.MINUTE_IN_MILLIS);
                        Picasso.with(getContext()).load( text[1]).into(view);

                        view.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
                        // view.setBackgroundResource(R.drawable.search_bg_shadow);
                        final Dialog d = this;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                d.dismiss();
                            }
                        });

                        setContentView(view);
                        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.MATCH_PARENT);
                    }
                };
                dialog.setTitle(marker.getTitle());
                dialog.show();
                break;
            case 'R':
                try {
                    Uri streetViewUri = Uri.parse(
                            "google.streetview:cbll=" + snippet[1] + "&cbp=1,90,,0,1.0&mz=20");
                    Intent streetViewIntent = new Intent(Intent.ACTION_VIEW, streetViewUri);
                    startActivity(streetViewIntent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.street")));
                }
                break;
            default:
                break;
        }
        marker.hideInfoWindow();
    }

    public class CamRequest extends AsyncTask<String, Void, ArrayList<Webcam>> {

        @Override
        protected ArrayList<Webcam> doInBackground(String... params) {

            if (location != null) {
                try {
                    DataBaseHelper mDbHelper = new DataBaseHelper(getParentFragment().getActivity());
                    mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
                    Cursor webcamCursor = mDbHelper.fetchAllWebcam();
                    ArrayList<Webcam> myList = new ArrayList<Webcam>();
                    for (webcamCursor.moveToFirst(); webcamCursor.moveToNext(); webcamCursor
                            .isAfterLast()) {
                        // The Cursor is now set to the right position

                        myList.add(new Webcam(
                                webcamCursor.getInt(webcamCursor.getColumnIndex("_id")),
                                webcamCursor.getString(webcamCursor.getColumnIndex("city")),
                                Utils.getDistance(location.getLatitude(), location.getLongitude(), webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")), webcamCursor.getDouble(webcamCursor.getColumnIndex("lng"))),
                                webcamCursor.getString(webcamCursor.getColumnIndex("img")),
                                webcamCursor.getDouble(webcamCursor.getColumnIndex("lat")),
                                webcamCursor.getDouble(webcamCursor.getColumnIndex("lng")),
                                webcamCursor.getInt(webcamCursor.getColumnIndex("starred")) == 0));
                    }

                    Collections.sort(myList, new CustomComparator());
                    mDbHelper.close();
                    return myList;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Webcam> result) {

            reloadTraffic();
            reloadRadars();

            if (result != null) {
                for (Webcam item : result) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.lat, item.lon))
                            .title("W;" + item.img + ";" + item.city)
                            .anchor(0.5f, 1.0f)
                            .snippet("")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_m_webcam)));
                }
            }

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class CustomComparator implements Comparator<Webcam> {
        @Override
        public int compare(Webcam o1, Webcam o2) {
            if (o1.distance == o2.distance)
                return 0;
            else if (o1.distance > o2.distance)
                return 1;
            else return -1;
        }
    }

}