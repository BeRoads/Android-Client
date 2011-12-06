package com.profete162.WebcamWallonnes;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.profete162.WebcamWallonnes.adapter.TabsAdapter;
import com.profete162.WebcamWallonnes.adapter.TrafficAdapter;
import com.profete162.WebcamWallonnes.misc.Snippets;

public class TrafficActivity extends FragmentActivity {
	Boolean tabsSet = false;
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	FragmentActivity fa;
	static ArrayList<Traffic> list = new ArrayList<Traffic>();

	public static HashMap<Integer, Integer> codeLink = new HashMap<Integer, Integer>();

	static double GPS[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Loading...");
		GPS = Snippets.getLocationFromBundle(this.getIntent().getExtras());
		
		setTrafficTabs();
		fa = this;

	}

	public String parseTraffic() {

		try {
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				ThreadPolicy tp = ThreadPolicy.LAX;
				StrictMode.setThreadPolicy(tp);

			}

			String url = "http://91.121.10.214/The-DataTank/IWay/TrafficEvent/"+ getString(R.string.lan) +"/all/?format=json&from="
					+ GPS[0]
					+ ","
					+ GPS[1];
			System.out.println("*** URL:" + url);

			try {

				// Log.i("MY INFO", "Json Parser started..");
				Gson gson = new Gson();
				Reader r = new InputStreamReader(getJSONData(url));
				// Log.i("MY INFO", r.toString());
				TrafficList obj = gson.fromJson(r, TrafficList.class);
				// TODO DISTANCE
				for (Traffic traf : obj.item)
					list.add(traf);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "FAIL";
		}

		return "OK";

	}

	public class TrafficList {

		private List<Traffic> item;

		public List<Traffic> getTraffics() {
			return item;
		}

	}

	public class Traffic {
		private String source;
		private String message;
		private String category;
		private double lat;
		private double lng;

		public Traffic(String category, String message, String source, double lat,
				double lon) {
			this.source = source;
			this.message = message;
			this.category = category;
			this.lat = lat;
			this.lng = lon;
		}

		public String getSource() {
			return this.source;
		}
		
		public String getCategory() {
			return this.category;
		}

		public String getMessage() {
			return this.message;
		}

		public double getLat() {
			return this.lat;
		}

		public double getLon() {
			return this.lng;
		}


	}

	public void setTrafficTabs() {
		new SetTrafficTabs().execute();
	}

	public class SetTrafficTabs extends AsyncTask<String, Void, Void> {

		private String parsed;

		protected void onPreExecute() {
		}

		protected Void doInBackground(String... urls) {
			parsed = parseTraffic();
			return null;
		}

		protected void onPostExecute(Void unused) {
			getSupportActionBar().setTitle(parsed);

			ActionBar.Tab tab0 = getSupportActionBar().newTab().setText(parsed);

			mViewPager = (ViewPager) findViewById(R.id.pager);

			mTabsAdapter = new TabsAdapter(fa, getSupportActionBar(),
					mViewPager);

			mTabsAdapter.addTab(tab0, TrafficActivity.TrafficFragment.class, 0);

		}

	}

	public static class TrafficFragment extends ListFragment {

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			this.setListAdapter(new TrafficAdapter(getActivity(),
					R.layout.row_traffic, list, getActivity()
							.getLayoutInflater(), GPS[0], GPS[1]));
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);

			TrafficAdapter adapter = (TrafficAdapter) l.getAdapter();
			Traffic clickedItem = (Traffic) adapter.getItem(position);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(clickedItem.getMessage()).setCancelable(false)
					.setNeutralButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

								}
							});

			builder.create().show();

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("index", getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	public InputStream getJSONData(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter("http.useragent",
				" Appli de Waza_be " + System.getProperty("http.agent"));
		URI uri;
		InputStream data = null;
		try {
			uri = new URI(url);
			HttpGet method = new HttpGet(uri);
			HttpResponse response = httpClient.execute(method);
			data = response.getEntity().getContent();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
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
