package com.profete162.WebcamWallonnes.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.RadarFragment;
import com.profete162.WebcamWallonnes.TrafficFragment.Traffic;
import com.profete162.WebcamWallonnes.Utils.Utils;

import java.util.ArrayList;


public class RadarAdapter extends ArrayAdapter<RadarFragment.Item>{

	private LayoutInflater myLayoutInflater;
	protected ArrayList<RadarFragment.Item> items;
	double currentLat; double currentLon;

	public RadarAdapter(Context context, int textViewResourceId, ArrayList<RadarFragment.Item> list, LayoutInflater layoutInflater, double currentLat, double currentLon) {
		super(context, textViewResourceId, list);
		this.myLayoutInflater = layoutInflater;
		this.items = list;
		this.currentLat = currentLat;
		this.currentLon = currentLon;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//

		View row = convertView;

		if (row == null) {			
			row = myLayoutInflater.inflate(R.layout.row_radar, parent, false);
		}

        RadarFragment.Item item=items.get(position);

        TextView tLimit = (TextView) row.findViewById(R.id.tLimit);
        tLimit.setText(""+item.getSpeedLimit());

        TextView tType = (TextView) row.findViewById(R.id.tType);
        tType.setText(item.getType());

		TextView tDesc = (TextView) row.findViewById(R.id.tDesc);
        tDesc.setText(item.getAddress());
		
		TextView tDis = (TextView) row.findViewById(R.id.tDist);
        int iDistance = (int) Utils.getDistance(currentLat, currentLon, item.getLat(), item.getLng())/100;
		tDis.setText((double) iDistance / 10 + "km");

		 return row;
	}
}
	

