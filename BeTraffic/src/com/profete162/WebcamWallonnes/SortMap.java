package com.profete162.WebcamWallonnes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.ImageMapReceivedCallback;
import com.profete162.WebcamWallonnes.misc.Snippets;

public class SortMap extends FragmentActivity  implements LocationListener , ImageMapReceivedCallback {

	/** Called when the activity is first created. */

	static Cursor webcamCursor;
	static DataBaseHelper mDbHelper;
	static SortMap mContext;
	
	private MapView mMap;
	private MapController mMapController;
	private Drawable marker;

	private double geoLatitude;
	private double geoLongitude;
	
	private MapController mController;

	private GeoPoint mGeoPoint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);
		geoLatitude = 40.334585;
		geoLongitude = 5.0236548;

		mMap = (MapView) findViewById(R.id.myGmap);
		mMap.setBuiltInZoomControls(true);
		mMap.setSatellite(false);
		setmMapController(mMap.getController());

		mDbHelper = new DataBaseHelper(this);
		mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
		webcamCursor = mDbHelper.fetchAllWebcam();

		marker = getResources().getDrawable(R.drawable.cible);
		ItemizedOverlayPerso myOverlay = new ItemizedOverlayPerso(marker);
		
		for (int i=0;i<webcamCursor.getCount();i++){
			webcamCursor.moveToPosition(i);
			float lat= Float.valueOf(webcamCursor.getString(webcamCursor.getColumnIndex("Lat")));
			float lon= Float.valueOf(webcamCursor.getString(webcamCursor.getColumnIndex("Lon")));
			GeoPoint gp = new GeoPoint((int) (lat * 1E6),
					(int) (lon * 1E6));
			myOverlay.addPoint(gp);	
		}

		
		mMap.getOverlays().add(myOverlay);
		mController = mMap.getController();
		
		 //MapController mController = mMap.getController();
		 //mController.setCenter(gp); mController.setZoom(15);
		
		float lat=(float) 50.133 ;
		float lon= (float) 4.733;
		GeoPoint gp = new GeoPoint((int) (lat * 1E6),
				(int) (lon * 1E6));
		
		mMap.getController().setCenter(gp);
		mMap.getController().setZoom(8);

	}
	
	public class ItemizedOverlayPerso extends ItemizedOverlay<OverlayItem> {

		private List<GeoPoint> points = new ArrayList<GeoPoint>();

		public ItemizedOverlayPerso(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}

		@Override
		protected OverlayItem createItem(int i) {
			GeoPoint point = points.get(i);
			return new OverlayItem(point, "Title", "Description");
		}

		@Override
		public int size() {
			return points.size();
		}

		public void addPoint(GeoPoint point) {
			this.points.add(point);
			populate();
		}

		public void clearPoint() {
			this.points.clear();
			populate();
		}

		//
		// method for events when the user clicks on any marker ...
		//
		@Override
		protected boolean onTap(int index) {
			Log.i("Hub", "Tap registered on ItemizedOverlay on ITEM #"+index);
			stationDetailDialog(index);
			return true;
		}
	}
public void stationDetailDialog(int index) {
		
		webcamCursor.moveToPosition(index);
		
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.camera_dialog);

		int picId=Integer.valueOf(webcamCursor.getString(webcamCursor
				.getColumnIndex("_id")));
		char cat=webcamCursor.getString(webcamCursor
				.getColumnIndex("Cat")).charAt(0);
		dialog.setTitle(webcamCursor.getString(webcamCursor
				.getColumnIndex("City")));
		ImageView image = (ImageView) dialog.findViewById(R.id.image);

		Snippets.createImageMapReceiver(picId,cat,image,this);
		

		image.setImageResource(R.drawable.icon);

		dialog.show();
	}



	public void Train_Detail_Dialog() {

	}
/*
	@Override
	protected void onResume() {
		super.onResume();

		myLocationOverlay.enableMyLocation();
		myLocationOverlay.enableCompass();
		Log.i("Profete162", "enable compass");
	}

	@Override
	protected void onPause() {
		
		super.onPause();		
		myLocationOverlay.disableCompass();
		myLocationOverlay.disableMyLocation();
		Log.i("Profete162", "compass" + myLocationOverlay.isMyLocationEnabled());

	}*/
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// menu.add(0,100,0,"Zoom In");
		// menu.add(0,101,0,"Zoom Out");
		//menu.add(0, 102, 0, "Satellite");
		// menu.add(0,103,0,"Trafic");
		// menu.add(0,104,0,"Street view");
		//menu.add(0, 105, 0, "Exit").setIcon(
			//	android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case 100: monControler.setZoom(maMap.getZoomLevel() + 1) ;break;
		// case 101: monControler.setZoom(maMap.getZoomLevel() - 1) ;break;
		case 102:
			mMap.setSatellite(!mMap.isSatellite());
			break;
		// case 103: maMap.setTraffic(!maMap.isTraffic()) ;break;
		// case 104: maMap.setStreetView(!maMap.isStreetView()) ;break;
		case 105:
			finish();

		
		return true;
	}
}*/

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	public void setmMapController(MapController mMapController) {
		this.mMapController = mMapController;
	}

	public MapController getmMapController() {
		return mMapController;
	}

	public void setmGeoPoint(GeoPoint mGeoPoint) {
		this.mGeoPoint = mGeoPoint;
	}

	public GeoPoint getmGeoPoint() {
		return mGeoPoint;
	}
	
	public class ImageDisplayer implements Runnable {
		public ImageView view;
		public Bitmap bmp;

		public ImageDisplayer(ImageView imageView, Bitmap bmp) {
			this.view = imageView;
			this.bmp = bmp;
		}

		public void run() {
			view.setImageBitmap(bmp);
		}

	}

	public class ImageMapReceiver extends Thread {
		String url;
		ImageMapReceivedCallback callback;
		ImageView view;

		public ImageMapReceiver(String url, ImageMapReceivedCallback callback,
				ImageView view) {
			this.url = url;
			this.callback = callback;
			this.view = view;
			start();
		}

		public void run() {
			try {
				HttpURLConnection conn = (HttpURLConnection) (new URL(url))
						.openConnection();
				conn.connect();
				ImageDisplayer displayer = new ImageDisplayer(view,
						BitmapFactory.decodeStream(conn.getInputStream()));
				callback.onImageReceived(displayer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void onImageReceived(ImageDisplayer displayer) {
		// TODO Auto-generated method stub
		this.runOnUiThread(displayer);

	}
	
	public static class AppMapListFragment extends Fragment  {
		String url="";
		ImageView image=null;
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			//registerForContextMenu(getExpandableListView());
			mDbHelper = new DataBaseHelper(getActivity());
			mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
			webcamCursor = mDbHelper.fetchAllWebcam();
			System.out.println("***" + webcamCursor);
			

		}

		
		 private class DownloadPicTask extends AsyncTask<String, Integer, Bitmap> {
		     protected Bitmap doInBackground(String... url) {
					Bitmap bm=null;
					try {
				        final URLConnection conn = new URL(url[0]).openConnection();
				        conn.connect();
				        final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
				        bm = BitmapFactory.decodeStream(bis);
				        bis.close();
				       
				    } catch (IOException e) {
				        Log.d("DEBUGTAG", "Oh noooz an error...");
				    }
				    return bm;
		     }


		     protected void onPostExecute(Bitmap result) {
		    	 image.setImageBitmap(result);
		     }


		 }
	 } 

	
}

