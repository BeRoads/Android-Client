package com.profete162.WebcamWallonnes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.SectionedAdapter;
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
		ActionBar.Tab tab2 = getSupportActionBar().newTab().setText("Regions");
		// ActionBar.Tab tab3 =
		// getSupportActionBar().newTab().setText("Distance");

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mTabsAdapter = new TabsAdapter(this, getSupportActionBar(), mViewPager);
		mTabsAdapter.addTab(tab1, SortAlphabetFragment.class, 0);
		mTabsAdapter.addTab(tab2, SortGroupFragment.class, 0);
		// mTabsAdapter.addTab(tab3,
		// CamerasActivity.DistanceListFragment.class,0);

		if (savedInstanceState != null) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt("index"));
		}

		GPS = Snippets.getLocationFromBundle(this.getIntent().getExtras());
	}

	public static class DistanceListFragment extends ListFragment {
		String url = "";
		ImageView image = null;

		static Cursor webcamCursor;
		static DataBaseHelper mDbHelper;

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
			// Log.i("FragmentComplexList", "Ze Item clicked: " + id);
			webcamCursor.moveToPosition(position);
			char cat = webcamCursor.getString(
					webcamCursor.getColumnIndex("Cat")).charAt(0);

			Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(R.layout.camera_dialog);

			int picId = Integer.valueOf(webcamCursor.getString(webcamCursor
					.getColumnIndex("_id")));
			dialog.setTitle(webcamCursor.getString(webcamCursor
					.getColumnIndex("City")));

			image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.icon);
			// Log.d("DEBUGTAG", "WTF: "+Snippets.getUrlFromCat(picId, cat));
			new DownloadPicTask().execute(Snippets.getUrlFromCat(picId, cat));

			dialog.show();
		}
		private class DownloadPicTask extends AsyncTask<String, Integer, Bitmap> {
			protected Bitmap doInBackground(String... url) {
				Bitmap bm = null;

				try {
					Log.d("DEBUGTAG", "URL: " + url[0]);
					final URLConnection conn = new URL(url[0]).openConnection();
					conn.connect();
					final BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream());
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

	public static class SortGroupFragment extends ListFragment {
		String url = "";
		static Cursor webcamCursor;
		static DataBaseHelper mDbHelper;
		ImageView image = null;

		private static String[] items = { "Ring sud de Bruxelles",
				"Autoroutes autour de Liege", "Mons", "Axe Charleroi - Liege",
				"Axe Bruxelles - Namur", "Charleroi Est", "Charleroi Ouest",
				"Axe Namur - Arlon", "Anvers/Antwerpen", "Bruxelles/Brussels",
				"Ring Bruxelles/Brussels", "Gand/Gent", "Lummen" };

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mDbHelper = new DataBaseHelper(getActivity());
			mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
			ArrayList<String> list;
			for (int i = 0; i < items.length; ++i) {

				list = new ArrayList<String>();
				webcamCursor = mDbHelper.fetchAllWebcam(i + 1);
				for (webcamCursor.moveToFirst(); webcamCursor.moveToNext(); webcamCursor
						.isAfterLast()) {
					// The Cursor is now set to the right position
					list.add(webcamCursor.getString(webcamCursor
							.getColumnIndex("City")));
				}

				adapter.addSection(items[i], new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						list));
			}

			setListAdapter(adapter);
			mDbHelper.close();
		}

		SectionedAdapter adapter = new SectionedAdapter() {
			@Override
			protected View getHeaderView(String caption, int index,
					View convertView, ViewGroup parent) {
				TextView result = (TextView) convertView;

				if (convertView == null) {
					result = (TextView) getActivity().getLayoutInflater()
							.inflate(R.layout.header, null);
				}

				result.setText(caption);

				return (result);
			}

		};

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			SectionedAdapter adapter = (SectionedAdapter) l.getAdapter();
			String name = "" + adapter.getItem(position);
			mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
			webcamCursor = mDbHelper.fetchWebcam(name.replace("'", "''"));

			Log.i("FragmentComplexList",
					"Item clicked: " + adapter.getItem(position));
			webcamCursor.moveToPosition(0);
			char cat = webcamCursor.getString(
					webcamCursor.getColumnIndex("Cat")).charAt(0);

			Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(R.layout.camera_dialog);

			int picId = Integer.valueOf(webcamCursor.getString(webcamCursor
					.getColumnIndex("_id")));
			dialog.setTitle(webcamCursor.getString(webcamCursor
					.getColumnIndex("City")));

			image = (ImageView) dialog.findViewById(R.id.image);
			image.setImageResource(R.drawable.icon);
			new DownloadPicTask().execute(Snippets.getUrlFromCat(picId, cat));
			mDbHelper.close();
			dialog.show();
		}
		
		private class DownloadPicTask extends AsyncTask<String, Integer, Bitmap> {
			protected Bitmap doInBackground(String... url) {
				Bitmap bm = null;

				try {
					Log.d("DEBUGTAG", "URL: " + url[0]);
					final URLConnection conn = new URL(url[0]).openConnection();
					conn.connect();
					final BufferedInputStream bis = new BufferedInputStream(
							conn.getInputStream());
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

	public static class SortAlphabetFragment extends ListFragment  {
		String url="";
		ImageView image=null;
		static Cursor webcamCursor;
		static DataBaseHelper mDbHelper;
		
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			registerForContextMenu(getListView());
			mDbHelper = new DataBaseHelper(getActivity());
			mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
			webcamCursor = mDbHelper.fetchAllWebcam();
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					getActivity(), android.R.layout.simple_list_item_1,
					webcamCursor, new String[] { "City" },
					new int[] { android.R.id.text1 });
			this.setListAdapter(adapter);
			mDbHelper.close();

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
			dialog.setContentView(R.layout.camera_dialog);

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
					  Log.d("DEBUGTAG", ""+url[0]);
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
