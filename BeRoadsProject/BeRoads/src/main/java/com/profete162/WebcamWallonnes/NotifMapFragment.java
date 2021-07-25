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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.profete162.WebcamWallonnes.Adapter.PopupTrafficAdapter;
import com.profete162.WebcamWallonnes.Utils.DataBaseHelper;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.Utils.Web;
import com.profete162.WebcamWallonnes.Utils.Webcam;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class NotifMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    Location location;
    GoogleMap mMap;
    int ZOOM = 9;
    SharedPreferences preferences;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        // mMapFragment = MapFragment.newInstance();
        this.getMapAsync(this);

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        location = ((DrawerActivity) this.getActivity()).loc;

        CamRequest camTask = new CamRequest();
        camTask.execute("CVE");
        mMap.setInfoWindowAdapter(new PopupTrafficAdapter(getParentFragment().getActivity().getLayoutInflater()));


        preferences = PreferenceManager.getDefaultSharedPreferences(getParentFragment().getActivity());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(preferences.getFloat("lat", 50.5f),
                        preferences.getFloat("lng", 4)), preferences
                        .getFloat("zoom", ZOOM)));
    }

    public class APITrafficRequest extends AsyncTask<String, Void, TrafficFragment.ApiResponse> {

        @Override
        protected TrafficFragment.ApiResponse doInBackground(String... params) {

            if (location != null) {

                //int randomNum = 0;
                //Random ran = new Random();
                //location.setLongitude(ran.nextDouble()*4.3+2.37);
                //location.setLatitude(ran.nextDouble()*2+49.5);


                try {
                    String url = "https://data.beroads.com/IWay/TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json&from="
                            + location.getLatitude()
                            + ","
                            + location.getLongitude()
                            + "&max=30";

                    InputStream content = Web.DownloadJsonFromUrlAndCacheToSd(url,
                            TrafficFragment.FILENAME, NotifMapFragment.this.getParentFragment().getActivity());

                    return new Gson().fromJson(new InputStreamReader(content), TrafficFragment.ApiResponse.class);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(TrafficFragment.ApiResponse result) {
            if (result != null) {


                for (TrafficFragment.Traffic aTraffic : result.TrafficEvent.getTraffics())
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(aTraffic.getLat(), aTraffic.getLon()))
                            .title("T;" + ";" + aTraffic.getCategory())
                            .anchor(0.5f, 1.0f)
                            .snippet(aTraffic.getMessage())
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_m_traffic)));
            }


            APIRadarRequest radarTask = new APIRadarRequest();
            radarTask.execute("CVE");

            //TODO Display error if null
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class APIRadarRequest extends AsyncTask<String, Void, RadarFragment.ApiResponse> {

        @Override
        protected RadarFragment.ApiResponse doInBackground(String... params) {

            if (location != null) {
                String url = "https://data.beroads.com/IWay/Radar.json?format=json&from="
                        + location.getLatitude()
                        + ","
                        + location.getLongitude() + "&max=30";

                try {

                    InputStream content = Web.DownloadJsonFromUrlAndCacheToSd(url,
                            RadarFragment.FILENAME, NotifMapFragment.this.getParentFragment().getActivity());

                    return new Gson().fromJson(new InputStreamReader(content), RadarFragment.ApiResponse.class);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(RadarFragment.ApiResponse result) {
            try {
                if (result != null) {
                    for (RadarFragment.Item aRadar : result.Radar.getRadars())
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(aRadar.getLat(), aRadar.getLng()))
                                .title("R;" + aRadar.getType() + ";" + aRadar.getAddress())
                                .anchor(0.5f, 1.0f)
                                .snippet("" + aRadar.getSpeedLimit() + ";" + aRadar.getLat() + "," + aRadar.getLng())
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.ic_m_radar)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                getParentFragment().getActivity().setProgressBarIndeterminateVisibility(false);

                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), ZOOM));


                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        final String[] text = marker.getTitle().split(";");
                        final String[] snippet = marker.getSnippet().split(";");
                        switch (text[0].charAt(0)) {
                            case 'W':
                                final Dialog dialog = new Dialog(NotifMapFragment.this.getParentFragment()
                                        .getActivity(), R.style.full_screen_dialog) {
                                    @Override
                                    protected void onCreate(Bundle savedInstanceState) {
                                        super.onCreate(savedInstanceState);

                                        ImageView view = new ImageView(NotifMapFragment.this.getParentFragment()
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
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
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

            APITrafficRequest trafficTask = new APITrafficRequest();
            trafficTask.execute("CVE");
            int total = 0;

            if (result != null) {
                for (Webcam item : result) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.lat, item.lon))
                            .title("W;" + item.img + ";" + item.city)
                            .anchor(0.5f, 1.0f)
                            .snippet("")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_m_webcam)));
                    total++;
                    //if (total >= 30)
                     //   break;
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