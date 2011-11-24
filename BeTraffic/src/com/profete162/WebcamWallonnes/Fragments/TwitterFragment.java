package com.profete162.WebcamWallonnes.Fragments;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ImageView;
import android.widget.Toast;

import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.Twitter.Tweet;
import com.profete162.WebcamWallonnes.Twitter.Twitter;
import com.profete162.WebcamWallonnes.adapter.TweetItemAdapter;

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
			final TwitterFragment fragment=this;
			final Activity a = getActivity();
			new Thread(new Runnable() {
				public void run() {
					try {
						tweets = Twitter.getTweets("BeRoads", 1, getActivity());
						System.out.println("***2" + tweets.size());
						a.runOnUiThread(new Thread(new Runnable() {
							public void run() {
								System.out.println("***" + tweets.size());
								TweetItemAdapter adapter=new TweetItemAdapter(getActivity(),R.layout.row_tweet, tweets,getActivity().getLayoutInflater());
								System.out.println("***" + adapter);
								fragment.setListAdapter(adapter);
							}
						}));
					} catch (final Exception e) {
						e.printStackTrace();
						a.runOnUiThread(new Thread(new Runnable() {
							public void run() {
								e.printStackTrace();
								Toast.makeText(a, "Please connect to Internet\n",
										Toast.LENGTH_LONG).show();
								a.finish();

							}
						}));

					}

				}
			}).start();
		}
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			File file = new File(android.os.Environment
					.getExternalStorageDirectory(), "data/BeRoads");
			File[] files = file.listFiles();
			for (File f : files)
				f.delete();
		} catch (Exception e) {
		}
	}
	


}