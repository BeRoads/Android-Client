package com.profete162.WebcamWallonnes.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.Rss.RSSFeed;
import com.profete162.WebcamWallonnes.Rss.RSSItem;

public class RSSAdapter extends ArrayAdapter<RSSItem>{

	private LayoutInflater myLayoutInflater;
	private RSSFeed myRssFeed;
	
	public RSSAdapter(Context context, int textViewResourceId,List<RSSItem> list,LayoutInflater layoutInflater,RSSFeed rssFeed) {
		super(context, textViewResourceId, list);
		this.myLayoutInflater = layoutInflater;
		this.myRssFeed = rssFeed;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		//

		View row = convertView;

		if (row == null) {			
			row = myLayoutInflater.inflate(R.layout.row_rss, parent, false);
		}

		TextView listTitle = (TextView) row.findViewById(R.id.listtitle);
		
		listTitle.setText(myRssFeed.getList().get(position).getTitle());

		 return row;
	}
}
	

