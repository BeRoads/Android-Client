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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.profete162.WebcamWallonnes.adapter.DataBaseHelper;
import com.profete162.WebcamWallonnes.adapter.SectionedAdapter;
import com.profete162.WebcamWallonnes.misc.Snippets;

public class SortGroup extends FragmentActivity {
	/** Called when the activity is first created. */
	static Cursor webcamCursor;
	static DataBaseHelper mDbHelper;
	static SortGroup mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	public static class AppListFragment extends ListFragment {
		String url = "";
		ImageView image = null;

		private static String[] items = { "Ring sud de Bruxelles",
			"Autoroutes autour de Liège","Mons", "Axe Charleroi - Liège",
			"Axe Bruxelles - Namur", "Charleroi Est", "Charleroi Ouest",
			"Axe Namur - Arlon", "Anvers/Antwerpen", "Bruxelles/Brussels", 
			"Ring Bruxelles/Brussels", "Gand/Gent", "Lummen" };

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		
			mDbHelper = new DataBaseHelper(getActivity());
			mDbHelper.openDataBase(DataBaseHelper.DB_NAME_WEBCAM);
			ArrayList<String> list;
			for (int i = 0; i < items.length ; ++i) {
				
				list = new ArrayList<String>();
				webcamCursor= mDbHelper.fetchAllWebcam(i+1);
				for(webcamCursor.moveToFirst(); webcamCursor.moveToNext(); webcamCursor.isAfterLast()) {
				    // The Cursor is now set to the right position
				    list.add(webcamCursor.getString(webcamCursor
							.getColumnIndex("City")));
				}
				
				adapter.addSection(items[i],
						new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_list_item_1,
							list));
			}

			
			setListAdapter(adapter);
		}

		SectionedAdapter adapter = new SectionedAdapter() {
			@Override
			protected View getHeaderView(String caption, int index,
					View convertView, ViewGroup parent) {
				TextView result = (TextView) convertView;

				if (convertView == null) {
					result = (TextView) getActivity().getLayoutInflater().inflate(R.layout.header, null);
				}

				result.setText(caption);

				return (result);
			}

		};
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			SectionedAdapter adapter = (SectionedAdapter) l.getAdapter();
			String name=""+adapter.getItem(position);
			webcamCursor=mDbHelper.fetchWebcam(name.replace("'","''"));
			
			
			Log.i("FragmentComplexList", "Item clicked: " +adapter.getItem(position));
			webcamCursor.moveToPosition(0);
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
