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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItem;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.misc.Snippets;

public class SortAlphabet extends FragmentActivity {
	/** Called when the activity is first created. */
	static Cursor webcamCursor;
	static DataBaseHelper mDbHelper;
	static SortAlphabet mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	public static class AppListFragment extends ListFragment  {
		String url="";
		ImageView image=null;
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

	

}