package com.profete162.WebcamWallonnes.Twitter;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ImageView;


public class TwitterFragment extends ListFragment {
	String url = "";
	ImageView image = null;
	static ArrayList<Tweet> tweets = new ArrayList<Tweet>();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		tweets = Twitter.getTweets(1,this, "BeRoads","Waza_be: BeTraffic ",
				new ArrayList<String>());

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			File file = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"data/BeRoads");
			File[] files = file.listFiles();
			for (File f : files)
				f.delete();
		} catch (Exception e) {
		}
	}

}