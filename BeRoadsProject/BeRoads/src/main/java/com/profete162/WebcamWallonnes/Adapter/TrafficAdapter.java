package com.profete162.WebcamWallonnes.Adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.Utils.Utils;
import com.profete162.WebcamWallonnes.models.Traffic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TrafficAdapter extends ArrayAdapter<Traffic>{

	private LayoutInflater myLayoutInflater;
	protected List<Traffic> items;
	Location currentLoc;
	
	public TrafficAdapter(Context context, int textViewResourceId,List<Traffic> list,LayoutInflater layoutInflater,Location loc) {
		super(context, textViewResourceId, list);
		this.myLayoutInflater = layoutInflater;
		this.items = list;
		this.currentLoc=loc;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//

		View row = convertView;

		if (row == null) {			
			row = myLayoutInflater.inflate(R.layout.row_traffic, parent, false);
		}

		TextView tMax = (TextView) row.findViewById(R.id.tName);
		tMax.setText(items.get(position).getLocation());
		
		TextView tMin = (TextView) row.findViewById(R.id.tDesc);
		tMin.setText(items.get(position).getMessage());
		
		TextView tDis = (TextView) row.findViewById(R.id.tDist);
        if(currentLoc!=null){
            tDis.setVisibility(View.VISIBLE);
            int iDistance = (int) Utils.getDistance(currentLoc.getLatitude(), currentLoc.getLongitude(), items.get(position).getLat(), items.get(position).getLon())/100;
            tDis.setText((double) iDistance / 10 + "km");
        }else
            tDis.setVisibility(View.GONE);


        if(items.get(position).getTime()!=0){
            TextView tTime = (TextView) row.findViewById(R.id.tTime);
            Date date = new Date(items.get(position).getTime()*1000);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM HH:mm");
            tTime.setText(items.get(position).getSource()+" - "+formatter.format(date));
        }
		 return row;
	}
}
	

