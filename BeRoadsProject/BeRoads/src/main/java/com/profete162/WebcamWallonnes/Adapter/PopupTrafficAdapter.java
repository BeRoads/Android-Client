package com.profete162.WebcamWallonnes.Adapter;

/**
 * Created by 201601 on 18/06/13.
 */

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.Utils.Utils;

public class PopupTrafficAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater inflater = null;

    public PopupTrafficAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return (null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        String[] text = marker.getTitle().split(";");
        View popup;
        switch (text[0].charAt(0)) {
            case 'W':
                popup=inflater.inflate(R.layout.popup_webcam, null);
                TextView tTitle = (TextView) popup.findViewById(R.id.tTitle);
                tTitle.setText(text[2]);
                return (popup);
            case 'R':
                popup=inflater.inflate(R.layout.popup_radar, null);
                TextView tDesc = (TextView) popup.findViewById(R.id.tDesc);
                tDesc.setText(marker.getSnippet().split(";")[0]);
                TextView tDist = (TextView) popup.findViewById(R.id.tDist);
                tDist.setText(text[2]);
                TextView tName = (TextView) popup.findViewById(R.id.tName);
                tName.setText(text[1]);
                return (popup);
            default:
                popup=inflater.inflate(R.layout.popup_traffic, null);
                TextView tv = (TextView) popup.findViewById(R.id.tName);
                tv.setText(text[2]);
                tv = (TextView) popup.findViewById(R.id.tDesc);
                tv.setText(marker.getSnippet());
                return (popup);
        }




    }

}