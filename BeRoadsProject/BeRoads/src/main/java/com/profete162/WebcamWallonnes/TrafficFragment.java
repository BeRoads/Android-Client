package com.profete162.WebcamWallonnes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.profete162.WebcamWallonnes.Adapter.TrafficAdapter;
import com.profete162.WebcamWallonnes.Utils.NumberedListFragment;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.Utils.Web;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TrafficFragment extends NumberedListFragment {

    public static String FILENAME = "traffic.json";
    AsyncTask<String, Void, ApiResponse> task = null;
    private Location location;
    ApiResponse rep = null;

    public void updateToLoc(Location newLocation) {
        try {
            this.location = newLocation;
            new APIRequest().execute("");
            getActivity().setProgressBarIndeterminateVisibility(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        POSITION = 0;
        return inflater.inflate(R.layout.row_tuto_swipe, null);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Traffic event = ((TrafficAdapter) l.getAdapter()).getItem(position);
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, event.getLocation());
        intent.putExtra(Intent.EXTRA_TEXT, event.getMessage());
        startActivity(Intent.createChooser(intent, this.getString(R.string.share) + "\n\n" + getString(R.string.getBeRoads) + "https://play.google.com/store/apps/details?id=com.profete162.WebcamWallonnes"));
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getBoolean("tuto", true) && ((DrawerActivity) this.getActivity()).mDrawerLayout != null)
            this.getView().findViewById(R.id.tuto).setVisibility(View.VISIBLE);

        this.getListView().setDivider(null);
        task = new APIRequest();

        location = ((DrawerActivity) getActivity()).loc;


        new Thread(new Runnable() {
            @Override
            public void run() {
                displayDatafromSd();
            }
        }).start();

    }

    public void displayDatafromSd() {
        rep = null;
        Activity a = TrafficFragment.this.getActivity();
        try {
            File f = new File(a.getDir("CACHE", Context.MODE_PRIVATE),
                    FILENAME);

            if (f.exists()) {
                BufferedReader is = Utils.getFromFile(f);
                Gson gson = new Gson();
                if (is != null) {
                    rep = gson.fromJson(is, ApiResponse.class);
                }
            }

            if (rep != null)
                TrafficFragment.this.getActivity().runOnUiThread(
                        new Runnable() {
                            public void run() {
                                updateUI(rep, false);
                            }
                        });

            TrafficFragment.this.getActivity().runOnUiThread(
                    new Runnable() {
                        public void run() {
                            try {
                                task.execute("CVE");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });


        } catch (Exception e) {
            getActivity().setProgressBarIndeterminateVisibility(false);
            e.printStackTrace();
        }

    }

    private void displayError() {
    }

    public void updateUI(ApiResponse result, boolean hideProgress) {
        if (result != null)
            try {
                if (this.getListAdapter() == null)
                    this.setListAdapter(new TrafficAdapter(getActivity(),
                            R.layout.row_traffic, result.TrafficEvent.getTraffics(), getActivity()
                            .getLayoutInflater(), location));

                else {
                    TrafficAdapter a = (TrafficAdapter) this.getListAdapter();
                    a.clear();
                    for (Traffic aTraffic : result.TrafficEvent.getTraffics())
                        a.add(aTraffic);
                    a.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }

        if (hideProgress)
            try {
                getActivity().setProgressBarIndeterminateVisibility(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task != null)
            task.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public class APIRequest extends AsyncTask<String, Void, ApiResponse> {

        @Override
        protected ApiResponse doInBackground(String... params) {

            try {
                String url;
                if (location != null)
                    url = "https://data.beroads.com/IWay/TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json&from="
                            + location.getLatitude()
                            + ","
                            + location.getLongitude();
                else
                    url = "https://data.beroads.com/IWay/TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json";

                System.out.println("*** URL:" + url);
                try {
                    InputStream content = Web.DownloadJsonFromUrlAndCacheToSd(url,
                            FILENAME, TrafficFragment.this.getActivity());
                    return new Gson().fromJson(new InputStreamReader(content), ApiResponse.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (
                    Exception e
                    )

            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ApiResponse result) {

            updateUI(result, true);
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
        TrafficEvent TrafficEvent;
    }

    public class TrafficEvent {
        private ArrayList<Traffic> item;

        public ArrayList<Traffic> getTraffics() {
            return item;
        }

    }

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

}

