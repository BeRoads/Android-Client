package com.profete162.WebcamWallonnes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
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
import android.widget.Toast;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.TabsAdapter;
import com.profete162.WebcamWallonnes.misc.Snippets;
import com.profete162.WebcamWallonnes.parking.Parking;
import com.profete162.WebcamWallonnes.parking.ParkingLocationAdapter;

public class ParkingActivity extends FragmentActivity {
	/** Called when the activity is first created. */

	private static final String TAG = "BETRAINS";

	static FragmentActivity fa;
	private static Parking clickedItem;
	private static ParkingLocationAdapter myLocationAdapter;

	static double GPS[];

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

		mTabsAdapter.addTab(tab0, ParkingActivity.ParkingFragment.class, 0);

	}

	public static class ParkingFragment extends ListFragment {
		private Thread thread = null;
		private DataBaseHelper myDbHelper;
		ArrayList<Parking> stationList = new ArrayList<Parking>();
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

			ParkingLocationAdapter adapter = (ParkingLocationAdapter) l
					.getAdapter();
			try {
				clickedItem = (Parking) adapter.getItem(position);
				// TODO
				final CharSequence[] items = { "See on a Map", "Navigation" };

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("Station: " + clickedItem.getId());
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						switch (item) {
						case 0:
							try {
								final Intent myIntent = new Intent(
										android.content.Intent.ACTION_VIEW,
										Uri.parse("geo:+"
												+ (double) clickedItem.getLat()
												/ 1E7 + ","
												+ (double) clickedItem.getLon()
												/ 1E7));
								startActivity(myIntent);
							} catch (Exception e) {
								(Toast.makeText(getActivity(),
										"GoogleMap not found",
										Toast.LENGTH_LONG)).show();
							}
							break;

						case 1:
							try {
								Uri uri = Uri.parse("google.navigation:q="
										+ clickedItem.getAdress());
								Intent it = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(it);
							} catch (ActivityNotFoundException e) {
								(Toast.makeText(getActivity(),
										"Navigation not found",
										Toast.LENGTH_LONG)).show();
							}
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

			myDbHelper.openDataBase(DataBaseHelper.DB_NAME_PARKING);
			Cursor locationCursor = myDbHelper.fetchAllParking();
			Log.i(TAG, "size in updateListToLocationThread: "
					+ locationCursor.getCount());

			stationList.clear();

			for (int i = 0; i < locationCursor.getCount(); i++) {
				if (thread.isInterrupted()) {
					break;
				}
				compareStationsListToMyLocation(locationCursor, i, lat, lon);
			}
			Collections.sort(stationList);
			Looper.prepare();
			ParkingLocationAdapter locationAdapter = new ParkingLocationAdapter(
					getActivity(), R.layout.row_closest, stationList);
			myLocationAdapter = locationAdapter;
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					lf.setListAdapter(myLocationAdapter);
					getSupportActivity().getSupportActionBar().setTitle("OK");
				}
			});

			myDbHelper.close();

		}

		/**
		 * ProgressDialog that stop thread when back key is pressed
		 */
		class MyProgressDialog extends ProgressDialog {
			public MyProgressDialog(Context context) {
				super(context);
			}

			@Override
			public void onBackPressed() {
				super.onBackPressed();
				myDbHelper.close();
				thread.interrupt();
				return;
			}
		}

		public void compareStationsListToMyLocation(Cursor locationCursor,
				int i, double lat, double lon) {
			locationCursor.moveToPosition(i);
			String strName = locationCursor.getString(locationCursor
					.getColumnIndex("nom"))
					+ " ("
					+ locationCursor.getString(locationCursor
							.getColumnIndex("places")) + ")";

			double iLat = locationCursor.getDouble(locationCursor
					.getColumnIndex("lat"));

			double iLon = locationCursor.getDouble(locationCursor
					.getColumnIndex("lon"));

			String sAdress = locationCursor.getString(locationCursor
					.getColumnIndex("localisation"));

			double dDis = Snippets
					.getDistance(lat, lon, iLat, iLon);

			stationList.add(new Parking(strName, iLat, iLon, dDis + "",
					sAdress, 0, locationCursor.getInt(locationCursor
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