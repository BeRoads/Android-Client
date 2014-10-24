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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.tests.toolbox.GsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.profete162.WebcamWallonnes.Adapter.RadarAdapter;
import com.profete162.WebcamWallonnes.Utils.NumberedListFragment;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.Utils.Web;
import com.profete162.WebcamWallonnes.models.RadarItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RadarFragment extends NumberedListFragment {

    private Location location;
    private GsonRequest<List<RadarItem>> gsonRequest;
    private MainActivity activity;

    public void updateToLoc(Location location) {
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        POSITION = 2;
        super.onActivityCreated(savedInstanceState);
        this.getListView().setDivider(null);
        activity = (MainActivity) getActivity();
        this.reloadRadars();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity = ((MainActivity) this.getActivity());
        location = activity.loc;
    }

    private void displayError() {
    }

    public void updateUI(final List<RadarItem> result) {
        try {
            Log.i("", "Result size: " + result.size());
            Log.i("", "Inflater: " + getActivity().getLayoutInflater());
            Log.i("", "pos: " + location);

            if (this.getListAdapter() == null)
                this.setListAdapter(new RadarAdapter(getActivity(),
                        R.layout.row_radar, result, getActivity()
                        .getLayoutInflater(), location.getLatitude(), location.getLongitude()));
            else{
                RadarAdapter a = (RadarAdapter) this.getListAdapter();
                a.clear();
                for (RadarItem aRadar:result)
                    a.add(aRadar);

                a.notifyDataSetChanged();
            }



            this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                    Log.i("FragmentComplexList", "Item clicked: " + arg);
                    RadarItem i = result.get((int) arg);
                    try {
                        Uri streetViewUri = Uri.parse(
                                "google.streetview:cbll=" + i.getLat() + "," + i.getLng() + "&cbp=1,90,,0,1.0&mz=20");
                        Intent streetViewIntent = new Intent(Intent.ACTION_VIEW, streetViewUri);
                        startActivity(streetViewIntent);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.google.android.street")));
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
        if (gsonRequest != null) {
            gsonRequest.cancel();
        }
    }

    public class CustomComparator implements Comparator<RadarItem> {
        @Override
        public int compare(RadarItem o1, RadarItem o2) {

            if (o1.distance == o2.distance)
                return 0;
            else if (o1.distance > o2.distance)
                return 1;
            else return -1;
        }
    }

    private void reloadRadars() {
        String url;
        if (location != null) {
            url = MainActivity.DATA_BEROADS_URL+"Radar.json?format=json&from="
                    + location.getLatitude()
                    + ","
                    + location.getLongitude() + "&area=40";
        } else {
            url = MainActivity.DATA_BEROADS_URL + "Radar.json?format=json";
        }

        gsonRequest = new GsonRequest<List<RadarItem>>(url,new TypeToken<List<RadarItem>>(){}.getType(),null,new Response.Listener<List<RadarItem>>() {
            @Override
            public void onResponse(List<RadarItem> response) {
                updateUI(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("",error.toString());
            }
        });
        gsonRequest.setTag(activity);
        activity.setProgressBarIndeterminateVisibility(false);
        activity.getRequestQueue().add(gsonRequest);
    }

}

