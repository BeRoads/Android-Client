package com.profete162.WebcamWallonnes.parking;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.misc.AbstractAdapter;

public class ParkingLocationAdapter extends AbstractAdapter<Parking> {
	public ParkingLocationAdapter(Context context, int rowResourceId,
			ArrayList<Parking> items) {
		super(context, rowResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) super.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_closest, null);
		}
		Parking station = items.get(position);
		if (station != null) {
			TextView tvName = (TextView) v.findViewById(R.id.tv_name);
			TextView tvGps = (TextView) v.findViewById(R.id.tv_adress);
			TextView tvDistance = (TextView) v.findViewById(R.id.tv_dis);
			// ImageView ivExpress = (ImageView) v.findViewById(R.id.ivExpress);

			tvName.setText(station.getStation());
			tvGps.setText(station.getAdress());

			int iDistance = (int) (Double.valueOf(station.getDistance()) / 100);
			tvDistance.setText((double) iDistance / 10 + "km");
			/*
			 * switch(station.getType()){ case 1:
			 * ivExpress.setImageResource(R.drawable.express); break;
			 * 
			 * case 2: ivExpress.setImageResource(R.drawable.esso); break;
			 * 
			 * case 3: ivExpress.setImageResource(R.drawable.shell); break; }
			 */

		}
		return v;
	}

}
