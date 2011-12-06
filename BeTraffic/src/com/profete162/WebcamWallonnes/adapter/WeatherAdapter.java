package com.profete162.WebcamWallonnes.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.WeatherActivity;
import com.profete162.WebcamWallonnes.Weather.Weather;

public class WeatherAdapter extends ArrayAdapter<Weather>{

	private LayoutInflater myLayoutInflater;
	protected ArrayList<Weather> items;
	
	public WeatherAdapter(Context context, int textViewResourceId,ArrayList<Weather> list,LayoutInflater layoutInflater) {
		super(context, textViewResourceId, list);
		this.myLayoutInflater = layoutInflater;
		this.items = list;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//

		View row = convertView;

		if (row == null) {			
			row = myLayoutInflater.inflate(R.layout.row_weather, parent, false);
		}

		TextView tMax = (TextView) row.findViewById(R.id.tmax);
		tMax.setText(items.get(position).getTMax()+"°C");
		
		TextView tMin = (TextView) row.findViewById(R.id.tmin);
		tMin.setText(items.get(position).getTMin()+"°C");
		
		TextView desc = (TextView) row.findViewById(R.id.desc);
		desc.setText(items.get(position).getDesc());
		
		TextView date = (TextView) row.findViewById(R.id.date);
		date.setText(items.get(position).getDate());
		
		ImageView icon= (ImageView) row.findViewById(R.id.icon);
		//System.out.println(items.get(position).getCode());
		//System.out.println(WeatherActivity.codeLink.get(items.get(position).getCode()));
		
		icon.setImageResource(WeatherActivity.codeLink.get(items.get(position).getCode()));
		
		 return row;
	}
}
	

