package com.profete162.WebcamWallonnes.Twitter;

import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Twitter {


	public static ArrayList<Tweet> getTweets(String searchTerm, int page,Context context) {
		
		SharedPreferences mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);;	
		String accounts = "";
		
		if(mDefaultPrefs.getBoolean("mHarkor", true))
			accounts+="%20OR%20@Harkor";
		
		if(mDefaultPrefs.getBoolean("mQkaiser", true))
			accounts+="%20OR%20@QKaiser";
		
		if(mDefaultPrefs.getBoolean("mWazaBe", true))
			accounts+="%20OR%20@Waza_be";
		
		String searchUrl = "http://search.twitter.com/search.json?q="
				+ searchTerm +accounts+ "&rpp=100&page=" + page;
		System.out.println(searchUrl);		
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();

		
		/*
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(searchUrl);

		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		String responseBody = null;
		try {
			responseBody = client.execute(get, responseHandler);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		JSONObject jsonObject = null;
		JSONParser parser = new JSONParser();

		StringReader myStringReader = new StringReader(responseBody);

		try {
			Object obj = parser.parse(myStringReader);
			jsonObject = (JSONObject) obj;
		} catch (Exception ex) {
			Log.v("TEST", "Exception: " + ex.getMessage());
		}

		JSONArray arr = null;

		try {
			Object j = jsonObject.get("results");
			arr = (JSONArray) j;
		} catch (Exception ex) {
			Log.v("TEST", "Exception: " + ex.getMessage());
		}

		for (Object t : arr) {
			Tweet tweet = new Tweet(((JSONObject) t).get("from_user")
					.toString(), ((JSONObject) t).get("text").toString(),
					((JSONObject) t).get("profile_image_url").toString());
			tweets.add(tweet);
		}
	*/
		return tweets;
	}

}
