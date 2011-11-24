package com.profete162.WebcamWallonnes;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.MenuAdapter;
import com.viewpagerindicator.CirclePageIndicator;

public class WelcomeActivity extends FragmentActivity {

	private static SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private DataBaseHelper myDbHelper;

	private LocationManager locationManager;
	private MyGPSLocationListener locationGpsListener;
	private MyNetworkLocationListener locationNetworkListener;
	private Location lastLocation;

	TextView tvLocation;
	TextView tvAccuraty;
	
	
	List<Address> addresses = null;

	@Override
	public void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);

		myDbHelper = new DataBaseHelper(this);
		// On each update, Update DB.
		String myVersion = "";
		PackageManager manager = this.getPackageManager();
		try {
			myVersion = (manager.getPackageInfo(this.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		System.out.println("***" + myVersion + "/"
				+ settings.getString("pVersion", "X") + "***");
		if (!myVersion.equals(settings.getString("pVersion", "X"))) {
			try {
				 Toast.makeText(this, "Database Updated",
				 Toast.LENGTH_SHORT).show();
				myDbHelper.forceCreateDataBase(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, "Unable to create database", Toast.LENGTH_LONG).show();
			}
		}

		editor = settings.edit();
		editor.putString("pVersion", myVersion);
		// Don't forget to commit your edits!!!
		editor.commit();

		setWelcomeContent();

		tvLocation = (TextView) findViewById(R.id.tv_position);
		tvAccuraty = (TextView) findViewById(R.id.tv_accuracy);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationGpsListener = new MyGPSLocationListener();
		locationNetworkListener = new MyNetworkLocationListener();

		new Thread(new Runnable() {
			public void run() {
				displayLocation();
			}
		}).start();

		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */

	}
	
	public void setWelcomeContent(){
		setContentView(R.layout.activity_welcome);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		
		ViewPager mPager=(ViewPager) this.findViewById(R.id.pager);
		MenuAdapter adapter= new MenuAdapter(this);
		mPager.setAdapter(adapter);
		
		CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setSnap(true);
		
	}

	public void displayLocation() {
		
		List<String> providers = locationManager.getProviders(true);
		Location l = null;
		for (int i = providers.size() - 1; i >= 0; i--) {
			l = locationManager.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}

		lastLocation = l;
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		
		try {
			addresses = geocoder.getFromLocation(lastLocation.getLatitude(), lastLocation
					.getLongitude(), 1);
			handler.sendEmptyMessage(0);


		} catch (IOException e) {
			handler.sendEmptyMessage(1);

			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0) {

				tvLocation.setText(addresses.get(0).getAddressLine(0) + ", "
						+ addresses.get(0).getLocality());
				tvAccuraty.setText("(" + (int) lastLocation.getAccuracy() + "m)");

				}
			if(msg.what == 1) {
				String masque = new String("#0.##");
				DecimalFormat form = new DecimalFormat(masque);
				tvLocation.setText(form.format(lastLocation.getLatitude()) + ";"
						+ form.format(lastLocation.getLongitude())+" @"+lastLocation.getAccuracy()+"m");
				tvAccuraty.setText("(No Internet)");

				}


		};

	};

	public void onDefaultClick(View v) {
		Toast
				.makeText(getBaseContext(),
						"En cours de d�veloppement, soyez patients.",
						Toast.LENGTH_LONG).show();
	}

	public void onCameraClick(View v) {
		Intent i = new Intent(this, CamerasActivity.class);
		putBundle(i);
	}

	public void onTrafficClick(View v) {
		Intent i = new Intent(this, TrafficActivity.class);
		putBundle(i);
	}

	public void onRadarClick(View v) {
		Intent i = new Intent(this, RadarActivity.class);
		putBundle(i);
	}

	public void onParkingClick(View v) {
		Intent i = new Intent(this, ParkingActivity.class);
		putBundle(i);
	}

	public void onTwitClick(View v) {
		Intent i = new Intent(this, TwitterActivity.class);
		startActivity(i);
	}

	public void onWeatherClick(View v) {
		Intent i = new Intent(this, WeatherActivity.class);
		putBundle(i);
	}

	public void onAboutClick(View v) {

		Dialog dialog = new Dialog(this);

		dialog.setContentView(R.layout.about_dialog);
		dialog.setTitle("About us");

		dialog.show();
	}
	private void putBundle(Intent i){
		try{
			Bundle bundle = new Bundle();
			bundle.putDouble("lat", lastLocation.getLatitude());
			bundle.putDouble("lng", lastLocation.getLongitude());
			i.putExtras(bundle);
			startActivity(i);
		}catch(Exception e){
			Toast.makeText(getBaseContext(), "Please wait for location", Toast.LENGTH_LONG).show();
		}

	}

	private class MyGPSLocationListener implements LocationListener

	{

		public void onLocationChanged(final Location loc) {

			if (loc != null && lastLocation != null) {
				// Toast.makeText(getBaseContext(), "GPS: "+loc.getAccuracy(),
				// Toast.LENGTH_LONG).show();
				
				lastLocation = loc;
				if (loc.getAccuracy() <= 25) {
					new Thread(new Runnable() {
						public void run() {
							displayLocation();
						}
					}).start();
					locationManager.removeUpdates(locationNetworkListener);
					locationManager.removeUpdates(locationGpsListener);
				}

			}

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status,

		Bundle extras) {
		}

	}

	private class MyNetworkLocationListener implements LocationListener

	{

		public void onLocationChanged(final Location loc) {

			if (loc != null && lastLocation != null && locationManager != null) {
				// Toast.makeText(getBaseContext(),
				// "Network: "+loc.getAccuracy(), Toast.LENGTH_LONG).show();
				locationManager.removeUpdates(locationNetworkListener);
				lastLocation = loc;
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {

		}

		@Override
		public void onProviderEnabled(String arg0) {

		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		final long INT_MINTIME = 0;
		final long INT_MINDISTANCE = 0;
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				INT_MINTIME, INT_MINDISTANCE, locationGpsListener);

		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, INT_MINTIME, INT_MINDISTANCE,
				locationNetworkListener);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (locationManager != null) {
			locationManager.removeUpdates(locationGpsListener);
			locationManager.removeUpdates(locationNetworkListener);
		}

		locationManager = null;
	}

}
