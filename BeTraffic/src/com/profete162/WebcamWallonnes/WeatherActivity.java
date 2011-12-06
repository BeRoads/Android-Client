package com.profete162.WebcamWallonnes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.profete162.WebcamWallonnes.TrafficActivity.SetTrafficTabs;
import com.profete162.WebcamWallonnes.Weather.Weather;
import com.profete162.WebcamWallonnes.adapter.TabsAdapter;
import com.profete162.WebcamWallonnes.adapter.WeatherAdapter;
import com.profete162.WebcamWallonnes.misc.Snippets;

public class WeatherActivity extends FragmentActivity {
	Boolean tabsSet = false;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	
	static ArrayList<Weather> list=new ArrayList<Weather>();
	static double GPS[];
	public static HashMap<Integer, Integer> codeLink = new HashMap<Integer, Integer>();
	static FragmentActivity fa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_generic);

		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Loading...");

		fa = this;
		GPS = Snippets.getLocationFromBundle(this.getIntent().getExtras());

		setCodeLink();
		
		setWeatherTabs();

	}
	
	public void setWeatherTabs() {
		new SetWeatherTabs().execute();
	}

	public void setCodeLink() {
		codeLink.put(395, R.drawable.w12);
		codeLink.put(392, R.drawable.w16);
		codeLink.put(389, R.drawable.w24);
		codeLink.put(386, R.drawable.w16);
		codeLink.put(377, R.drawable.w21);
		codeLink.put(374, R.drawable.w13);
		codeLink.put(371, R.drawable.w12);
		codeLink.put(368, R.drawable.w11);
		codeLink.put(365, R.drawable.w13);
		codeLink.put(362, R.drawable.w13);
		codeLink.put(359, R.drawable.w18);
		codeLink.put(356, R.drawable.w10);
		codeLink.put(353, R.drawable.w09);
		codeLink.put(350, R.drawable.w21);
		codeLink.put(338, R.drawable.w20);
		codeLink.put(335, R.drawable.w12);
		codeLink.put(332, R.drawable.w20);
		codeLink.put(329, R.drawable.w20);
		codeLink.put(326, R.drawable.w11);
		codeLink.put(323, R.drawable.w11);
		codeLink.put(320, R.drawable.w19);
		codeLink.put(317, R.drawable.w21);
		codeLink.put(314, R.drawable.w21);
		codeLink.put(311, R.drawable.w21);
		codeLink.put(308, R.drawable.w18);
		codeLink.put(305, R.drawable.w10);
		codeLink.put(302, R.drawable.w18);
		codeLink.put(299, R.drawable.w10);
		codeLink.put(296, R.drawable.w17);
		codeLink.put(293, R.drawable.w17);
		codeLink.put(284, R.drawable.w21);
		codeLink.put(281, R.drawable.w21);
		codeLink.put(266, R.drawable.w17);
		codeLink.put(263, R.drawable.w09);
		codeLink.put(260, R.drawable.w07);
		codeLink.put(248, R.drawable.w07);
		codeLink.put(230, R.drawable.w20);
		codeLink.put(227, R.drawable.w19);
		codeLink.put(200, R.drawable.w16);
		codeLink.put(185, R.drawable.w21);
		codeLink.put(182, R.drawable.w21);
		codeLink.put(179, R.drawable.w13);
		codeLink.put(176, R.drawable.w09);
		codeLink.put(143, R.drawable.w06);
		codeLink.put(122, R.drawable.w04);
		codeLink.put(119, R.drawable.w03);
		codeLink.put(116, R.drawable.w02);
		codeLink.put(113, R.drawable.w01);

	}
	
	public class SetWeatherTabs extends AsyncTask<String, Void, Void> {

		private String parsed;

		protected void onPreExecute() {
		}

		protected Void doInBackground(String... urls) {
			parsed = parseWeather();
			return null;
		}

		protected void onPostExecute(Void unused) {
			getSupportActionBar().setTitle(parsed);

			ActionBar.Tab tab0 = getSupportActionBar().newTab().setText(parsed);

			mViewPager = (ViewPager) findViewById(R.id.pager);

			mTabsAdapter = new TabsAdapter(fa, getSupportActionBar(),
					mViewPager);

			mTabsAdapter.addTab(tab0, WeatherActivity.WeeklyWeatherFragment.class, 0);

		}

	}


	public static String parseWeather() {
		JSONObject dataObject = null;
		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				ThreadPolicy tp = ThreadPolicy.LAX;
				StrictMode.setThreadPolicy(tp);

			}
			URL url;
			url = new URL(
					"http://free.worldweatheronline.com/feed/weather.ashx?q="
							+ GPS[0]
							+ ","
							+ GPS[1]
							+ "&key=1df836d286132805111508&num_of_days=5&includeLocation=yes&format=json");
			System.out.println("URL = " + url);
			URLConnection tc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(tc
					.getInputStream()));

			String line;
			//System.out.println("*********** ICI********");

			while ((line = in.readLine()) != null) {
				JSONObject jo = new JSONObject(line);
				dataObject = jo.getJSONObject("data");

				JSONArray weatherArray = dataObject.getJSONArray("weather");
				
				list.clear();
				
				for (int i = 0; i < weatherArray.length(); i++) {
					JSONObject weatherObject = (JSONObject) weatherArray.get(i);
					list.add(new Weather(weatherObject.getJSONArray("weatherDesc").getJSONObject(0).getString("value"),
							weatherObject.getString("tempMaxC"),
							weatherObject.getString("tempMinC"),
							weatherObject.getString("winddir16Point"),
							weatherObject.getString("windspeedKmph"),
							weatherObject.getInt("weatherCode"),
							weatherObject.getString("date")));

				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		try {
			return dataObject.getJSONArray("nearest_area").getJSONObject(0).getJSONArray("areaName").getJSONObject(0).getString("value");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}

	}

	public static void setWeeklyTabs(String result) {
		
		fa.getSupportActionBar().setTitle(result);

	}

	public static class WeeklyWeatherFragment extends ListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setWeeklyTabs(parseWeather());
			this.setListAdapter(new WeatherAdapter(getActivity(),
					R.layout.row_weather, list,getActivity().getLayoutInflater()));
		}

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", getSupportActionBar()
				.getSelectedNavigationIndex());
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();

		default:
			Log.i("", "ID: " + item.getItemId());

		}

		return super.onOptionsItemSelected(item);

	}
}
