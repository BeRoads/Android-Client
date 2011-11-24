package com.profete162.WebcamWallonnes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.TabsAdapter;
import com.profete162.WebcamWallonnes.misc.Snippets;

public class CamerasActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	ViewPager mViewPager;
	TabsAdapter mTabsAdapter;
	static double GPS[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ActionBar.Tab tab1 = getSupportActionBar().newTab().setText("Alphabet");
		ActionBar.Tab tab2 = getSupportActionBar().newTab().setText("RŽgions");
		ActionBar.Tab tab3 = getSupportActionBar().newTab().setText("Distance");

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);
		mTabsAdapter.addTab(tab1, SortAlphabet.AppListFragment.class,0);
		mTabsAdapter.addTab(tab2, SortGroup.AppListFragment.class,0);
		mTabsAdapter.addTab(tab3, CamerasActivity.DistanceListFragment.class,0);

		if (savedInstanceState != null) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt("index"));
		}
		
		GPS = Snippets.getLocationFromBundle(this.getIntent().getExtras());
	}
	public static class DistanceListFragment extends ListFragment  {
		String url="";
		ImageView image=null;
		
		static Cursor webcamCursor;
		static DataBaseHelper mDbHelper;
		static SortAlphabet mContext;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			registerForContextMenu(getListView());
			mDbHelper = new DataBaseHelper(getActivity());
			mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
			webcamCursor = mDbHelper.fetchAllWebcamByDistance(GPS);
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					getActivity(), android.R.layout.simple_list_item_1,
					webcamCursor, new String[] { "City" },
					new int[] { android.R.id.text1 });
			this.setListAdapter(adapter);

		}

		@Override
		public void onCreateContextMenu(ContextMenu menu, View v,
				ContextMenuInfo menuInfo) {
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.add(0, 0, 0, "Favori");
		}

		public boolean onContextItemSelected(MenuItem item) {
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();

			int id = (int) menuInfo.id;
			int position = (int) menuInfo.position;
			webcamCursor.moveToPosition(position);

			String name = webcamCursor.getString(webcamCursor
					.getColumnIndex("City"));
			String newName;

			if (name.contains("* "))
				newName = name.replace("* ", "");
			else
				newName = "* " + name;

			mDbHelper.updateWebcam(id, newName);

			webcamCursor = mDbHelper.fetchAllWebcam();

			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					getActivity(), android.R.layout.simple_list_item_1,
					webcamCursor, new String[] { "City" },
					new int[] { android.R.id.text1 });

			this.setListAdapter(adapter);
			return super.onContextItemSelected(item);

		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i("FragmentComplexList", "Item clicked: " + id);
			webcamCursor.moveToPosition(position);
			char cat = webcamCursor.getString(
					webcamCursor.getColumnIndex("Cat")).charAt(0);

			Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(R.layout.custom_dialog);

			int picId = Integer.valueOf(webcamCursor.getString(webcamCursor
					.getColumnIndex("_id")));
			dialog.setTitle(webcamCursor.getString(webcamCursor
					.getColumnIndex("City")));
			
			image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.icon);
			
			 new DownloadPicTask().execute(Snippets.getUrlFromCat(picId, cat));


			dialog.show();
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
