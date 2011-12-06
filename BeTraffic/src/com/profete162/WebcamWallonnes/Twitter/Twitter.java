package com.profete162.WebcamWallonnes.Twitter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.google.gson.Gson;
import com.profete162.WebcamWallonnes.R;
import com.profete162.WebcamWallonnes.Tools.Tools;

public class Twitter {

	public static ArrayList<Tweet> getTweets(int page, final ListFragment lf,
			final String appName, String agentName, ArrayList<String> tagList) {

		final Activity activity = lf.getActivity();

		SharedPreferences mDefaultPrefs = PreferenceManager
				.getDefaultSharedPreferences(activity.getBaseContext());
		;
		String accounts = "";

		for (String aTag : tagList) {
			if (mDefaultPrefs.getBoolean("tweet" + aTag, true))
				accounts += "%20OR%20" + aTag;
		}

		final String searchUrl = "http://search.twitter.com/search.json?q="
				+ appName + accounts + "&rpp=100&page=" + page;

		System.out.println(searchUrl);
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		try {

			final InputStream is = Tools.DownloadJsonFromUrlAndCacheToSd(
					searchUrl, "/Android/data/" + appName + "/Twitter", null,
					activity.getBaseContext(), agentName);

			new Thread(new Runnable() {
				public void run() {
					Gson gson = new Gson();
					final Reader reader = new InputStreamReader(is);
					final Tweets tweets = gson.fromJson(reader, Tweets.class);
					Log.i("Twitter.Java",
							"Tweets total: " + tweets.results.size());

					activity.runOnUiThread(new Thread(new Runnable() {
						public void run() {
							lf.setListAdapter(new TweetItemAdapter(activity,
									R.layout.row_tweet, tweets.results,
									activity.getLayoutInflater()));

						}
					}));
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
			activity.runOnUiThread(new Thread(new Runnable() {
				public void run() {
					// TextView tv = (TextView) context.findViewById(R.id.fail);
					// tv.setVisibility(View.VISIBLE);
				}
			}));
		}

		/*
		 * HttpClient client = new DefaultHttpClient(); HttpGet get = new
		 * HttpGet(searchUrl);
		 * 
		 * ResponseHandler<String> responseHandler = new BasicResponseHandler();
		 * 
		 * String responseBody = null; try { responseBody = client.execute(get,
		 * responseHandler); } catch (Exception ex) { ex.printStackTrace(); }
		 * 
		 * JSONObject jsonObject = null; JSONParser parser = new JSONParser();
		 * 
		 * StringReader myStringReader = new StringReader(responseBody);
		 * 
		 * try { Object obj = parser.parse(myStringReader); jsonObject =
		 * (JSONObject) obj; } catch (Exception ex) { Log.v("TEST",
		 * "Exception: " + ex.getMessage()); }
		 * 
		 * JSONArray arr = null;
		 * 
		 * try { Object j = jsonObject.get("results"); arr = (JSONArray) j; }
		 * catch (Exception ex) { Log.v("TEST", "Exception: " +
		 * ex.getMessage()); }
		 * 
		 * for (Object t : arr) { Tweet tweet = new Tweet(((JSONObject)
		 * t).get("from_user") .toString(), ((JSONObject)
		 * t).get("text").toString(), ((JSONObject)
		 * t).get("profile_image_url").toString()); tweets.add(tweet); }
		 */
		return tweets;
	}
}
