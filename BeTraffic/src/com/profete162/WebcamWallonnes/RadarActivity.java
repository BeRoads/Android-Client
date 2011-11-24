package com.profete162.WebcamWallonnes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.RadarLocationAdapter;
import com.profete162.WebcamWallonnes.adapter.TabsAdapter;
import com.profete162.WebcamWallonnes.misc.Snippets;
import com.profete162.WebcamWallonnes.radar.Radar;

public class RadarActivity extends FragmentActivity {
	/** Called when the activity is first created. */

	private static final String TAG = "BETRAINS";

	private static Radar clickedItem;
	private static RadarLocationAdapter myRadarAdapter;

	static FragmentActivity fa;

	static double GPS[];

	ArrayList<Radar> stationList = new ArrayList<Radar>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_generic);

		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Loading...");

		fa = this;
		GPS = Snippets.getLocationFromBundle(this.getIntent().getExtras());
		
		ActionBar.Tab tab0 = getSupportActionBar().newTab().setText(
				"Loading...");

		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);

		TabsAdapter mTabsAdapter = new TabsAdapter(fa, getSupportActionBar(),
				mViewPager);

		mTabsAdapter.addTab(tab0, RadarActivity.RadarFragment.class, 0);

	}

	public static class RadarFragment extends ListFragment {
		private Thread thread = null;
		private DataBaseHelper myDbHelper;
		ArrayList<Radar> radarList = new ArrayList<Radar>();
		ListFragment lf;

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			updateListToLocation();
			
			myDbHelper = new DataBaseHelper(getActivity());
			try {
				myDbHelper.createDataBase();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			lf = this;
			try {
				myDbHelper.openDataBase(DataBaseHelper.DB_NAME_PARKING);

			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}

			myDbHelper.close();
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);

			RadarLocationAdapter adapter = (RadarLocationAdapter) l
					.getAdapter();
			try {
				clickedItem = (Radar) adapter.getItem(position);
				// TODO
				final CharSequence[] items = { "See on a Map" };

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Radar: " + clickedItem.getId());
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							break;
						}

					}
				});
				AlertDialog alert = builder.create();
				alert.show();

			} catch (Exception e) {

			}

		}

		private void updateListToLocation() {
			Runnable updateListRunnable = new Runnable() {
				public void run() {
					updateListToLocationThread(GPS[0], GPS[1]);
				}
			};
			Log.v(TAG, "updateListToLocation");
			thread = new Thread(null, updateListRunnable, "MagentoBackground");
			thread.start();

		}

		/**
		 * The thread that is launched to read the database and compare each
		 * Station location to my current location.
		 */
		@SuppressWarnings("unchecked")
		private void updateListToLocationThread(double lat, double lon) {

			myDbHelper.openDataBase(DataBaseHelper.DB_NAME_RADAR);
			Cursor locationCursor = myDbHelper.fetchAllRadar();
			Log.i(TAG, "size in updateListToLocationThread: "
					+ locationCursor.getCount());

			radarList.clear();

			for (int i = 0; i < locationCursor.getCount(); i++) {
				if (thread.isInterrupted()) {
					break;
				}
				compareStationsListToMyLocation(locationCursor, i, lat, lon);
			}
			Collections.sort(radarList);
			Looper.prepare();
			RadarLocationAdapter locationAdapter = new RadarLocationAdapter(
					getActivity(), R.layout.row_closest, radarList);
			myRadarAdapter = locationAdapter;
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					lf.setListAdapter(myRadarAdapter);
					getSupportActivity().getSupportActionBar().setTitle("OK");
				}
			});

			myDbHelper.close();

		}

		public void compareStationsListToMyLocation(Cursor locationCursor,
				int i, double lat, double lon) {
			locationCursor.moveToPosition(i);
			String strName = locationCursor.getString(locationCursor
					.getColumnIndex("name"));

			double iLat = locationCursor.getDouble(locationCursor
					.getColumnIndex("lat"));

			double iLon = locationCursor.getDouble(locationCursor
					.getColumnIndex("lon"));

			String sAdress = locationCursor.getString(locationCursor
					.getColumnIndex("name"));

			double dDis = Snippets.getDistance(lat, lon, iLat, iLon);

			radarList
					.add(new Radar(strName, iLat, iLon, dDis + "", sAdress, 0,
							locationCursor.getInt(locationCursor
									.getColumnIndex("id"))));
		}
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