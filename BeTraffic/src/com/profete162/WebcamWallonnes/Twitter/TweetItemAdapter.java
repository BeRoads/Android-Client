package com.profete162.WebcamWallonnes.Twitter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.R;

public class TweetItemAdapter extends ArrayAdapter<Tweet> {
	private LayoutInflater myLayoutInflater;
		ImageLoader imageLoader;
		ArrayList<Tweet> tweets;
		public TweetItemAdapter(Context context, int textViewResourceId,
				ArrayList<Tweet> tweets,LayoutInflater layoutInflater) {
			super(context, textViewResourceId, tweets);
			this.myLayoutInflater = layoutInflater;
			this.tweets=tweets;
			this.imageLoader = new ImageLoader(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// return super.getView(position, convertView, parent);
			View row = convertView;

			if (row == null) {			
				row = myLayoutInflater.inflate(R.layout.row_tweet, parent, false);
			}
			
			TextView username = (TextView) row.findViewById(R.id.username);
			TextView message = (TextView) row.findViewById(R.id.message);
			ImageView image = (ImageView) row.findViewById(R.id.avatar);

			final Tweet tweet = tweets.get(position);
			if (tweet != null) {
				username.setText(tweet.from_user);
				message.setText(tweet.text);
				image.setTag(tweet.profile_image_url);
				imageLoader.DisplayImage(tweet.profile_image_url, row.getContext(),
						image);

			}

			return row;
		}
	}
