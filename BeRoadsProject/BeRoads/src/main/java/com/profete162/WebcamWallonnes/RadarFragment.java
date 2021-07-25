package com.profete162.WebcamWallonnes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.profete162.WebcamWallonnes.Adapter.RadarAdapter;
import com.profete162.WebcamWallonnes.Utils.NumberedListFragment;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.Utils.Web;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RadarFragment extends NumberedListFragment {

    public static String FILENAME = "radar.json";
    AsyncTask<String, Void, ApiResponse> task;
    private Location location;

    public void updateToLoc(Location location) {
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        POSITION = 2;
        super.onActivityCreated(savedInstanceState);
        this.getListView().setDivider(null);
        task = new APIRequest();
        new Thread(new Runnable() {
            @Override
            public void run() {
                displayDatafromSd();
            }
        }).start();

    }

    @Override
    public void onResume() {
        super.onResume();
        location = ((DrawerActivity) this.getActivity()).loc;
    }

    public void displayDatafromSd() {
        Activity a = RadarFragment.this.getActivity();
        // Log.i("", "Activity2: " +
        // PositionFragment.this.getSherlockActivity());
        try {
            File f = new File(a.getDir("CACHE", Context.MODE_PRIVATE),
                    FILENAME);
            // Log.i("", "File: " + f);
            if (f.exists()) {
                BufferedReader is = Utils.getFromFile(f);
                Gson gson = new Gson();
                if (is != null) {
                    final ApiResponse rep = gson.fromJson(is, ApiResponse.class);
                    Log.i("", "*** REP " + rep);
                    Collections.sort(rep.Radar.item, new CustomComparator());
                    if (rep != null)
                        RadarFragment.this.getActivity().runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        updateUI(rep);
                                    }
                                });
                    else
                        Log.i("", "*** File - NULL ");
                }
            }
        } catch (Exception f) {
            f.printStackTrace();
        }
        this.getListView().setSelector(R.drawable.listselector_yellow);
        task.execute("CVE");
    }

    private void displayError() {
    }

    public void updateUI(final ApiResponse result) {
        try {
            Log.i("", "Result: " + result.Radar.item);
            Log.i("", "Inflater: " + getActivity().getLayoutInflater());
            Log.i("", "pos: " + location);

            if (this.getListAdapter() == null)
                this.setListAdapter(new RadarAdapter(getActivity(),
                        R.layout.row_radar, result.Radar.item, getActivity()
                        .getLayoutInflater(), location.getLatitude(), location.getLongitude()));
            else{
                RadarAdapter a = (RadarAdapter) this.getListAdapter();
                a.clear();
                for (Item aRadar:result.Radar.getRadars())
                    a.add(aRadar);

                a.notifyDataSetChanged();
            }



            this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                    Log.i("FragmentComplexList", "Item clicked: " + arg);
                    Item i = result.Radar.item.get((int) arg);
                    try {
                        Uri streetViewUri = Uri.parse(
                                "google.streetview:cbll=" + i.getLat() + "," + i.getLng() + "&cbp=1,90,,0,1.0&mz=20");
                        Intent streetViewIntent = new Intent(Intent.ACTION_VIEW, streetViewUri);
                        startActivity(streetViewIntent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.street")));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        task.cancel(true);
    }

    public class CustomComparator implements Comparator<Item> {
        @Override
        public int compare(Item o1, Item o2) {

            if (o1.distance == o2.distance)
                return 0;
            else if (o1.distance > o2.distance)
                return 1;
            else return -1;
        }
    }

    private class APIRequest extends AsyncTask<String, Void, ApiResponse> {

        @Override
        protected ApiResponse doInBackground(String... params) {

            if (location != null) {
                String url = "https://data.beroads.com/IWay/Radar.json?format=json&from="
                        + location.getLatitude()
                        + ","
                        + location.getLongitude() + "&area=40";
                System.out.println("*** URL:" + url);
                try {

                    InputStream content = Web.DownloadJsonFromUrlAndCacheToSd(url,
                            FILENAME, RadarFragment.this.getActivity());

                    return new Gson().fromJson(new InputStreamReader(content), ApiResponse.class);


                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else
                System.out.println("*** LOC:" + location);
            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse result) {
            if (result != null)
                updateUI(result);

            try {
                RadarFragment.this.getActivity().setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {

            }
            //TODO Display error if null

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    public class ApiResponse {
        Radar Radar;
    }

    public class Radar {
        private ArrayList<Item> item;

        public ArrayList<Item> getRadars() {
            return item;
        }

    }

    public class Item {
        public int distance;
        private String name;
        private String address;
        private int speedLimit;
        private String type;
        private double lat;
        private double lng;

        public Item(String name, int speedLimit, String type, double lat,
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

}

