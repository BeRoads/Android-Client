package com.profete162.WebcamWallonnes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.tests.toolbox.GsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.profete162.WebcamWallonnes.Adapter.TrafficAdapter;
import com.profete162.WebcamWallonnes.Utils.NumberedListFragment;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.Utils.Web;
import com.profete162.WebcamWallonnes.models.Traffic;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TrafficFragment extends NumberedListFragment {

    private Location location;
    private MainActivity activity;
    private GsonRequest<List<Traffic>> gsonRequest;

    public void updateToLoc(Location newLocation) {
        try {
            this.location = newLocation;
            this.reloadTraffic();
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

        if (PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getBoolean("tuto", true) && ((DrawerActivity) this.getActivity()).mDrawerLayout != null) {
            this.getView().findViewById(R.id.tuto).setVisibility(View.VISIBLE);
        }

        this.getListView().setDivider(null);

        activity = ((MainActivity) getActivity());
        location = activity.loc;

        reloadTraffic();

    }

    private void displayError() {
    }

    public void updateUI(List<Traffic> result, boolean hideProgress) {
        if (result != null)
            try {
                if (this.getListAdapter() == null)
                    this.setListAdapter(new TrafficAdapter(getActivity(),
                            R.layout.row_traffic, result, getActivity()
                            .getLayoutInflater(), location));

                else {
                    TrafficAdapter a = (TrafficAdapter) this.getListAdapter();
                    a.clear();
                    for (Traffic aTraffic : result)
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

        if (gsonRequest != null) {
            gsonRequest.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void reloadTraffic(){
        String url;
        if (location != null) {
            url = MainActivity.DATA_BEROADS_URL+"TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json&from="
                    + location.getLatitude()
                    + ","
                    + location.getLongitude();
        } else {
            url = MainActivity.DATA_BEROADS_URL+"TrafficEvent/" + getString(R.string.lan) + "/all.json?format=json";
        }

        Log.d("","*** URL:" + url);

        gsonRequest = new GsonRequest<List<Traffic>>(url,new TypeToken<List<Traffic>>(){}.getType(),null,new Response.Listener<List<Traffic>>() {
            @Override
            public void onResponse(List<Traffic> response) {
                updateUI(response, true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("",error.toString());
            }
        });
        gsonRequest.setTag(activity);
        activity.getRequestQueue().add(gsonRequest);
    }

}

